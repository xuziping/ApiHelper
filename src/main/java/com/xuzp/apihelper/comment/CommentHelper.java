package com.xuzp.apihelper.comment;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xuzp.apihelper.utils.ClassHelper;
import com.xuzp.apihelper.utils.Constants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author za-xuzhiping
 * @Date 2017/12/11
 * @Time 18:06
 */
public class CommentHelper {

    private static final Logger log = LoggerFactory.getLogger(CommentHelper.class);

    public static Map<String, List<CommentPair>> commentMap = Maps.newHashMap();

    public static Map<String, CompilationUnit> compilationUnitMap = Maps.newHashMap();

    public static Map<String, NodeList<TypeParameter>> typeParameterMap = Maps.newHashMap();

    public static void preLoadComments(File file) {
        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                Arrays.stream(file.listFiles()).forEach(CommentHelper::preLoadComments);
            } else if (ClassHelper.isJavaFile(file)) {
                try {
                    CompilationUnit compilationUnit = JavaParser.parse(file);
                    List<CommentPair> commentPairs = collectComments(compilationUnit);
                    String className = ClassHelper.getPackageName(compilationUnit) + file.getName().replaceFirst(".java", "");
                    if (CollectionUtils.isNotEmpty(commentPairs)) {
                        log.info("加载注释成功，class: {}", className);
                        commentMap.put(className, commentPairs);
                    }
                    compilationUnitMap.put(className, compilationUnit);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }
    }


    public static Class getReturnClass(Class cls, Method method) {
        CompilationUnit compilationUnit = compilationUnitMap.get(cls.getName());
        MethodDeclaration md = getMatchedMethodDeclaration(cls, method);
        if (md != null) {
            Optional<NodeList<Type>>  nodes =  ((ClassOrInterfaceType) md.getType()).getTypeArguments();
            if (nodes.isPresent()) {
                List<Node> children = nodes.get().get(0).getChildNodes();
                for(int i=0; i< children.size(); i++) {
                    if(children.get(i) instanceof SimpleName) {
                        String clsName = ((SimpleName)children.get(i)).asString();
                        CompilationUnit unit = compilationUnitMap.get(getClassByImportDeclaration(cls,clsName));
                        if (unit != null) {
                            unit.getChildNodes().stream().forEach(y -> {
                                if (y instanceof ClassOrInterfaceDeclaration) {
                                    NodeList<TypeParameter> typeParameters = ((ClassOrInterfaceDeclaration) y).getTypeParameters();
                                    typeParameterMap.put(cls.getName() + "_" + method.getName(), typeParameters);
                                    y.getChildNodes().forEach(p -> {
                                        if (p instanceof FieldDeclaration) {
                                            List<Node> fdList =  ((FieldDeclaration) p).getChildNodes();
                                            for(Node fdn: fdList) {
                                                if (fdn instanceof VariableDeclarator) {
                                                    VariableDeclarator vd = (VariableDeclarator)fdn;

                                                    typeParameterMap.put(cls.getName() + "_" + method.getName() + "_"
                                                            + vd.getNameAsString(), typeParameters);
                                                }
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    } else if (children.get(i)  instanceof ClassOrInterfaceType) {
                        String clsName = getClassByImportDeclaration(cls, ((ClassOrInterfaceType)
                                children.get(i) ).getNameAsString());
                        CompilationUnit unit = compilationUnitMap.get(getClassByImportDeclaration(cls,clsName));

                    }
                }
            }
       }
        return null;
    }

    private static String getClassByImportDeclaration(Class cls, String name){
        StringBuilder sb = new StringBuilder();
        CompilationUnit compilationUnit = compilationUnitMap.get(cls.getName());
        if(compilationUnit!=null && CollectionUtils.isNotEmpty(compilationUnit.getImports())) {
           compilationUnit.getImports().stream().filter(x->x.getName().asString().endsWith(Constants.DOT + name))
                    .findFirst().ifPresent(x -> {
               sb.append(x.getName().asString());
            });
        }
        return sb.toString();
    }

    /**
     * 获取基于java文件的匹配的方法声明
     */
    private static MethodDeclaration getMatchedMethodDeclaration(Class cls, Method method) {
        CompilationUnit compilationUnit = compilationUnitMap.get(cls.getName());
        if (compilationUnit != null) {
            try {
                List<MethodDeclaration> ret = Lists.newArrayList();
                compilationUnit.getChildNodes().stream().filter(x -> x instanceof ClassOrInterfaceDeclaration).findFirst().ifPresent(x -> {
                    x.getChildNodes().stream().filter(xm -> xm instanceof MethodDeclaration).forEach(m -> {
                        MethodDeclaration md = (MethodDeclaration) m;
                        if (md.getNameAsString().equalsIgnoreCase(method.getName())) {
                            Class[] paramTypes = method.getParameterTypes();
                            if (paramTypes == null && md.getTypeParameters() == null) {
                                ret.add(md);
                                return;
                            }

                            if (paramTypes.length == md.getParameters().size()) {
                                for (int i = 0; i < paramTypes.length; i++) {
                                    Parameter typeParameter = md.getParameters().get(i);
                                    if (!typeParameter.getType().asString().equals(paramTypes[i].getSimpleName())) {
                                        log.warn("可能参数不匹配,{}!={}", typeParameter.getType().asString(), paramTypes[i].getSimpleName());
//                                        return;
                                    }
                                }
                                ret.add(md);
                            }
                        }
                    });
                });
                if (ret.size() != 1) {
                    throw new Exception("匹配的方法声明数有问题");
                }
                return ret.get(0);
            } catch (Exception e) {
                log.error("获取方法声明失败，类={}，方法={}", cls.getName(), method.getName());
            }
        }
        return null;
    }

    /**
     * 获取真实参数名
     */
    public static String getParameterName(Class cls, Method method, int index) {
        StringBuilder sb = new StringBuilder();
        MethodDeclaration md = getMatchedMethodDeclaration(cls, method);
        if (md != null) {
            md.getChildNodes().stream().filter(mp -> mp instanceof Parameter)
                    .skip(index).findFirst().ifPresent(mp -> {
                sb.append(((Parameter) mp).getNameAsString());
            });
        }
        return sb.toString();
    }

    public static List<CommentPair> collectComments(CompilationUnit compilationUnit) {
        List<CommentPair> commentPairs = Lists.newArrayList();
        for (Comment comment : compilationUnit.getComments()) {
            comment.getCommentedNode().ifPresent(cNode -> {
                commentPairs.addAll(cNode.getChildNodes().stream().map(
                        node -> {
                            if (node instanceof NormalAnnotationExpr) {
                                return getCommentPair((NormalAnnotationExpr) node);
                            } else if (node instanceof SingleMemberAnnotationExpr) {
                                return getCommentPair((SingleMemberAnnotationExpr) node);
                            } else if (node instanceof VariableDeclarator) {
                                return getCommentPair((VariableDeclarator) node);
                            }
                            return null;
                        }
                ).filter(Objects::nonNull).collect(Collectors.toList()));
            });
        }

        return commentPairs;
    }

    private static CommentPair getCommentPair(VariableDeclarator var) {
        CommentPair commentPair = null;
        String key = var.getNameAsString();
        CommentObj value = getCommentStr(var);
        if (value != null) {
            commentPair = new CommentPair(key, value);
            log.debug("commentPair, key={}, value={}", key, value);
        }
        return commentPair;
    }

    private static CommentObj getCommentStr(Node node) {
        CommentObj result = null;
        if (node.getParentNode().isPresent() && node.getParentNode().get().getComment().isPresent()) {
            String value = node.getParentNode().get().getComment().get().getContent();
            result = getCommentContent(value);
        }
        return result;
    }

    private static CommentPair getCommentPair(SingleMemberAnnotationExpr anno) {
        CommentPair commentPair = null;
        if (Constants.REQUEST_MAPPINGS.contains(anno.getNameAsString())) {
            String key = getKey(anno.getMemberValue());
            key = getCommentKey(key);
            CommentObj commentObj = getCommentStr(anno);
            if (commentObj != null) {
                commentPair = new CommentPair(key, commentObj);
                log.debug("commentPair, key={}, value={}", key, commentObj);
            }
        }
        return commentPair;
    }

    private static CommentPair getCommentPair(NormalAnnotationExpr anno) {
        CommentPair commentPair = null;
        if (Constants.REQUEST_MAPPINGS.contains(anno.getNameAsString())) {
            Optional<MemberValuePair> mvp = anno.getPairs().parallelStream().filter(x -> "value".equals(x.getNameAsString())).findFirst();
            if (mvp.isPresent()) {
                String key = getKey(mvp.get().getValue());
                key = getCommentKey(key);
                String value = anno.getParentNode().get().getComment().get().getContent();
                CommentObj commentObj = getCommentContent(value);
                if (commentObj != null) {
                    commentPair = new CommentPair(key, commentObj);
                    log.debug("commentPair, key={}, value={}", key, value);
                }
            }
        }
        return commentPair;
    }

    private static String getKey(Expression expression) {
        String key = null;
        if (expression instanceof ArrayInitializerExpr) {
            key = expression.asArrayInitializerExpr().getValues().get(0).asStringLiteralExpr().asString();
        } else {
            key = expression.asStringLiteralExpr().asString();
        }
        return key;
    }

    private static CommentObj getCommentContent(String content) {
        if (StringUtils.isEmpty(content)) {
            return null;
        }
        String[] lines = content.split("\r\n");
        if (lines.length == 1) {
            lines = content.split("\n");
        }
        StringBuilder shortSb = new StringBuilder();
        StringBuilder wholeSb = new StringBuilder();
        for (String line : lines) {
            line = line.replaceAll("\\*", "").replaceAll("<[.[^>]]*>", "").trim();
            if (line.startsWith("@")) {
                // 忽略所有 @ 之后的注释
                break;
            }
            if (shortSb.length() == 0) {
                // 只取首行注释
                shortSb.append(line);
            }
            wholeSb.append(line);
        }
        if (StringUtils.isEmpty(shortSb.toString()) || StringUtils.isEmpty(wholeSb.toString())) {
            return null;
        }
        return new CommentObj(shortSb.toString(), wholeSb.toString());
    }

    private static String getCommentKey(String key) {
        if (key.startsWith("/")) {
            return key.substring(1);
        }
        return key;
    }

    public static CommentObj getComment(Class cls, String key) {
        String ckey = getCommentKey(key);
        List<CommentPair> comments = commentMap.get(cls.getName());
        CommentObj result = null;
        if (CollectionUtils.isNotEmpty(comments)) {
            Optional<CommentPair> comment = comments.stream().filter(x -> x.getKey().equals(ckey)).findFirst();
            if (comment.isPresent()) {
                result = comment.get().getComment();
            }
        }
        if (result == null && cls.getSuperclass() != null) {
            return getComment(cls.getSuperclass(), ckey);
        }
        return result;
    }

    public static String getEnumInfo(String typeName) {
        StringBuilder sb = new StringBuilder();
        try {
            Class enumClass = Class.forName(typeName);
            Method getDescMethod = enumClass.getDeclaredMethod("getDesc");
            Object[] enumObjs = enumClass.getEnumConstants();

            if (null != enumObjs) {
                for (Object enumObj : enumObjs) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append(enumObj.toString());
                    if (getDescMethod != null) {
                        sb.append(" ").append((String) getDescMethod.invoke(enumObj));
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取Enum信息出错, typeName={}", typeName);
            return "";
        }
        return "(" + sb.toString() + ")";
    }

    public static boolean isOptionalParam(CommentObj comment) {
        if (comment == null || StringUtils.isEmpty(comment.getWholeComment())) {
            return false;
        }
        if (comment.getWholeComment().contains("不需要前端提供")
                || comment.getWholeComment().contains("非必须") ||
                comment.getWholeComment().contains("非必需")) {
            return true;
        }
        return false;
    }
}

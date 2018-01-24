package com.xuzp.apihelper.comment;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.*;
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

    /**
     * 获取参数名称
     */
    public static String getParameterName(Class cls, Method method) {
        StringBuilder sb = new StringBuilder();
        CompilationUnit compilationUnit = compilationUnitMap.get(cls.getName());
        if (compilationUnit != null) {
            try {
                compilationUnit.getChildNodes().stream().filter(x -> x instanceof ClassOrInterfaceDeclaration).findFirst().ifPresent(x -> {
                            x.getChildNodes().stream().filter(xm -> xm instanceof MethodDeclaration).forEach(m -> {
                                if (((MethodDeclaration) m).getNameAsString().equalsIgnoreCase(method.getName())) {
                                    m.getChildNodes().stream().filter(mp -> mp instanceof Parameter).findFirst().ifPresent(mp -> {
                                        sb.append(((Parameter) mp).getNameAsString());
                                    });
                                }
                            });
                        }
                );
            } catch (Exception e) {
                log.error("获取真实参数名失败，类={}，方法={}", cls.getName(), method.getName());
            }
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
        return key.replaceFirst("/", "");
    }

    public static CommentObj getComment(Class cls, String key) {
        List<CommentPair> comments = commentMap.get(cls.getName());
        CommentObj result = null;
        if (CollectionUtils.isNotEmpty(comments)) {
            Optional<CommentPair> comment = comments.stream().filter(x -> x.getKey().equals(key)).findFirst();
            if (comment.isPresent()) {
                result = comment.get().getComment();
            }
        }
        if (result == null && cls.getSuperclass() != null) {
            return getComment(cls.getSuperclass(), key);
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

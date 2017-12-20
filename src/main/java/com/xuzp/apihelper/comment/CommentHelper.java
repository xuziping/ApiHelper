package com.xuzp.apihelper.comment;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xuzp.apihelper.utils.ClassHelper;
import com.xuzp.apihelper.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author za-xuzhiping
 * @Date 2017/12/11
 * @Time 18:06
 */
@Slf4j
public class CommentHelper {

    public static Map<String, List<CommentPair>> commentMap = Maps.newHashMap();

    public static void preLoadComments(File file) {
        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                Arrays.stream(file.listFiles()).forEach(CommentHelper::preLoadComments);
            } else if(ClassHelper.isJavaFile(file)){
                try {
                    CompilationUnit compilationUnit = JavaParser.parse(file);
                    List<CommentPair> commentPairs = collectComments(compilationUnit);
                    if (CollectionUtils.isNotEmpty(commentPairs)) {
                        String className = ClassHelper.getPackageName(compilationUnit) + file.getName().replaceFirst(".java", "");
                        log.info("加载注释成功，class: {}", className);
                        CommentHelper.commentMap.put(className, commentPairs);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }
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
        String value = getCommentStr(var);
        if (StringUtils.isNoneBlank(value)) {
            commentPair = new CommentPair(key, value);
            log.info("commentPair={}", commentPair);
        }
        return commentPair;
    }

    private static String getCommentStr(Node node) {
        String result = null;
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
            String value = getCommentStr(anno);
            if (StringUtils.isNoneBlank(value)) {
                commentPair = new CommentPair(key, value);
                log.info("commentPair={}", commentPair);
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
                value = getCommentContent(value);
                if (StringUtils.isNoneBlank(value)) {
                    commentPair = new CommentPair(key, value);
                    log.info("commentPair={}", commentPair);
                }
            }
        }
        return commentPair;
    }

    private static String getKey(Expression expression){
        String key = null;
        if (expression instanceof ArrayInitializerExpr) {
            key = expression.asArrayInitializerExpr().getValues().get(0).asStringLiteralExpr().asString();
        } else {
            key = expression.asStringLiteralExpr().asString();
        }
        return key;
    }

    private static String getCommentContent(String content) {
        if(StringUtils.isEmpty(content) || content.trim().startsWith(Constants.COMMENT_MARK)) {
            // 略过之前的apidoc注释
            return "";
        }
        String[] lines = content.split("\r\n");
        if(lines.length == 1) {
            lines = content.split("\n");
        }
        StringBuilder sb = new StringBuilder();
        for(String line : lines) {
            line = line.replaceFirst("\\*", "").replaceAll("<[.[^>]]*>","").trim();
            if (line.startsWith("@")) {
                // 忽略所有 @ 之后的注释
                break;
            }
            if(sb.length()>0) {
                // 只取首行注释
                break;
            }
            sb.append(line);
        }
        return sb.toString();
    }

    private static String getCommentKey(String key) {
        return key.replaceFirst("/", "");
    }

    public static String getComment(Class cls, String key) {
        List<CommentPair> comments = commentMap.get(cls.getName());
        String result = "";
        if (CollectionUtils.isNotEmpty(comments)) {
            Optional<CommentPair> comment = comments.stream().filter(x -> x.getKey().equals(key)).findFirst();
            result = comment.isPresent() ? comment.get().getCommnent() : "";
        }
        if (StringUtils.isEmpty(result) && cls.getSuperclass() != null) {
            return getComment(cls.getSuperclass(), key);
        }
        return result;
    }

    public static String getEnumInfo(String typeName){
        StringBuilder sb = new StringBuilder();
        try {
            Class enumClass = Class.forName(typeName);
            Method getDescMethod = enumClass.getDeclaredMethod("getDesc");
            Object[] enumObjs = enumClass.getEnumConstants();

            if (null != enumObjs) {
                for(Object enumObj: enumObjs) {
                    if(sb.length()>0) {
                        sb.append(", ");
                    }
                    sb.append(enumObj.toString());
                    if (getDescMethod!=null) {
                        sb.append(" ").append((String)getDescMethod.invoke(enumObj));
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取Enum信息出错, typeName={}", typeName);
            return "";
        }
        return "(" + sb.toString() + ")";
    }
}

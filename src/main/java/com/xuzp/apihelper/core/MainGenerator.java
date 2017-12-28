package com.xuzp.apihelper.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.xuzp.apihelper.comment.CommentHelper;
import com.xuzp.apihelper.properties.ApiHelperProperties;
import com.xuzp.apihelper.properties.LoadProperties;
import com.xuzp.apihelper.template.apidoc.ApiDocTemplate;
import com.xuzp.apihelper.template.help.Help;
import com.xuzp.apihelper.template.markdown.MarkdownTemplate;
import com.xuzp.apihelper.template.postman.PostmanTemplate;
import com.xuzp.apihelper.utils.ClassHelper;
import com.xuzp.apihelper.utils.Constants;
import com.xuzp.apihelper.utils.TypeHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.xuzp.apihelper.utils.Constants.*;
import static com.xuzp.apihelper.utils.TypeHelper.fixTypeName;

/**
 * @author za-xuzhiping
 * @Date 2017/11/17
 * @Time 11:34
 */
public class MainGenerator {

    private static final Logger log = LoggerFactory.getLogger(MainGenerator.class);

    public static void main(String[] args) {

        ApiHelperProperties properties = LoadProperties.getProperties();
        ClassHelper.loadClassPath(properties.getClassPath());
        String moduleName = properties.getModuleName();
        Set<Class> clsSet = ClassHelper.loadServiceClasses(properties.getServicePath());
        if (CollectionUtils.isNotEmpty(clsSet)) {
            if (StringUtils.isNotEmpty(properties.getCommentPath())) {
                for (String commentPath : properties.getCommentPath().split(Constants.SPLIT_MARK)) {
                    if (StringUtils.isNotEmpty(commentPath)) {
                        CommentHelper.preLoadComments(FileUtils.getFile(commentPath));
                    }
                }
            }

            PostmanTemplate postmanTemplate = new PostmanTemplate(moduleName);
            MainGenerator generator = new MainGenerator();
            clsSet.stream().filter(Objects::nonNull).forEach(cls -> {
                try {
                    List<MethodApiObj> apiList = generator.parse(cls);
                    generateApiDocFile(apiList, cls, properties.getOutputPath());
                    generateMarkdownFile(apiList, cls, properties.getOutputPath());
                    apiList.stream().forEach(api -> {
                        postmanTemplate.add(api);
                    });
                } catch (Exception e) {
                    log.error("解析类 {} 失败, 异常: {}", cls.getName(), e.getMessage());
                    e.printStackTrace();
                }
            });
            try {
                generatePostmanFile(properties.getOutputPath(), postmanTemplate);
            } catch (Exception e) {
                log.error("生成 postman.json 失败, 异常: {}", e.getMessage());
            }

        }
        log.info("完成");
        String msg = Help.message();
        if (StringUtils.isNotEmpty(msg)) {
            log.info(Help.message());
        }
    }

    private static void generateApiDocFile(List<MethodApiObj> apiList, Class cls, String outputPath) throws Exception {
        StringBuilder apiDocContent = new StringBuilder();
        apiList.stream().forEach(api -> {
            String apiDoc = new ApiDocTemplate(api).getContent();
            if (StringUtils.isNotEmpty(apiDoc)) {
                log.info(apiDoc);
                apiDocContent.append(apiDoc).append(LF).append(LF);
            }
        });
        File apiDocFolder = new File(outputPath, Constants.API_DOC_FOLDER);
        if (apiDocContent.length() > 0) {
            File outFile = new File(apiDocFolder, cls.getSimpleName() + TEXT_FILE_SUFFIX);
            FileUtils.writeStringToFile(outFile, apiDocContent.toString(), ENCODING);
            log.info("生成apidoc写入{}成功", outFile.getAbsolutePath());
        }
    }

    private static void generateMarkdownFile(List<MethodApiObj> apiList, Class cls, String outputPath) throws Exception {
        StringBuilder markdownContent = new StringBuilder();
        apiList.stream().forEach(api -> {
            String markdown = new MarkdownTemplate(api).getContent();
            if (StringUtils.isNotEmpty(markdown)) {
                log.info(markdown);
                markdownContent.append(markdown).append(LF).append(LF);
            }
        });
        File markdownFolder = new File(outputPath, Constants.MARKDOWN_FOLDER);
        if (markdownContent.length() > 0) {
            File outFile = new File(markdownFolder, cls.getSimpleName() + MARKDOWN_FILE_SUFFIX);
            FileUtils.writeStringToFile(outFile, markdownContent.toString(), ENCODING);
            log.info("生成markdown写入{}成功", outFile.getAbsolutePath());
        }
    }

    private static void generatePostmanFile(String outputPath, PostmanTemplate postmanTemplate) throws Exception {
        File postmanFolder = FileUtils.getFile(outputPath, Constants.POSTMAN_FOLDER);
        if (postmanTemplate.hasData()) {
            String postmanContent = postmanTemplate.getContent();
            File outFile = FileUtils.getFile(postmanFolder, "postman" + JSON_FILE_SUFFIX);
            FileUtils.writeStringToFile(outFile, postmanContent, ENCODING);
            log.info("生成postman写入{}成功", outFile.getAbsolutePath());
        }
    }

    /**
     * 解析生成ApiDoc的核心方法
     */
    private List<MethodApiObj> parse(Class cls) throws Exception {
        String apiGroup = getApiGroupName(cls);
        List<MethodApiObj> methodApiObjs = Lists.newArrayList();
        if (apiGroup == null) {
            log.warn("忽略没有实现RequestMapping注解的类：{}", cls.getSimpleName());
            return methodApiObjs;
        }
        log.info("######## 正在解析 {} ########", cls.getSimpleName());
        log.debug("组名: {}", apiGroup);
        Method[] methods = cls.getMethods();

        if (methods != null) {
            for (Method method : methods) {
                log.info("#######################");
                MethodApiObj api = new MethodApiObj();
                api.setGroup(apiGroup);

                String methodName = method.getName();
                log.debug("处理方法: {}", methodName);

                Annotation requestMappingAnnotation = method.getAnnotation(RequestMapping.class);
                String apimethod = getApiMethod(requestMappingAnnotation);
                log.debug("访问方式: {}", apimethod);
                api.setApiMethod(apimethod);

                String path = getRequestMappingAnnotationValue(requestMappingAnnotation);
                if (path == null) {
                    log.warn("无法获取方法上的RequestMapping，忽略 {}", methodName);
                    continue;
                }
                log.debug("路径: {}", path);
                api.setPath(path);
                api.setName(apiGroup + File.separator + path);

                String description = CommentHelper.getComment(cls, path);
                description = description != null ? description : "";
                log.debug("作用: {}", description);
                api.setDesc(description);

                if (StringUtils.isNotEmpty(description)) {
                    api.setLabelName(description);
                } else {
                    api.setLabelName(methodName);
                }
                log.debug("菜单名: {}", api.getLabelName());

                if (method.getParameterTypes() != null && method.getParameterTypes().length > 0) {
                    Parameter parameter = method.getParameters()[0];
                    log.debug("参数值: {} - {}", parameter.getParameterizedType(), parameter.getName());
                    List<Param> params = collectTypeInfo(parameter.getParameterizedType(), 0, true);
                    if (CollectionUtils.isNotEmpty(params)) {
                        String paramName = CommentHelper.getParameterName(cls, method);
                        params.stream().filter(x -> StringUtils.isBlank(x.getName())).forEach(x -> {
                            x.setName(StringUtils.isNotEmpty(paramName) ? paramName: parameter.getName());
                        });
                    }
                    api.setParams(params);
                } else {
                    log.debug("没有参数");
                }

                // 支持泛型ResultBase<T> 和 void及基础类型
                Type returnType = TypeHelper.isVoid(method.getReturnType()) ||
                        TypeHelper.isBasicType(method.getReturnType()) ? method.getReturnType() :
                        ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
                if (TypeHelper.isBasicType(returnType) ||
                        TypeHelper.isVoid(returnType)) {
                    log.debug("返回值: 基础数据类型 {}", returnType.getTypeName());
                    Param param = new Param(returnType, null, null, null, null);
                    param.setBasicType(true);
                    api.setReturns(Lists.newArrayList(param));
                    api.setIsCollectionReturnType(Boolean.FALSE);
                } else {
                    log.debug("返回值: {}", returnType.getTypeName());
                    List<Param> returns = collectTypeInfo(returnType, 0, false);
                    api.setReturns(returns);
                    api.setIsCollectionReturnType(returnType instanceof ParameterizedTypeImpl
                            && TypeHelper.isCollection(((ParameterizedTypeImpl) returnType).getRawType()));
                }
                methodApiObjs.add(api);
            }
        }
        return methodApiObjs;
    }

    /**
     * 收集参数类型。对于List包装的非基本类型，递归收集
     */
    private List<Param> collectTypeInfo(Type type, int level, boolean isParam) throws Exception {
        if (level > MAX_RECURSION) {
            log.warn("递归达到极限值，怀疑参数循环引用");
            return Lists.newArrayList(
                    new Param(String.class, fixTypeName(type.getTypeName()), "怀疑参数循环引用", "怀疑参数循环引用", null));
        }

        if (type instanceof ParameterizedType) {
            Class cls = (((ParameterizedTypeImpl) type).getRawType());
            if (TypeHelper.isCollection(cls)) {
                Type child = ((ParameterizedTypeImpl) type).getActualTypeArguments()[0];
                return collectTypeInfo(child, level + 1, isParam);
            }
        }
        if (TypeHelper.isMultipartFile(type)) {
            return Lists.newArrayList(
                    new Param(type, "", "上传文件", null, null));
        }
        if (TypeHelper.isBasicType(type)) {
            return Lists.newArrayList(
                    new Param(type, "", "", null, null));
        }
        Class cls = null;
        try {
            String typeName = type.getTypeName();
            int i = typeName.indexOf("<");
            if (i != -1) {
                typeName = typeName.substring(0, i);
            }
            cls = Class.forName(typeName);
        } catch (ClassNotFoundException e) {
            log.error("Not found class: {}", type.getTypeName());
            cls = Object.class;
        }

        if (TypeHelper.isPagableType(cls)) {
            try {
                int start = type.getTypeName().indexOf("<");
                if (start != -1) {
                    String containedType = type.getTypeName().substring(start + 1, type.getTypeName().length() - 1);
                    Class containedClass = Class.forName(containedType);
                    return getPagableParams(containedClass);
                }
            } catch (Exception e) {
                log.error("Not get pagable class: {}", type.getTypeName());
            }
            return getPagableParams(null);
        }

        if (!TypeHelper.isVoid(cls)) {
            Set<Field> fields = Sets.newHashSet();
            TypeHelper.getAllFields(cls, fields);
            List<Param> params = Lists.newArrayList();
            for (Field field : fields) {
                field.setAccessible(true);
                /** 忽略静态字段 */
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                Param param = new Param();
                param.setName(field.getName());
                param.setType(field.getGenericType());
                param.setDesc(CommentHelper.getComment(cls, field.getName()));
                params.add(param);
                log.debug(field.getGenericType().getTypeName() + " - " + field.getName());

                /** 处理集合包装类型字段，如List等 */
                if (TypeHelper.isCollection(field.getType())) {
                    Type childType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                    if (!TypeHelper.isBasicType(childType)) {
                        List<Param> children = collectTypeInfo(childType, level + 1, isParam);
                        param.setChildren(children);
                    }
                }
                /** 处理Map类型字段 */
//                else if (TypeHelper.isMap(field.getType())) {
//                    Type keyType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
//                    Type valueType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[1];
//                }
                /** 处理翻页包装类字段，本项目中包装类是 Page */
                else if (TypeHelper.isPagableType(field.getType())) {
                    if (isParam) {
                        param.setChildren(getPagableParams(field.getType()));
                    } else {
                        param.setChildren(getPagableReturns());
                        Type childType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                        List<Param> children = collectTypeInfo(childType, level + 1, isParam);
                        param.getChildren().addAll(children);
                    }
                }
                /** 处理非枚举类型字段以及非基本类型字段的自定义对象字段 */
                else if (!TypeHelper.isEnumType(field.getType()) && !TypeHelper.isBasicType(field.getGenericType())) {
                    List<Param> children = collectTypeInfo(field.getGenericType(), level + 1, isParam);
                    param.setChildren(children);
                    if (CollectionUtils.isNotEmpty(children)) {
                        param.setType(Object.class);
                    }
                } else if (TypeHelper.isEnumType(field.getGenericType())) {
                    String enumInfo = CommentHelper.getEnumInfo(field.getGenericType().getTypeName());
                    if (StringUtils.isNotEmpty(enumInfo)) {
                        param.setDesc(param.getDesc() + " " + enumInfo);
                    }
                }
            }
            return params;
        }
        return null;
    }

    /**
     * 组装翻页请求参数
     */
    protected List<Param> getPagableParams(Class cls) throws Exception {
        List<Param> params = Lists.newArrayList(new Param(Integer.class, "start", "起始记录", "1", null),
                new Param(Integer.class, "pageIndex", "第几页", "1", null),
                new Param(Integer.class, "limit", "最大查询数", "20", null)
        );
        if (cls != null) {
            Param condition = new Param(Object.class, "condition", "查询条件", null,
                    collectTypeInfo(cls, 1, true));
            params.add(condition);
        }
        return params;
    }

    /**
     * 组装翻页返回参数
     */
    protected List<Param> getPagableReturns() {
        return Lists.newArrayList(new Param(Integer.class, "total", "总记录数", "32", null));
    }

    /**
     * 取注释RequestMapping的值
     */
    private String getRequestMappingAnnotationValue(Annotation annotation) {
        String value = null;
        if (annotation != null) {
            RequestMapping requestMapping = ((RequestMapping) annotation);
            if (requestMapping.value() != null) {
                value = requestMapping.value()[0];
                if (value.startsWith(URL_SPLIT)) {
                    value = value.substring(1);
                }
                if (value.endsWith(URL_SPLIT)) {
                    value = value.substring(0, value.length() - 1);
                }
            }
        }
        return value;
    }

    private String getApiMethod(Annotation annotation) {
        String value = "POST";
        if (annotation != null) {
            RequestMapping requestMapping = ((RequestMapping) annotation);
            if (requestMapping.method() != null && requestMapping.method().length == 1) {
                value = String.format("%s", requestMapping.method()[0].name());
            }
        }
        return value;
    }

    /**
     * 获取ApiGroup名
     */
    private String getApiGroupName(Class cls) {
        Annotation annotation = cls.getAnnotation(RequestMapping.class);
        return getRequestMappingAnnotationValue(annotation);
    }
}

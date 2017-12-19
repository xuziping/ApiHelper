package com.xuzp.apidoc.properties;

import com.xuzp.apidoc.utils.Constants;
import lombok.Data;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * @author za-xuzhiping
 * @Date 2017/12/13
 * @Time 15:49
 */
@Data
public class ApiDocProperties {

    /**
     * 要扫描的可能存在注释的文件目录，支持多目录，以分号分隔
     */
    private String commentPath;

    /**
     * 存放最终生成文档的目录
     */
    private String outputPath;

    /**
     * 项目模块路径
     */
    private String modulePath;

    /**
     * 接口和出参入参以及依赖的类路径
     */
    private String classPath;

    /**
     * 要解析的接口目录，会对该目录下的实现RequestMapping的方法生成文档
     */
    private String servicePath;

    /**
     * 请求地址
     */
    private String requestURL;

    /**
     * 翻页包装类类名
     */
    private String pagableClassName;

    public String getServicePath(){
        if (StringUtils.isNoneBlank(servicePath)) {
            return servicePath;
        }
        return modulePath + File.separator + "src" + File.separator + "main" + File.separator + "java";
    }

    public String getCommentPath(){
        String moduleCommentPath = null;
        if (StringUtils.isNotEmpty(modulePath)) {
            moduleCommentPath = modulePath + File.separator + "src" + File.separator + "main" + File.separator + "java";
        }
        if (StringUtils.isNoneBlank(commentPath)) {
            if(StringUtils.isNotEmpty(moduleCommentPath)) {
                return commentPath + ";" + moduleCommentPath;
            }
            return commentPath;
        }
        return moduleCommentPath;
    }

    public String getClassPath(){
        String moduleClassPath = "";
        if(StringUtils.isNotEmpty(modulePath)) {
            moduleClassPath = modulePath+ File.separator+ "target" + File.separator + "classes;";
        }
        return StringUtils.isEmpty(classPath) ? moduleClassPath : moduleClassPath + classPath;
    }

    public String  getOutputPath(){
        if (StringUtils.isNotEmpty(outputPath)) {
            return outputPath;
        }
        return Constants.DEFAULT_OUTPUT_FOLDER;
    }

    public String getModuleName(){
        String moduleName = getModulePath();
        if (moduleName == null) {
            moduleName = Constants.DEFAULT_MODULE_NAME;
        } else {
            moduleName = FilenameUtils.getName(moduleName);
        }
        return moduleName;
    }
}

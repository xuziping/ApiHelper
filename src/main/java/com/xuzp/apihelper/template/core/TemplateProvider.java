package com.xuzp.apihelper.template.core;

import com.google.common.collect.Lists;
import com.xuzp.apihelper.properties.LoadProperties;
import com.xuzp.apihelper.utils.Constants;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author XuZiPing
 * @Date 2017/12/26
 * @Time 23:34
 */
public class TemplateProvider {

    private static final Logger log = LoggerFactory.getLogger(TemplateProvider.class);

    private static Configuration cfg = null;

    static {
        cfg = new Configuration(Configuration.VERSION_2_3_23);
        cfg.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_23));
        cfg.setDefaultEncoding(Constants.ENCODING);
        StringTemplateLoader loader = new StringTemplateLoader();
        Lists.newArrayList(Constants.APIDOC_TEMPLATE_FTL,
                Constants.MARKDOWN_TEMPLATE_FTL, Constants.RESPONSE_JSON_FTL, Constants.HELP_FTL)
                .stream().forEach(templateName -> {
            String content = getUserDefinedTemplateContent(templateName);
            if (StringUtils.isEmpty(content)) {
                content = getDefaultTemplateContent(templateName);
            }
            loader.putTemplate(templateName, content);
        });
        cfg.setTemplateLoader(loader);
    }

    private static String getDefaultTemplateContent(String templateName) {
        try (InputStream in = LoadProperties.class.getClassLoader().getResourceAsStream(Constants.TEMPLATE_DEFAULT_PATH
                + "/" + templateName);) {
            return IOUtils.toString(in, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("加载默认模板失败，模板名={},异常={}", templateName, e.getMessage());
        }
        return null;
    }

    private static String getUserDefinedTemplateContent(String templateName) {
        String path = LoadProperties.getProperties().getTemplatePath();
        if (StringUtils.isNotBlank(path)) {
            File templatePath = new File(path);
            if (templatePath.exists() && templatePath.isDirectory()) {
                File templateFile = new File(templatePath, templateName);
                if (templateFile.exists() && templateFile.isFile()) {
                    try {
                        return FileUtils.readFileToString(templateFile);
                    } catch (Exception e) {
                        log.error("加载用户自定义模板失败，模板名={}，异常={}", templateName, e.getMessage());
                    }
                }
            }
        }
        return null;
    }

    public static String loadTemplate(String templateName, Map<String, Object> params) {
        try {
            Template template = cfg.getTemplate(templateName);
            StringWriter out = new StringWriter();
            template.process(params, out);
            return out.toString();
        } catch (Exception e) {
            log.error("获取模板内容失败, 模板={}，参数={}，异常={}", templateName, params, e);
            return null;
        }
    }
}

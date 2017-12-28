package com.xuzp.apihelper.utils;

import com.xuzp.apihelper.properties.LoadProperties;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.StringWriter;
import java.util.Map;

/**
 * @author XuZiPing
 * @Date 2017/12/26
 * @Time 23:34
 */
public class TemplateProvider {

    private static final Logger log = LoggerFactory.getLogger(TemplateProvider.class);

    public static String loadTemplate(String templateName, Map<String, String> params) {
        try {
            String path = LoadProperties.getProperties().getTemplatePath();
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
            cfg.setDirectoryForTemplateLoading(new File(path));
            cfg.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_23));
            cfg.setDefaultEncoding(Constants.ENCODING);
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

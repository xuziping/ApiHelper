package com.xuzp.apidoc.properties;

import com.xuzp.apidoc.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author za-xuzhiping
 * @Date 2017/12/12
 * @Time 18:19
 */
@Slf4j
public class LoadProperties {

    private static ApiDocProperties apiDocProperties = null;


    static {
        loadProps();
    }

    private synchronized static boolean loadProps() {
        boolean ret = false;
        try (InputStream in = LoadProperties.class.getClassLoader().getResourceAsStream(Constants.PROPERTY_FILE);) {
            Properties props = new Properties();
            props.load(in);
            if (apiDocProperties == null) {
                apiDocProperties = new ApiDocProperties();
                apiDocProperties.setClassPath(getProperty(props, Constants.CLASS_PATH, ""));
                apiDocProperties.setCommentPath(getProperty(props, Constants.COMMENT_PATH, ""));
                apiDocProperties.setOutputPath(getProperty(props, Constants.OUTPUT_PATH, Constants.DEFAULT_OUTPUT_FOLDER));
                apiDocProperties.setServicePath(getProperty(props, Constants.SERVICE_PATH, ""));
                apiDocProperties.setModulePath(getProperty(props, Constants.MODULE_PATH, ""));
                apiDocProperties.setRequestURL(getProperty(props, Constants.REQUEST_URL, Constants.DEFAULT_REQUEST_URL));
                apiDocProperties.setPagableClassName(getProperty(props, Constants.PAGABLE_CLASS_NAME, Constants.DEFAULT_PAGABLE_CLASS_NAME));
            }

            ret = true;
            log.info("配置文件加载成功：{}", apiDocProperties);
        } catch (Exception e) {
            log.error("解析配置文件{}出错", Constants.PROPERTY_FILE);
        }
        return ret;
    }

    private static String getProperty(Properties props, String propertyName, String defaultValue) {
        String value = props.getProperty(propertyName, defaultValue);

        if (StringUtils.isEmpty(value)) {
            log.warn("没有设置{}", propertyName);
        }

        return value.trim();
    }

    public static ApiDocProperties  getProperties() {
        if (null == apiDocProperties) {
            loadProps();
        }
        return apiDocProperties;
    }
}

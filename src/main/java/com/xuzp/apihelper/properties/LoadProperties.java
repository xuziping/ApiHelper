package com.xuzp.apihelper.properties;

import com.xuzp.apihelper.utils.Constants;
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

    private static ApiHelperProperties apiHelperProperties = null;


    static {
        loadProps();
    }

    private synchronized static boolean loadProps() {
        boolean ret = false;
        try (InputStream in = LoadProperties.class.getClassLoader().getResourceAsStream(Constants.PROPERTY_FILE);) {
            Properties props = new Properties();
            props.load(in);
            if (apiHelperProperties == null) {
                apiHelperProperties = new ApiHelperProperties();
                apiHelperProperties.setClassPath(getProperty(props, Constants.CLASS_PATH, ""));
                apiHelperProperties.setCommentPath(getProperty(props, Constants.COMMENT_PATH, ""));
                apiHelperProperties.setOutputPath(getProperty(props, Constants.OUTPUT_PATH, Constants.DEFAULT_OUTPUT_FOLDER));
                apiHelperProperties.setServicePath(getProperty(props, Constants.SERVICE_PATH, ""));
                apiHelperProperties.setModulePath(getProperty(props, Constants.MODULE_PATH, ""));
                apiHelperProperties.setRequestURL(getProperty(props, Constants.REQUEST_URL, Constants.DEFAULT_REQUEST_URL));
                apiHelperProperties.setPagableClassName(getProperty(props, Constants.PAGABLE_CLASS_NAME, Constants.DEFAULT_PAGABLE_CLASS_NAME));
            }

            ret = true;
            log.info("配置文件加载成功：{}", apiHelperProperties);
        } catch (Exception e) {
            log.error("解析配置文件{}出错, 异常={}", Constants.PROPERTY_FILE, e);
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

    public static ApiHelperProperties getProperties() {
        if (null == apiHelperProperties) {
            loadProps();
        }
        return apiHelperProperties;
    }
}

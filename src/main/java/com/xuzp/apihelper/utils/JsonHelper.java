package com.xuzp.apihelper.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author XuZiPing
 * @Date 2017/12/19
 * @Time 22:51
 */
public class JsonHelper {

    private static final Logger log = LoggerFactory.getLogger(JsonHelper.class);

    public static String beautify(String content){
        if(StringUtils.isNoneEmpty(content)) {
            try {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonParser jsonPar = new JsonParser();
                JsonElement jsonEl = jsonPar.parse(content);
                return gson.toJson(jsonEl);
            } catch(Exception e) {
                log.warn("格式化Json失败, 内容={}, 异常={}", content, e.getMessage());
                return content;
            }
        }
        return "";
    }

}

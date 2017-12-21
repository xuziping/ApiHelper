package com.xuzp.apihelper.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author XuZiPing
 * @Date 2017/12/19
 * @Time 22:51
 */
@Slf4j
public class JsonHelper {

    public static String beautify(String content){
        if(StringUtils.isNoneEmpty(content)) {
            try {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonParser jsonPar = new JsonParser();
                JsonElement jsonEl = jsonPar.parse(content);
                return gson.toJson(jsonEl);
            } catch(Exception e) {
                log.warn("美化Json失败, 内容={}", content);
                return content;
            }
        }
        return "";
    }

}

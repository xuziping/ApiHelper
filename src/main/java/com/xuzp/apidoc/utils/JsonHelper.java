package com.xuzp.apidoc.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;

/**
 * @author XuZiPing
 * @Date 2017/12/19
 * @Time 22:51
 */
public class JsonHelper {

    public static String beautify(String content){
        if(StringUtils.isNoneEmpty(content)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonParser jsonPar = new JsonParser();
            JsonElement jsonEl = jsonPar.parse(content);
            String prettyJson = gson.toJson(jsonEl);
            return prettyJson;
        }
        return "";
    }

}

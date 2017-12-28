package com.xuzp.apihelper.template.postman.enums;

/**
 * @author XuZiPing
 * @Date 2017/12/16
 * @Time 17:36
 */
public enum BodyModeEnum {

    /**
     * JSON 类型
     */
    RAW("raw"),

    /**
     * FORM 类型，目前不支持
     */
    FOARM_DATA("formdata");

    private String value;

    private BodyModeEnum (String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}

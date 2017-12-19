package com.xuzp.apihelper.template.postman.enums;

/**
 * @author XuZiPing
 * @Date 2017/12/16
 * @Time 17:34
 */
public enum ContentTypeEnum {

    /**
     * JSON 类型
     */
    JSON("application/json"),

    /**
     * FORM 类型
     */
    FORM_DATA("multipart/form-data"),
    ;

    private String value;

    private ContentTypeEnum (String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}

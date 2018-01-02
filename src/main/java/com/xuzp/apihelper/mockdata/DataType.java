package com.xuzp.apihelper.mockdata;

/**
 * @author za-xuzhiping
 * @Date 2017/12/29
 * @Time 16:11
 */
public enum DataType {

    /**
     * 字符串
     */
    STRING("String"),

    /**
     * 日期
     */
    DATE("Date"),

    /**
     * Long或long
     */
    LONG("Long"),

    /**
     * Integer或int
     */
    INTEGER("Integer"),

    /**
     * Boolean或boolean
     */
    BOOLEAN("Boolean"),

    /**
     * 字符串列表
     */
    LIST_STRING("List<String>"),

    /**
     * Integer列表
     */
    LIST_INTEGER("List<Integer>"),

    /**
     * Long列表
     */
    LIST_LONG("List<Long>"),

    /**
     * 大数字包括小数
     */
    BIGDECIMAL("BigDecimal"),

    /**
     * 文件上传
     */
    MULTIPARTFILE("文件上传"),

    /**
     * 对象及不可识别类型
     */
    OBJECT("Object");

    private String code;

    private DataType (String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}

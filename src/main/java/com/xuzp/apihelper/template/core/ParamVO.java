package com.xuzp.apihelper.template.core;

/**
 * @author za-xuzhiping
 * @Date 2018/1/23
 * @Time 23:32
 */
public class ParamVO {

    private String name;
    private String type;
    private Boolean isOptional = Boolean.FALSE;
    private String desc = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getIsOptional() {
        return isOptional;
    }

    public void setOptional(Boolean optional) {
        isOptional = optional;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

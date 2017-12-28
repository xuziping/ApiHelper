package com.xuzp.apihelper.core;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author za-xuzhiping
 * @Date 2017/11/17
 * @Time 11:34
 */
public class Param {

    /**
     * 参数类型
     */
    private Type type;

    /**
     * 参数名称
     */
    private String name;

    /**
     * 参数描述
     */
    private String desc;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 是否基础类型，如int,String等
     */
    private boolean isBasicType = false;

    /**
     * 子属性，用于普通对象
     */
    private List<Param> children;

    public Param(){

    }

    public Param(Type type, String name, String desc, String defaultValue, List<Param> children){
        this.type = type;
        this.name = name;
        this.desc = desc;
        this.defaultValue = defaultValue;
        this.children = children;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isBasicType() {
        return isBasicType;
    }

    public void setBasicType(boolean basicType) {
        isBasicType = basicType;
    }

    public List<Param> getChildren() {
        return children;
    }

    public void setChildren(List<Param> children) {
        this.children = children;
    }
}

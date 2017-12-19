package com.xuzp.apidoc.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author za-xuzhiping
 * @Date 2017/11/17
 * @Time 11:34
 */
@Data
@NoArgsConstructor
public class Param {

    private Type type;
    private String name;
    private String desc;
    private String defaultValue;
    private List<Param> children;

    public Param(Type type, String name, String desc, String defaultValue, List<Param> children){
        this.type = type;
        this.name = name;
        this.desc = desc;
        this.defaultValue = defaultValue;
        this.children = children;
    }
}

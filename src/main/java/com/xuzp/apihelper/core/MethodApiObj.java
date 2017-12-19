package com.xuzp.apihelper.core;

import lombok.Data;

import java.util.List;

/**
 * @author XuZiPing
 * @Date 2017/12/17
 * @Time 13:17
 */
@Data
public class MethodApiObj {

    private String path;
    private String desc;
    private String labelName;
    private String name;
    private String apiMethod;
    private String group;
    private List<Param> params;
    private List<Param> returns;
    private Boolean isCollectionReturnType;

}

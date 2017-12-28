package com.xuzp.apihelper.core;

import java.util.List;

/**
 * @author XuZiPing
 * @Date 2017/12/17
 * @Time 13:17
 */
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

    public String getPath() {
        return path.replaceAll("\\\\", "/");
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getName() {
        return name.replaceAll("\\\\", "_");
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApiMethod() {
        return apiMethod.toUpperCase();
    }

    public void setApiMethod(String apiMethod) {
        this.apiMethod = apiMethod;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public List<Param> getParams() {
        return params;
    }

    public void setParams(List<Param> params) {
        this.params = params;
    }

    public List<Param> getReturns() {
        return returns;
    }

    public void setReturns(List<Param> returns) {
        this.returns = returns;
    }

    public Boolean getIsCollectionReturnType() {
        return isCollectionReturnType;
    }

    public void setIsCollectionReturnType(Boolean collectionReturnType) {
        isCollectionReturnType = collectionReturnType;
    }
}

package com.xuzp.apihelper.core;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

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

    public String getFormalApiMethod(){
        if (StringUtils.isNotBlank(apiMethod)) {
            return apiMethod.toUpperCase().substring(1, apiMethod.length() - 1);
        }
        return apiMethod;
    }

}

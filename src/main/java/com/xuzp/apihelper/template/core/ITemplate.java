package com.xuzp.apihelper.template.core;

import java.util.List;

/**
 * @author za-xuzhiping
 * @Date 2017/12/22
 * @Time 17:22
 */
public interface ITemplate {

    /**
     * 获取请求Json
     * @return
     */
    String getRequestJson(boolean showComments);

    /**
     * 获取返回Json
     * @return
     */
    String getResponseJson();

    /**
     * 获取请求参数列表
     * @return
     */
    String getParamListString();

    /**
     * 获取请求参数列表
     * @return
     */
    List<ParamVO> getParamList();

    /**
     * 获取最终生成内容
     * @return
     */
    String getContent();
}

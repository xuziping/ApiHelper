package com.xuzp.apihelper.template.base;

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
    String getRequestJson();

    /**
     * 获取返回Json
     * @return
     */
    String getResponseJson();

    /**
     * 获取请求参数列表
     * @return
     */
    String getParamList();

    /**
     * 获取最终生成内容
     * @return
     */
    String getContent();
}

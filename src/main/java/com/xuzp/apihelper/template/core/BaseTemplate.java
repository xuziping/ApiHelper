package com.xuzp.apihelper.template.core;

import com.google.common.collect.Maps;
import com.xuzp.apihelper.core.MethodApiObj;
import com.xuzp.apihelper.core.Param;
import com.xuzp.apihelper.mockdata.MockDataProvider;
import com.xuzp.apihelper.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.util.List;
import java.util.Map;

import static com.xuzp.apihelper.utils.Constants.LF;

/**
 * @author XuZiPing
 * @Date 2017/12/26
 * @Time 23:29
 */
public abstract class BaseTemplate implements ITemplate {

    private MethodApiObj methodApiObj;

    public BaseTemplate() {

    }

    public BaseTemplate(MethodApiObj methodApiObj) {
        this.methodApiObj = methodApiObj;
    }

    @Override
    public String getRequestJson() {
        if (CollectionUtils.isNotEmpty(methodApiObj.getParams())) {
            StringBuffer sb = new StringBuffer();
            sb.append("{").append(LF);
            processJSONData(methodApiObj.getParams(), sb);
            sb.append(LF).append("}");
            return JsonHelper.beautify(sb.toString());
        }
        return null;
    }

    /**
     * 返回完整的Response Json
     *
     * @return
     */
    @Override
    public String getResponseJson() {
        Map<String, String> params = Maps.newHashMap();
        params.put(Constants.PARAM_RESPONSE_JSON, getResponseValueData());
        String content = TemplateProvider.loadTemplate(Constants.RESPONSE_JSON_FTL, params);
        return JsonHelper.beautify(content);
    }

    /**
     * 返回Response Value节点的值
     *
     * @return
     */
    private String getResponseValueData() {
        if (CollectionUtils.isNotEmpty(methodApiObj.getReturns())) {
            if (methodApiObj.getReturns().size() == 1 && TypeHelper.isBasicType(methodApiObj.getReturns().get(0).getType())) {
                return MockDataProvider.mockValue(methodApiObj.getReturns().get(0));
            } else {
                StringBuffer sb = new StringBuffer();
                if (methodApiObj.getIsCollectionReturnType()) {
                    sb.append("[{").append(LF);
                } else {
                    sb.append("{").append(LF);
                }
                processJSONData(methodApiObj.getReturns(), sb);
                if (methodApiObj.getIsCollectionReturnType()) {
                    sb.append("}]");
                } else {
                    sb.append("}");
                }
                return sb.toString();
            }
        }
        return null;
    }

    /**
     * 返回参数列表
     */
    @Override
    public String getParamList() {
        String template = getParamListTemplate();
        if (StringUtils.isNoneEmpty(template)) {
            StringBuffer sb = new StringBuffer();
            processParamList(methodApiObj.getParams(), sb, "", template);
            return sb.toString();
        }
        return null;
    }

    /**
     * 返回参数列表模板名
     * @return
     */
    public abstract String getParamListTemplate();

    /**
     * 返回整个API模板名
     * @return
     */
    public abstract String getWholeTemplate();

    /**
     * 获取最终生成内容
     * @return
     */
    @Override
    public String getContent() {
        String template = getWholeTemplate();
        if (StringUtils.isNotEmpty(template)) {
            Map<String, String> params = Maps.newHashMap();
            params.put(Constants.PARAM_PATH, methodApiObj.getPath());
            params.put(Constants.PARAM_DESC, methodApiObj.getDesc());
            params.put(Constants.PARAM_APINAME, methodApiObj.getName());
            params.put(Constants.PARAM_APIGROUP, methodApiObj.getGroup());
            params.put(Constants.PARAM_APIMETHOD, methodApiObj.getApiMethod());
            params.put(Constants.PARAM_LABELNAME, methodApiObj.getLabelName());
            params.put(Constants.PARAM_PARAM_LIST, getParamList());
            params.put(Constants.PARAM_REQUEST_JSON, getRequestJson());
            params.put(Constants.PARAM_RESPONSE_JSON, getResponseJson());
            return TemplateProvider.loadTemplate(template, params);
        }
        return null;
    }

    /**
     * 处理出参和入参JSON内容的示例，递归处理
     */
    public static void processJSONData(List<Param> params, StringBuffer sb) {
        if (CollectionUtils.isNotEmpty(params)) {
            for (int i = 0; i < params.size(); i++) {
                Param param = params.get(i);
                if (i > 0) {
                    sb.append(",").append(LF);
                }

                if (CollectionUtils.isNotEmpty(param.getChildren())) {
                    boolean isCollection = false;
                    if (param.getType() instanceof ParameterizedTypeImpl) {
                        isCollection = TypeHelper.isCollection(((ParameterizedTypeImpl) param.getType()).getRawType());
                    }
                    if (param.isBasicType()) {
                        sb
                                .append(isCollection ? "[{" : "{").append(LF);
                    } else {
                        sb.append("\"" + param.getName() + "\": ")
                                .append(isCollection ? "[{" : "{").append(LF);
                    }
                    processJSONData(param.getChildren(), sb);
                    sb.append(LF).append(isCollection ? "}]" : "}");
                } else {
                    if (param.isBasicType()) {
                        sb.append(MockDataProvider.mockValue(param));
                    } else {
                        sb.append("\"" + param.getName() + "\": ").append(MockDataProvider.mockValue(param));
                    }
                }
            }
        }
    }

    /**
     * 递归处理参数列表
     */
    private void processParamList(List<Param> params, StringBuffer sb, String prefixName, String template) {
        if (CollectionUtils.isNotEmpty(params)) {
            params.forEach(param -> {
                if (sb.length() > 0) {
                    sb.append(LF);
                }
                sb.append(template
                        .replaceAll("`PARAM_TYPE`", TypeHelper.fixTypeName(param.getType().getTypeName()))
                        .replaceAll("`PARAM_NAME`", prefixName + param.getName())
                        .replaceAll("`PARAM_DESC`", param.getDesc()));
                if (CollectionUtils.isNotEmpty(param.getChildren())) {
                    processParamList(param.getChildren(), sb, param.getName() + ".", template);
                }
            });
        }
    }

    public MethodApiObj getMethodApiObj() {
        return methodApiObj;
    }

    public void setMethodApiObj(MethodApiObj methodApiObj) {
        this.methodApiObj = methodApiObj;
    }
}

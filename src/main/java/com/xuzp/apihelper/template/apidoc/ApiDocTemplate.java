package com.xuzp.apihelper.template.apidoc;

import com.xuzp.apihelper.core.MethodApiObj;
import com.xuzp.apihelper.core.Param;
import com.xuzp.apihelper.utils.Constants;
import com.xuzp.apihelper.utils.JsonHelper;
import com.xuzp.apihelper.utils.MockDataHelper;
import com.xuzp.apihelper.utils.TypeHelper;
import org.apache.commons.collections.CollectionUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.util.List;

import static com.xuzp.apihelper.utils.Constants.LF;

/**
 * @author za-xuzhiping
 * @Date 2017/11/17
 * @Time 11:34
 */
public class ApiDocTemplate {

    private MethodApiObj methodApiObj;

    public ApiDocTemplate(MethodApiObj methodApiObj) {
        this.methodApiObj = methodApiObj;
    }

    /**
     * 整个方法的apidoc模板
     */
    private String wholeTemplate() {
        StringBuffer sb = new StringBuffer(LF);
        sb.append("/**").append(LF);
        sb.append(" * @api #{APIMETHOD} /#{APIGROUP}/#{PATH} #{LABELNAME}").append(LF);
        sb.append(" * @apiVersion 1.0.0").append(LF);
        sb.append(" * @apiName #{APINAME}").append(LF);
        sb.append(" * @apiGroup #{APIGROUP}").append(LF);
        sb.append(" * @apiExample Example").append(LF);
        sb.append(" *   http://ip:port/#{APIGROUP}/#{PATH}").append(LF);
        sb.append(" * @apiDescription <b>使用说明：</b>#{DESC}").append(LF);
        sb.append("#{PARAM_PART}").append(LF);
        sb.append(" * @apiSuccess {String} success true:成功, false:失败").append(LF);
        sb.append(" * @apiSuccess {String} msg 返回信息").append(LF);
        sb.append(" * @apiSuccessExample {json} 接口数据响应示例").append(LF);
        sb.append("#{RETURN_PART}").append(LF);
        sb.append(" */");
        return sb.toString();
    }

    /**
     * 参数部分的模板，包含参数列表和参数json示例
     */
    private String paramTemplate(boolean hasParam) {
        if (hasParam) {
            StringBuffer sb = new StringBuffer();
            sb.append("#{PARAM_LIST}");
            sb.append(" * @apiParamExample {json} 接口请求入参示例").append(LF);
            sb.append(" *  {").append(LF);
            sb.append("#{PARAM_DATA}").append(LF);
            sb.append(" *  }");
            return sb.toString();
        }
        return "";
    }

    /**
     * 返回值json的模板
     */
    private static String returnTemplate(MethodApiObj methodApiObj, boolean hasReturnValue) {
        StringBuffer sb = new StringBuffer();
        sb.append(" *  {").append(LF);
        sb.append(" *     \"msg\": \"\",").append(LF);
        sb.append(" *     \"additionalInfo\": {},").append(LF);
        if (hasReturnValue) {
            if (methodApiObj.getReturns().size() == 1 && TypeHelper.isBasicType(methodApiObj.getReturns().get(0).getType())) {
                sb.append(" *     \"value\": ").append(MockDataHelper.mockValue(methodApiObj.getReturns().get(0))).append(",").append(LF);
            } else {
                sb.append(" *     \"value\": ").append(methodApiObj.getIsCollectionReturnType() ? "[{" : "{").append(LF);
                sb.append("#{RETURN_VALUES}").append(LF);
                sb.append(" *      ").append(methodApiObj.getIsCollectionReturnType() ? "}]," : "},").append(LF);
            }
        }
        sb.append(" *     \"success\": true").append(LF);
        sb.append(" *   }");
        return sb.toString();
    }

    /**
     * 对整体模板进行解析，替换变量
     */
    public String getApiDoc() {
        return wholeTemplate().replaceAll("#\\{PATH\\}", methodApiObj.getPath()).replaceAll("#\\{DESC\\}", methodApiObj.getDesc())
                .replaceAll("#\\{APINAME\\}", methodApiObj.getName()).replaceAll("#\\{APIGROUP\\}", methodApiObj.getGroup())
                .replaceAll("#\\{APIMETHOD\\}", methodApiObj.getApiMethod()).replaceAll("#\\{LABELNAME\\}", methodApiObj.getLabelName())
                .replaceAll("#\\{PARAM_PART\\}", getParamPart()).replaceAll("#\\{RETURN_PART\\}", getReturnPart(methodApiObj));
    }

    /**
     * 获取参数列表内容
     */
    private String getParamList() {
        StringBuffer sb = new StringBuffer();
        processParamList(methodApiObj.getParams(), sb, "");
        return sb.toString();
    }

    /**
     * 递归处理参数列表
     */
    private static void processParamList(List<Param> params, StringBuffer sb, String prefixName) {
        if (CollectionUtils.isNotEmpty(params)) {
            params.forEach(param -> {
                sb.append(Constants.PARAM_LIST_TEMPLATE
                        .replaceFirst("#\\{PARAM_TYPE\\}", TypeHelper.fixTypeName(param.getType().getTypeName()))
                        .replaceFirst("#\\{PARAM_NAME\\}", prefixName + param.getName())
                        .replaceFirst("#\\{PARAM_DESC\\}", param.getDesc())).append(LF);
                if (CollectionUtils.isNotEmpty(param.getChildren())) {
                    processParamList(param.getChildren(), sb, param.getName() + ".");
                }
            });
        }
    }

    /**
     * 获取入参示例内容
     */
    private String getParamData() {
        StringBuffer sb = new StringBuffer();
        processJSONData(methodApiObj.getParams(), sb, "");
        return sb.toString();
    }

    /**
     * 获取出参示例内容
     */
    private static String getReturnData(MethodApiObj methodApiObj) {
        StringBuffer sb = new StringBuffer();
        processJSONData(methodApiObj.getReturns(), sb, "   ");
        return sb.toString();
    }

    /**
     * 处理出参和入参JSON内容的示例，递归处理
     */
    public static void processJSONData(List<Param> params, StringBuffer sb, String prefixSpace) {
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
                        sb.append(" *     " + prefixSpace)
                                .append(isCollection ? "[{" : "{").append(LF);
                    } else {
                        sb.append(" *     " + prefixSpace + "\"" + param.getName() + "\": ")
                                .append(isCollection ? "[{" : "{").append(LF);
                    }


                    processJSONData(param.getChildren(), sb, prefixSpace + "    ");
                    sb.append(LF).append(" *      " + prefixSpace).append(isCollection ? "}]" : "}");
                } else {
                    if (param.isBasicType()) {
                        sb.append(" *     " + prefixSpace).append(MockDataHelper.mockValue(param));
                    } else {
                        sb.append(" *     " + prefixSpace + "\"" + param.getName() + "\": ").append(MockDataHelper.mockValue(param));
                    }
                }
            }
        }
    }

    /**
     * 获取参数区内容，包括参数列表和入参示例
     */
    private String getParamPart() {
        if (CollectionUtils.isNotEmpty(methodApiObj.getParams())) {
            return paramTemplate(true).replaceFirst("#\\{PARAM_LIST\\}", getParamList())
                    .replaceFirst("#\\{PARAM_DATA\\}", getParamData());
        } else {
            return paramTemplate(false);
        }
    }

    /**
     * 获取出参示例内容
     */
    private static String getReturnPart(MethodApiObj methodApiObj) {
        if (CollectionUtils.isNotEmpty(methodApiObj.getReturns())) {
            return returnTemplate(methodApiObj, true).replaceFirst("#\\{RETURN_VALUES\\}", getReturnData(methodApiObj));
        } else {
            return returnTemplate(methodApiObj, false);
        }
    }

    public static String getRequestData(MethodApiObj methodApiObj) {
        if (CollectionUtils.isNotEmpty(methodApiObj.getParams())) {
            StringBuffer sb = new StringBuffer();
            sb.append(" *  {").append(LF);
            ApiDocTemplate.processJSONData(methodApiObj.getParams(), sb, "");
            sb.append(LF);
            sb.append(" *  }");
            String content = sb.toString().replaceAll(" \\*  ", "")
                    .replaceAll("\"", "\\\\\"");
            return JsonHelper.beautify(content);
        }
        return "";
    }


    public static String getResponseData(MethodApiObj methodApiObj) {
        if (CollectionUtils.isNotEmpty(methodApiObj.getReturns())) {
            StringBuffer sb = new StringBuffer();
            ApiDocTemplate.processJSONData(methodApiObj.getReturns(), sb, "");
            String content = getReturnPart(methodApiObj).replaceAll(" \\*  ", "")
                    .replaceAll("\"", "\\\\\"");
            return JsonHelper.beautify(content);
        }
        return "";
    }

    public static String getParamList(MethodApiObj methodApiObj) {
        StringBuffer sb = new StringBuffer();
        processParamList(methodApiObj.getParams(), sb, "");
        return sb.toString();
    }
}

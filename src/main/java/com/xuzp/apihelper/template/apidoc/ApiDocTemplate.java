package com.xuzp.apihelper.template.apidoc;

import com.xuzp.apihelper.core.MethodApiObj;
import com.xuzp.apihelper.core.Param;
import com.xuzp.apihelper.template.base.BaseTemplate;
import com.xuzp.apihelper.template.base.ITemplate;
import com.xuzp.apihelper.utils.Constants;
import com.xuzp.apihelper.utils.TypeHelper;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

import static com.xuzp.apihelper.utils.Constants.LF;

/**
 * @author za-xuzhiping
 * @Date 2017/11/17
 * @Time 11:34
 */
public class ApiDocTemplate implements ITemplate {

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
     * 对整体模板进行解析，替换变量
     */
    public String getApiDoc() {
        return wholeTemplate().replaceAll("#\\{PATH\\}", methodApiObj.getPath()).replaceAll("#\\{DESC\\}", methodApiObj.getDesc())
                .replaceAll("#\\{APINAME\\}", methodApiObj.getName()).replaceAll("#\\{APIGROUP\\}", methodApiObj.getGroup())
                .replaceAll("#\\{APIMETHOD\\}", methodApiObj.getApiMethod()).replaceAll("#\\{LABELNAME\\}", methodApiObj.getLabelName())
                .replaceAll("#\\{PARAM_PART\\}", getParamPart()).replaceAll("#\\{RETURN_PART\\}", BaseTemplate.getReturnPart(methodApiObj));
    }

    @Override
    public String getRequestData() {
        StringBuffer sb = new StringBuffer();
        BaseTemplate.processJSONData(methodApiObj.getParams(), sb, "");
        return sb.toString();
    }

    @Override
    public String getResponseData() {
        return null;
    }

    /**
     * 获取参数列表内容
     */
    @Override
    public String getParamList() {
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
     * 获取参数区内容，包括参数列表和入参示例
     */
    private String getParamPart() {
        if (CollectionUtils.isNotEmpty(methodApiObj.getParams())) {
            return paramTemplate(true).replaceFirst("#\\{PARAM_LIST\\}", getParamList())
                    .replaceFirst("#\\{PARAM_DATA\\}", getRequestData());
        } else {
            return paramTemplate(false);
        }
    }

    public static String getParamList(MethodApiObj methodApiObj) {
        StringBuffer sb = new StringBuffer();
        processParamList(methodApiObj.getParams(), sb, "");
        return sb.toString();
    }
}

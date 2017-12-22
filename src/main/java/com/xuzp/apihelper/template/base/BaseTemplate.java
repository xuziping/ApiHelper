package com.xuzp.apihelper.template.base;

import com.xuzp.apihelper.core.MethodApiObj;
import com.xuzp.apihelper.core.Param;
import com.xuzp.apihelper.utils.JsonHelper;
import com.xuzp.apihelper.utils.MockDataHelper;
import com.xuzp.apihelper.utils.TypeHelper;
import org.apache.commons.collections.CollectionUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.util.List;

import static com.xuzp.apihelper.utils.Constants.LF;

/**
 * @author za-xuzhiping
 * @Date 2017/12/22
 * @Time 17:39
 */
public class BaseTemplate {

    public static String getRequestData(MethodApiObj methodApiObj) {
        if (CollectionUtils.isNotEmpty(methodApiObj.getParams())) {
            StringBuffer sb = new StringBuffer();
            sb.append(" *  {").append(LF);
            processJSONData(methodApiObj.getParams(), sb, "");
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
            processJSONData(methodApiObj.getReturns(), sb, "");
            String content = getReturnPart(methodApiObj).replaceAll(" \\*  ", "")
                    .replaceAll("\"", "\\\\\"");
            return JsonHelper.beautify(content);
        }
        return "";
    }

    /**
     * 获取出参示例内容
     */
    public static String getReturnPart(MethodApiObj methodApiObj) {
        if (CollectionUtils.isNotEmpty(methodApiObj.getReturns())) {
            return returnTemplate(methodApiObj, true).replaceFirst("#\\{RETURN_VALUES\\}", getReturnData(methodApiObj));
        } else {
            return returnTemplate(methodApiObj, false);
        }
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
}

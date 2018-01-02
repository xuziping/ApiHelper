package com.xuzp.apihelper.template.apidoc;

import com.xuzp.apihelper.core.MethodApiObj;
import com.xuzp.apihelper.template.core.BaseTemplate;
import com.xuzp.apihelper.utils.Constants;
import org.apache.commons.lang3.StringUtils;

import static com.xuzp.apihelper.utils.Constants.LF;

/**
 * @author za-xuzhiping
 * @Date 2017/11/17
 * @Time 11:34
 */
public class ApiDocTemplate extends BaseTemplate {

    public ApiDocTemplate(MethodApiObj methodApiObj) {
        super(methodApiObj);
    }

    @Override
    public String getParamListTemplate() {
        return Constants.API_DOC_PARAM_LIST_TEMPLATE;
    }

    @Override
    public String getWholeTemplate() {
        return Constants.APIDOC_TEMPLATE_FTL;
    }

    @Override
    public String getRequestJson() {
        return fixJson(super.getRequestJson());
    }

    @Override
    public String getResponseJson() {
        return fixJson(super.getResponseJson());
    }

    /**
     * 按ApiDoc格式，补上 * 符号并且缩进
     * @param content
     * @return
     */
    private static String fixJson(String content) {
        if (StringUtils.isNotBlank(content)) {
            return content.replaceFirst("\\{", " *\t{")
                    .replaceAll(LF, "\n *\t");
        }
        return null;
    }
}

package com.xuzp.apihelper.template.markdown;

import com.xuzp.apihelper.core.MethodApiObj;
import com.xuzp.apihelper.template.core.BaseTemplate;
import com.xuzp.apihelper.utils.Constants;
import org.apache.commons.lang3.StringUtils;

/**
 * @author za-xuzhiping
 * @Date 2017/12/21
 * @Time 16:50
 */
public class MarkdownTemplate extends BaseTemplate {

    public MarkdownTemplate(MethodApiObj methodApiObj) {
        super(methodApiObj);
    }

    @Override
    public String getRequestJson() {
        return fixJson(super.getRequestJson());
    }

    @Override
    public String getResponseJson() {
        return fixJson(super.getResponseJson());
    }

    @Override
    public String getWholeTemplate() {
        return Constants.MARKDOWN_TEMPLATE_FTL;
    }

    @Override
    public String getParamListTemplate() {
        return Constants.MARKDOWN_PARAM_LIST_TEMPLATE;
    }

    /**
     * 符合markdown格式的缩进，使其有code样式
     * @param content
     * @return
     */
    private static String fixJson(String content) {
        if (StringUtils.isNotBlank(content)) {
            return "\t" + content.replaceAll("\n", "\n\t");
        }
        return null;
    }
}

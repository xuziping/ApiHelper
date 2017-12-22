package com.xuzp.apihelper.template.markdown;

import com.xuzp.apihelper.core.MethodApiObj;
import com.xuzp.apihelper.properties.LoadProperties;
import com.xuzp.apihelper.template.apidoc.ApiDocTemplate;
import com.xuzp.apihelper.template.base.BaseTemplate;
import com.xuzp.apihelper.template.base.ITemplate;
import com.xuzp.apihelper.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * @author za-xuzhiping
 * @Date 2017/12/21
 * @Time 16:50
 */
@Slf4j
public class MarkdownTemplate implements ITemplate{

    private static String template = null;

    private MethodApiObj methodApiObj;

    public static synchronized void loadTemplate() {
        try {
            File file = null;
            String markdownTemplate = LoadProperties.getProperties().getMarkdownTemplate();
            if (StringUtils.isNotEmpty(markdownTemplate)) {
                file = new File(markdownTemplate);
                if (file != null && file.exists() && file.isFile()) {
                    template = FileUtils.readFileToString(file, Constants.ENCODING);
                }
            }
        } catch (Exception e) {
            log.error("加载Markdown模板失败，异常={}", e);
        }
    }

    public MarkdownTemplate(MethodApiObj methodApiObj) {
        this.methodApiObj = methodApiObj;
    }

    public String getMarkdown() {
        if (StringUtils.isEmpty(template)) {
            return "";
        }
        return template.replaceAll("`API_METHOD`", methodApiObj.getFormalApiMethod())
                .replaceAll("`API_LABEL_NAME`", methodApiObj.getLabelName())
                .replaceAll("`API_GROUP`", methodApiObj.getGroup())
                .replaceAll("`API_PATH`", methodApiObj.getPath())
                .replaceAll("`API_DESCRIPTION`", methodApiObj.getDesc())
                .replaceAll("`REQUEST_DATA`", getRequestData())
                .replaceAll("`RESPONSE_DATA`", getResponseData())
                .replaceAll("`PARAM_LIST`", getParamList());
    }

    @Override
    public String getRequestData() {
        return "\t" + BaseTemplate.getRequestData(methodApiObj).replaceAll("\n", "\n\t");
    }

    @Override
    public String getResponseData() {
        return "\t" + BaseTemplate.getResponseData(methodApiObj).replaceAll("\n", "\n\t");
    }

    @Override
    public String getParamList() {
        String content = ApiDocTemplate.getParamList(methodApiObj);
        return content.replaceAll(" \\* @apiParam ", "")
                .replaceAll(" ", " \\| ")
                .replaceAll("\\{", "")
                .replaceAll("\\}", "");
    }
}

package com.xuzp.apihelper.template.markdown;

import com.xuzp.apihelper.core.MethodApiObj;
import com.xuzp.apihelper.template.apidoc.ApiDocTemplate;
import com.xuzp.apihelper.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author za-xuzhiping
 * @Date 2017/12/21
 * @Time 16:50
 */
@Slf4j
public class MarkdownTemplate {

    private static String template = null;

    private MethodApiObj methodApiObj;

    public static synchronized void loadTemplate() {
            try {
                File file = new File(MarkdownTemplate.class.getClassLoader().getResource(Constants.MARKDOWN_TEMPLATE).getFile());
                if(!file.exists() || !file.isFile()) {
                    file =  new File(MarkdownTemplate.class.getClassLoader().getResource(Constants.DEFAULT_MARKDOWN_TEMPLATE).getFile());
                }
                template = FileUtils.readFileToString(file, Constants.ENCODING);
            } catch(Exception e) {
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

    private String getRequestData() {
        return "\t" + ApiDocTemplate.getRequestData(methodApiObj).replaceAll("\n","\n\t");
    }

    private String getResponseData() {
        return "\t" + ApiDocTemplate.getResponseData(methodApiObj).replaceAll("\n","\n\t");
    }

    private String getParamList() {
        String content = ApiDocTemplate.getParamList(methodApiObj);
        return content.replaceAll(" \\* @apiParam ", "")
                .replaceAll(" ", " \\| ")
                .replaceAll("\\{", "")
                .replaceAll("\\}", "");
    }
}

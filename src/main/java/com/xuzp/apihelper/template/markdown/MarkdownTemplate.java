package com.xuzp.apihelper.template.markdown;

import com.xuzp.apihelper.core.MethodApiObj;
import com.xuzp.apihelper.template.apidoc.ApiDocTemplate;
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
public class MarkdownTemplate {

    private static String template = null;

    private MethodApiObj methodApiObj;

    public static synchronized void loadTemplate(String path) {
        if (StringUtils.isNoneBlank(path)) {
            File file = FileUtils.getFile(path);
            if(file.exists() && file.isFile()) {
                try {
                    template = FileUtils.readFileToString(file, Constants.ENCODING);
                } catch (Exception e) {
                    log.error("加载Markdown模板失败，文件={}，异常={}", file.getAbsolutePath(), e);
                }
            }
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
                .replaceAll("`API_GROUP`", methodApiObj.getGroup())
                .replaceAll("`API_PATH`", methodApiObj.getPath())
                .replaceAll("`API_DESCRIPTION`", methodApiObj.getDesc())
                .replaceAll("`REQUEST_DATA`", getRequestData())
                .replaceAll("`RESPONSE_DATA`", getResponseData())
                .replaceAll("`PARAM_LIST`", getParamList());
    }

    private String getRequestData() {
        return "\t\t" + ApiDocTemplate.getParamsData(methodApiObj).replaceAll("\n","\n\t\t");
    }

    private String getResponseData() {
        return "\t\t" + ApiDocTemplate.getReturnData(methodApiObj).replaceAll("\n","\n\t\t");
    }

    private String getParamList() {
        String content = ApiDocTemplate.getParamList(methodApiObj);
        return content.replaceAll(" \\* @apiParam ", "").replaceAll(" ", " \\| ");
    }
}

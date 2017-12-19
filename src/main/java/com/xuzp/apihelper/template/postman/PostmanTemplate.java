package com.xuzp.apihelper.template.postman;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.xuzp.apihelper.core.MethodApiObj;
import com.xuzp.apihelper.properties.LoadProperties;
import com.xuzp.apihelper.template.apidoc.ApiDocTemplate;
import com.xuzp.apihelper.template.postman.enums.BodyModeEnum;
import com.xuzp.apihelper.template.postman.enums.ContentTypeEnum;
import com.xuzp.apihelper.utils.Constants;
import com.xuzp.apihelper.utils.JsonHelper;
import com.xuzp.apihelper.utils.UrlHelper;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * @author XuZiPing
 * @Date 2017/12/16
 * @Time 17:44
 */
public class PostmanTemplate {

    private MethodApiObj methodApiObj;

    private PostmanRequest postmanRequest;

    private String moduleName;

    private Map<String, CategoryItemNode> categoryItemNodeMap = Maps.newHashMap();

    public PostmanTemplate(String moduleName) {
        this.moduleName = moduleName;
        postmanRequest();
    }

    private PostmanRequest postmanRequest() {
        if (postmanRequest == null) {
            postmanRequest = new PostmanRequest();
            postmanRequest.setInfo(infoNode());
            postmanRequest.setItem(Lists.newArrayList());
        }
        return postmanRequest;
    }

    public boolean hasData() {
        return postmanRequest != null && CollectionUtils.isNotEmpty(postmanRequest.getItem());
    }

    private InfoNode infoNode() {
        InfoNode infoNode = new InfoNode();
        infoNode.setDescription("");
        infoNode.setName(moduleName);
        infoNode.setSchema(Constants.SCHEMA_V2_1_0);
        return infoNode;
    }

    public void add(MethodApiObj api) {
        this.methodApiObj = api;
        CategoryItemNode categoryItemNode = categoryItemNodeMap.get(methodApiObj.getGroup());
        if (categoryItemNode == null) {
            categoryItemNode = new CategoryItemNode();
            categoryItemNode.setName(methodApiObj.getGroup());
            categoryItemNodeMap.put(methodApiObj.getGroup(), categoryItemNode);
            this.postmanRequest.getItem().add(categoryItemNode);
        }

        List<RequestItemNode> requestItemNodes = categoryItemNode.getItem();
        if (requestItemNodes == null) {
            requestItemNodes = Lists.newArrayList();
            categoryItemNode.setItem(requestItemNodes);
        }
        requestItemNodes.add(requestItemNode());
        this.methodApiObj = null;
    }

    private RequestItemNode requestItemNode() {
        RequestItemNode itemNode = new RequestItemNode();
        itemNode.setName(methodApiObj.getPath());
        itemNode.setRequest(requestNode());
        return itemNode;
    }

    private UrlNode urlNode() {
        UrlNode urlNode = new UrlNode();
        String requestURL = LoadProperties.getProperties().getRequestURL();
        String raw = String.format("%s/%s/%s", requestURL, methodApiObj.getGroup(), methodApiObj.getPath());
        urlNode.setRaw(raw);
        UrlHelper urlHelper = new UrlHelper(requestURL);
        urlNode.setHost(urlHelper.getHostSplit());
        urlNode.setPort(urlHelper.getPort());
        urlNode.setProtocol(urlHelper.getProtocol());
        String[] path = String.format("%s/%s", methodApiObj.getGroup(), methodApiObj.getPath())
                .replaceAll("\\\\", "/").split("/");
        urlNode.setPath(Lists.newArrayList(path));
        return urlNode;
    }

    private RequestNode requestNode() {
        RequestNode requestNode = new RequestNode();
        requestNode.setUrl(urlNode());
        requestNode.setBody(bodyNode());
        requestNode.setHeader(header());
        requestNode.setMethod(methodApiObj.getApiMethod().toUpperCase().substring(1, methodApiObj.getApiMethod().length() - 1));
        return requestNode;
    }

    private List<Map<String, String>> header() {
        List<Map<String, String>> header = Lists.newArrayList();
        Map<String, String> entry = Maps.newHashMap();
        entry.put("key", Constants.CONTENT_TYPE);
        entry.put("value", ContentTypeEnum.JSON.getValue());
        header.add(entry);
        return header;
    }

    private BodyNode bodyNode() {
        BodyNode bodyNode = new BodyNode();
        bodyNode.setMode(BodyModeEnum.RAW.getValue());
        bodyNode.setRaw(getParamsPart());
        return bodyNode;
    }

    public String getPostmanJSON() {
        return JsonHelper.beautify(new Gson().toJson(postmanRequest()));
    }

    private String getParamsPart() {
        if (CollectionUtils.isNotEmpty(methodApiObj.getParams())) {
            String content = String.format("{%s}", getParamData()).replaceAll("\\*", "");
            return JsonHelper.beautify(content);
        }
        return "";
    }

    private String getParamData() {
        StringBuffer sb = new StringBuffer();
        ApiDocTemplate.processJSONData(methodApiObj.getParams(), sb, "");
        return sb.toString();
    }
}

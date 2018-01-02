package com.xuzp.apihelper.template.postman;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.xuzp.apihelper.core.MethodApiObj;
import com.xuzp.apihelper.core.Param;
import com.xuzp.apihelper.properties.LoadProperties;
import com.xuzp.apihelper.template.core.BaseTemplate;
import com.xuzp.apihelper.template.postman.enums.BodyModeEnum;
import com.xuzp.apihelper.template.postman.enums.ContentTypeEnum;
import com.xuzp.apihelper.template.postman.node.*;
import com.xuzp.apihelper.utils.Constants;
import com.xuzp.apihelper.utils.JsonHelper;
import com.xuzp.apihelper.mockdata.MockDataProvider;
import com.xuzp.apihelper.utils.UrlHelper;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author XuZiPing
 * @Date 2017/12/16
 * @Time 17:44
 */
public class PostmanTemplate extends BaseTemplate {

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
        this.setMethodApiObj(api);
        CategoryItemNode categoryItemNode = categoryItemNodeMap.get(getMethodApiObj().getGroup());
        if (categoryItemNode == null) {
            categoryItemNode = new CategoryItemNode();
            categoryItemNode.setName(getMethodApiObj().getGroup());
            categoryItemNodeMap.put(getMethodApiObj().getGroup(), categoryItemNode);
            this.postmanRequest.getItem().add(categoryItemNode);
        }

        List<RequestItemNode> requestItemNodes = categoryItemNode.getItem();
        if (requestItemNodes == null) {
            requestItemNodes = Lists.newArrayList();
            categoryItemNode.setItem(requestItemNodes);
        }
        requestItemNodes.add(requestItemNode());
        this.setMethodApiObj(null);
    }

    private RequestItemNode requestItemNode() {
        RequestItemNode itemNode = new RequestItemNode();
        itemNode.setName(getMethodApiObj().getPath());
        itemNode.setRequest(requestNode());
        return itemNode;
    }

    private UrlNode urlNode() {
        UrlNode urlNode = new UrlNode();
        String requestURL = LoadProperties.getProperties().getRequestURL();
        UrlHelper urlHelper = new UrlHelper(requestURL);
        urlNode.setHost(urlHelper.getHostSplit());
        urlNode.setPort(urlHelper.getPort());
        urlNode.setProtocol(urlHelper.getProtocol());
        String[] path = String.format("%s/%s", getMethodApiObj().getGroup(), getMethodApiObj().getPath())
                .replaceAll("\\\\", "/").split("/");
        urlNode.setPath(Lists.newArrayList(path));
        String raw = String.format("%s/%s/%s", requestURL, getMethodApiObj().getGroup(), getMethodApiObj().getPath());
        if (isGET()) {
            if (CollectionUtils.isNotEmpty(getMethodApiObj().getParams())) {
                List<QueryNode> queryNodes = Lists.newArrayList();
                for (Param param : getMethodApiObj().getParams()) {
                    queryNodes.add(queryNode(param));
                }
                urlNode.setQuery(queryNodes);
                raw = raw + "?" + queryNodes.stream().map(x -> x.getKey() + "=" + x.getValue())
                        .collect(Collectors.joining("&"));
            }
        }
        urlNode.setRaw(raw);
        return urlNode;
    }

    private QueryNode queryNode(Param param) {
        QueryNode queryNode = new QueryNode();
        queryNode.setEquals(Boolean.TRUE);
        queryNode.setKey(param.getName());
        queryNode.setValue(MockDataProvider.mockValue(param));
        return queryNode;
    }

    private RequestNode requestNode() {
        RequestNode requestNode = new RequestNode();
        String apiMethod = getMethodApiObj().getApiMethod();
        requestNode.setMethod(apiMethod);
        switch (apiMethod) {
            case Constants.PUT:
            case Constants.POST:
                requestNode.setUrl(urlNode());
                requestNode.setBody(bodyNode());
                requestNode.setHeader(header());
                break;
            case Constants.GET:
                requestNode.setUrl(urlNode());
                break;
            case Constants.DELETE:
            default:
        }
        return requestNode;
    }

    private boolean isGET() {
        return getMethodApiObj().getApiMethod().toUpperCase().equals(Constants.GET);
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
        bodyNode.setRaw(getRequestJson());
        return bodyNode;
    }

    @Override
    public String getContent() {
        return JsonHelper.beautify(new Gson().toJson(postmanRequest()));
    }

    @Override
    public String getParamListTemplate() {
        return null;
    }

    @Override
    public String getWholeTemplate() {
        return null;
    }
}

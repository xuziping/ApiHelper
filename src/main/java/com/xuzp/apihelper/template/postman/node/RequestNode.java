package com.xuzp.apihelper.template.postman.node;

import java.util.List;
import java.util.Map;

/**
 * @author XuZiPing
 * @Date 2017/12/16
 * @Time 17:09
 */
public class RequestNode {

    private String method;

    private List<Map<String, String>> header;

    private BodyNode  body;

    private UrlNode url;

    private String description;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<Map<String, String>> getHeader() {
        return header;
    }

    public void setHeader(List<Map<String, String>> header) {
        this.header = header;
    }

    public BodyNode getBody() {
        return body;
    }

    public void setBody(BodyNode body) {
        this.body = body;
    }

    public UrlNode getUrl() {
        return url;
    }

    public void setUrl(UrlNode url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

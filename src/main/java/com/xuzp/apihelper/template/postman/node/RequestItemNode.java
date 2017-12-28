package com.xuzp.apihelper.template.postman.node;

import java.util.List;

/**
 * @author XuZiPing
 * @Date 2017/12/16
 * @Time 17:08
 */
public class RequestItemNode {

    private String name;

    private RequestNode request;

    private List<ResponseNode> response;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RequestNode getRequest() {
        return request;
    }

    public void setRequest(RequestNode request) {
        this.request = request;
    }

    public List<ResponseNode> getResponse() {
        return response;
    }

    public void setResponse(List<ResponseNode> response) {
        this.response = response;
    }
}

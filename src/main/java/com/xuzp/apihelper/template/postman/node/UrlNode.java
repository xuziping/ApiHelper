package com.xuzp.apihelper.template.postman.node;

import java.util.List;

/**
 * @author XuZiPing
 * @Date 2017/12/16
 * @Time 17:13
 */
public class UrlNode {

    private String raw;

    private String protocol;

    private List<String> host;

    private String port;

    private List<String> path;

    private List<QueryNode> query;

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public List<String> getHost() {
        return host;
    }

    public void setHost(List<String> host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    public List<QueryNode> getQuery() {
        return query;
    }

    public void setQuery(List<QueryNode> query) {
        this.query = query;
    }
}

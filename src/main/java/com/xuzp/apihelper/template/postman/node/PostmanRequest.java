package com.xuzp.apihelper.template.postman.node;

import java.util.List;

/**
 * @author za-xuzhiping
 * @Date 2017/12/7
 * @Time 17:18
 */
public class PostmanRequest {

    private InfoNode info;

    private List<CategoryItemNode> item;

    public InfoNode getInfo() {
        return info;
    }

    public void setInfo(InfoNode info) {
        this.info = info;
    }

    public List<CategoryItemNode> getItem() {
        return item;
    }

    public void setItem(List<CategoryItemNode> item) {
        this.item = item;
    }
}

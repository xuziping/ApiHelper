package com.xuzp.apihelper.template.postman.node;

import java.util.List;

/**
 * @author XuZiPing
 * @Date 2017/12/18
 * @Time 23:51
 */
public class CategoryItemNode {

    private String name;

    private List<RequestItemNode> item;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RequestItemNode> getItem() {
        return item;
    }

    public void setItem(List<RequestItemNode> item) {
        this.item = item;
    }
}

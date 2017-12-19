package com.xuzp.apidoc.template.postman;

import lombok.Data;

import java.util.List;

/**
 * @author XuZiPing
 * @Date 2017/12/18
 * @Time 23:51
 */
@Data
public class CategoryItemNode {

    private String name;

    private List<RequestItemNode> item;
}

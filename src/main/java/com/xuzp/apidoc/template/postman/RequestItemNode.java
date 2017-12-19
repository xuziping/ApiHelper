package com.xuzp.apidoc.template.postman;

import lombok.Data;

import java.util.List;

/**
 * @author XuZiPing
 * @Date 2017/12/16
 * @Time 17:08
 */
@Data
public class RequestItemNode {

    private String name;

    private RequestNode request;

    private List<ResponseNode> response;
}

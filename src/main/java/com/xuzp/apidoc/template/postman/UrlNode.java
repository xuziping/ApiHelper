package com.xuzp.apidoc.template.postman;

import lombok.Data;

import java.util.List;

/**
 * @author XuZiPing
 * @Date 2017/12/16
 * @Time 17:13
 */
@Data
public class UrlNode {

    private String raw;

    private String protocol;

    private List<String> host;

    private String port;

    private List<String> path;
}

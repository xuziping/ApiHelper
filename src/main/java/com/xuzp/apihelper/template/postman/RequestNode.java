package com.xuzp.apihelper.template.postman;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author XuZiPing
 * @Date 2017/12/16
 * @Time 17:09
 */
@Data
public class RequestNode {

    private String method;

    private List<Map<String, String>> header;

    private BodyNode  body;

    private UrlNode url;
}

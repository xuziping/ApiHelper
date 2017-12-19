package com.xuzp.apihelper.template.postman;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author za-xuzhiping
 * @Date 2017/12/7
 * @Time 17:18
 */
@Slf4j
@Data
public class PostmanRequest {

    private InfoNode info;

    private List<CategoryItemNode> item;

}

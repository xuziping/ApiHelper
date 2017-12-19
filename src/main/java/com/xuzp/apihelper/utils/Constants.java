package com.xuzp.apihelper.utils;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * @author za-xuzhiping
 * @Date 2017/12/7
 * @Time 17:19
 */
public interface Constants {

    String ENUM = "Enum";
    String URL_SPLIT = "/";
    String LF = "\n";
    String PARAM_LIST_TEMPLATE = " * @apiParam {#{PARAM_TYPE}} #{PARAM_NAME} #{PARAM_DESC}";
    String STRING_LIST_TYPE_NAME = "java.util.List<java.lang.String>";
    String INTEGER_LIST_TYPE_NAME = "java.util.List<java.lang.Integer>";
    String LONG_LIST_TYPE_NAME = "java.util.List<java.lang.Long>";
    String COLLECTION_TYPE_NAME = "java.util.Collection";
    String OBJECT_TYPE_NAME = "java.lang.Object";
    String MAP_TYPE_NAME = "java.util.Map";
    String ENCODING = "UTF-8";
    String PAGABLE_TYPE = "Page";
    String DOT = ".";
    String SPLIT_MARK = ";";
    String COMMENT_MARK = "*";
    int MAX_RECURSION = 4;
    String JAVA = "java";

    String CONTENT_TYPE = "Content-Type";
    String REQUEST_MAPPING = "RequestMapping";
    String GET_MAPPING = "GetMapping";
    String PUT_MAPPING = "PutMapping";
    String POST_MAPPING = "PostMapping";
    String DELETE_MAPPING = "DeleteMapping";
    Set<String> REQUEST_MAPPINGS = Sets.newHashSet(REQUEST_MAPPING,POST_MAPPING,PUT_MAPPING,DELETE_MAPPING,GET_MAPPING);
    String VOID = "void";

    String DEFAULT_PAGABLE_CLASS_NAME  = "Page";
    String DEFAULT_MODULE_NAME  = "new_module";
    String DEFAULT_REQUEST_URL = "http://127.0.0.1:8080";
    String DEFAULT_OUTPUT_FOLDER = "__autoAPI";
    String API_DOC_FOLDER = "apihelper";
    String POSTMAN_FOLDER = "postman";
    String TEXT_FILE_SUFFIX = ".txt";
    String JSON_FILE_SUFFIX = ".json";
    /**
     * Property Area
     */
    String PROPERTY_FILE = "autoApiDoc.properties";
    String COMMENT_PATH = "commentPath";
    String OUTPUT_PATH = "outputPath";
    String CLASS_PATH = "classPath";
    String SERVICE_PATH = "servicePath";
    String MODULE_PATH = "modulePath";
    String REQUEST_URL = "requestURL";
    String PAGABLE_CLASS_NAME = "pagableClassName";
    /**
     * Postman Area
     */
    String SCHEMA_V2_1_0 = "https://schema.getpostman.com/json/collection/v2.1.0/collection.json";


}



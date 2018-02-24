package com.xuzp.apihelper.mockdata;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.xuzp.apihelper.core.Param;
import com.xuzp.apihelper.properties.LoadProperties;
import com.xuzp.apihelper.utils.TypeHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;

import static com.xuzp.apihelper.utils.Constants.*;

/**
 * @author za-xuzhiping
 * @Date 2017/12/7
 * @Time 17:23
 */
public class MockDataProvider {

    private static final Logger log = LoggerFactory.getLogger(MockDataProvider.class);

    private static Map<DataType, String> mockDataMap = Maps.newHashMap();

    static {
        loadMockDataMap();
    }

    private synchronized static void loadMockDataMap() {
        // 首先加载所有默认假数据
        try {
            loadMockDataMap(new ClassPathResource("mockData.properties").getInputStream());
        } catch (Exception e) {
            log.error("加载默认假数据文件失败，异常={}", e);
        }

        // 如果用户有自定义假数据，覆盖默认值
        String path = LoadProperties.getProperties().getMockDataPath();
        if (StringUtils.isNotBlank(path)) {
            File mockDataFile = new File(path);
            if (!mockDataFile.exists() || !mockDataFile.isFile()) {
                log.warn("用戶指定的假数据文件不存在，路径={}", mockDataFile.getAbsolutePath());
            } else {
                try {
                    loadMockDataMap(new FileInputStream(mockDataFile));
                } catch (Exception e) {
                    log.error("加载用户自定义假数据文件失败，异常={}", e);
                }
            }
        }
    }

    private static void loadMockDataMap(InputStream inputStream) throws Exception {
        Properties props = new Properties();
        props.load(inputStream);
        Arrays.stream(DataType.values()).forEach(x -> {
            String value = props.getProperty(x.getCode());
            if (value == null) {
                value = props.getProperty(x.getCode().toLowerCase());
            }
            if (value == null) {
                value = props.getProperty(x.getCode().toUpperCase());
            }
            if (value != null) {
                mockDataMap.put(x, value);
            }
        });
    }

    /**
     * 按照参数类型返回假数据，用来填充参数请求和返回请求
     */
    public static String mockValue(Param param) {
        if (param.getDefaultValue() != null) {
            return param.getDefaultValue();
        }
        try {
            return mockValue(param.getType(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            return String.format("目前尚不支持%s类型的假数据", param.getType().getTypeName());
        }
    }

    private static String mockValue(Type type, int level) throws Exception {

        if (level > MAX_RECURSION) {
            return "\"递归达到极限值，怀疑参数循环引用\"";
        }

        String typeName = TypeHelper.fixTypeName(type.getTypeName());
        if (typeName.equalsIgnoreCase(TypeHelper.fixTypeName(Long.class.getName()))
                || typeName.equals(TypeHelper.fixTypeName(long.class.getName()))) {
            return mockDataMap.get(DataType.LONG);
        } else if (typeName.equalsIgnoreCase(TypeHelper.fixTypeName(Integer.class.getName()))
                || typeName.equals(TypeHelper.fixTypeName(int.class.getName()))) {
            return mockDataMap.get(DataType.INTEGER);
        } else if (typeName.equalsIgnoreCase(TypeHelper.fixTypeName(String.class.getName()))) {
            return mockDataMap.get(DataType.STRING);
        } else if (typeName.equalsIgnoreCase(TypeHelper.fixTypeName(Boolean.class.getName()))
                || typeName.equals(TypeHelper.fixTypeName(boolean.class.getName()))) {
            return mockDataMap.get(DataType.BOOLEAN);
        } else if (STRING_LIST_TYPE_NAME.equals(type.getTypeName())
                || STRING_ARRAY_TYPE_NAME.equals(type.getTypeName())){
            return mockDataMap.get(DataType.LIST_STRING);
        } else if (LONG_LIST_TYPE_NAME.equals(type.getTypeName())
                || LONG_ARRAY_TYPE_NAME.equals(type.getTypeName())
                || LONG_ARRAY_BASIC_TYPE_NAME.equals(type.getTypeName())) {
            return mockDataMap.get(DataType.LIST_LONG);
        } else if (INTEGER_LIST_TYPE_NAME.equals(type.getTypeName())
                || INTEGER_ARRAY_TYPE_NAME.equals(type.getTypeName())
                || INT_ARRAY_BASIC_TYPE_NAME.equals(type.getTypeName())) {
            return mockDataMap.get(DataType.LIST_INTEGER);
        } else if (TypeHelper.isEnumType(type)) {
            try {
                Object[] enumObjs = Class.forName(type.getTypeName()).getEnumConstants();
                return "\"" + enumObjs[0].toString() + "\"";
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (typeName.equalsIgnoreCase(TypeHelper.fixTypeName(Map.class.getName()))) {
            Type[] children = ((ParameterizedTypeImpl) type).getActualTypeArguments();
            return String.format("{%s: %s}", mockValue(children[0], level + 1), mockValue(children[1], level + 1));
        } else if (type.getTypeName().equals(Date.class.getName())) {
            return mockDataMap.get(DataType.DATE);
        } else if (type.getTypeName().equals(BigDecimal.class.getName())) {
            return mockDataMap.get(DataType.BIGDECIMAL);
        } else if (type.getTypeName().equals(Void.class.getName())) {
            return "";
        } else if (type.getTypeName().equalsIgnoreCase(Object.class.getTypeName())) {
            return mockDataMap.get(DataType.OBJECT);
        } else if (TypeHelper.isMultipartFile(type)) {
            return mockDataMap.get(DataType.MULTIPARTFILE);
        }

        if (type instanceof ParameterizedType) {
            Class cls = (((ParameterizedTypeImpl) type).getRawType());
            if (TypeHelper.isCollection(cls)) {
                Type child = ((ParameterizedTypeImpl) type).getActualTypeArguments()[0];
                return "[" + mockValue(child, level + 1) + "]";
            }
        }
        Class cls = null;
        try {
            cls = Class.forName(type.getTypeName());
        } catch (ClassNotFoundException e) {
            log.error("Not found class: {}", type.getTypeName());
            return "目前无法解析" + type.getTypeName();
        }
        boolean hasField = false;
        if (!TypeHelper.isVoid(cls)) {
            Set<Field> fields = Sets.newHashSet();
            TypeHelper.getAllFields(cls, fields);
            StringBuilder sb = new StringBuilder();
            for (Field field : fields) {
                field.setAccessible(true);
                /** 忽略静态字段 */
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                hasField = true;
                if (sb.length() > 0) {
                    sb.append(",");
                }

                /** 处理集合包装类型字段，如List等 */
                if (TypeHelper.isCollection(field.getType())) {
                    Type childType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                    if (!TypeHelper.isBasicType(childType)) {
                        sb.append("\"" + field.getName() + "\": " + mockValue(childType, level + 1));
                    }
                }
                /** 处理Map类型字段 */
                else if (TypeHelper.isMap(field.getType())) {
                    Type keyType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                    Type valueType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[1];
                    sb.append(mockValue(keyType, level + 1)
                            + ": " + mockValue(valueType, level + 1));
                }
                /** 处理非枚举类型字段以及非基本类型字段的自定义对象字段 */
                else if (!TypeHelper.isEnumType(field.getType()) && !TypeHelper.isBasicType(field.getGenericType())) {
                    sb.append("\"" + field.getName() + "\": " + mockValue(field.getGenericType(), level + 1));
                } else {
                    sb.append("\"" + field.getName() + "\": " + mockValue(field.getGenericType(), level + 1));
                }
            }
            return sb.length() > 0 ? "{" + sb.toString() + "}": "{}";
        }

        return "目前无法解析" + type.getTypeName();
    }
}

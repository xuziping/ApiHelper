package com.xuzp.apidoc.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.xuzp.apidoc.properties.ApiDocProperties;
import com.xuzp.apidoc.properties.LoadProperties;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import static com.xuzp.apidoc.utils.Constants.*;

/**
 * @author za-xuzhiping
 * @Date 2017/12/7
 * @Time 17:25
 */
public class TypeHelper {

    /**
     * 生成易读的参数名称，去除包名和泛型修饰
     */
    public static String fixTypeName(String type) {
        if (type.endsWith(ENUM)) {
            return ENUM;
        }
        int i = type.indexOf("<");
        if (i != -1) {
            type = type.substring(0, i);
        }

        // 支持翻页对象
        if (type.endsWith(DOT + PAGABLE_TYPE)) {
            return PAGABLE_TYPE;
        }
        if (type.indexOf(DOT) != -1) {
            type = type.substring(type.lastIndexOf(DOT) + 1);
        }
        return type;
    }

    /**
     * 判断class/interface是否是集合类型
     */
    public static boolean isCollection(Class cls) {
        Type[] types = cls.getGenericInterfaces();
        for (Type type : types) {
            if (type.getTypeName().indexOf(COLLECTION_TYPE_NAME) != -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断class/interface是否是Map类型
     */
    public static boolean isMap(Class cls) {
        Type[] types = cls.getGenericInterfaces();
        for (Type type : types) {
            if (type.getTypeName().indexOf(MAP_TYPE_NAME) != -1) {
                return true;
            }
        }
        return cls.getTypeName().indexOf(MAP_TYPE_NAME) != -1;
    }

    /**
     * 判断是否是空类型(Void)
     */
    public static boolean isVoid(Class cls) {
        return (cls.getName().equals(Void.class.getName())) || cls.getName().equalsIgnoreCase(Constants.VOID);
    }

    /**
     * 判断类型是否是基础类型
     */
    public static boolean isBasicType(Type param) {
        return Lists
                .newArrayList(Integer.class.getName(), String.class.getName(), Double.class.getName(),
                        Float.class.getName(), Long.class.getName(), Boolean.class.getName(), Date.class.getName(),
                        Integer[].class.getName(), String[].class.getName(), Double[].class.getName(),
                        Float[].class.getName(), Long[].class.getName(), Boolean[].class.getName(),
                        Date[].class.getName(), int.class.getName(), double.class.getName(), float.class.getName(),
                        long.class.getName(), boolean.class.getName(), int[].class.getName(), double[].class.getName(),
                        float[].class.getName(), long[].class.getName(), boolean[].class.getName(), BigDecimal.class.getName(),
                        "?")
                .stream().map(TypeHelper::fixTypeName)
                .anyMatch(x -> x.equalsIgnoreCase(fixTypeName(param.getTypeName())));
    }

    /**
     * 判断是不是枚举类型
     */
    public static boolean isEnumType(Type type) {
        return type.getTypeName().endsWith(ENUM);
    }

    /**
     * 判断是不是分页包装类
     */
    public static boolean isPagableType(Type type) {
        ApiDocProperties properties = LoadProperties.getProperties();
        return TypeHelper.fixTypeName(type.getTypeName()).equalsIgnoreCase(properties.getPagableClassName());
    }

    /**
     * 收集类对象的所有字段，包括继承于父类中的字段
     */
    public static void getAllFields(Class cls, Set<Field> allFields) {
        Field[] fields = cls.getDeclaredFields();
        if (fields != null && fields.length > 0) {
            allFields.addAll(Sets.newHashSet(fields));
        }
        if (cls.getSuperclass() != null) {
            getAllFields(cls.getSuperclass(), allFields);
        }
    }
}

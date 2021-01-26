package com.huan.redisstat.common;

import org.springframework.beans.BeanUtils;

/**
 * @author lihuan
 */
public class MyBeanUtils extends BeanUtils {

    /**
     * 返回bean 复制
     * */
    public static <T>  T copyAndReturn(Object s, T t) {
        copyProperties(s, t);
        return t;
    }

    public static <T> T defaultBean(T obj, T def) {
        return obj == null ? def : obj;
    }
}

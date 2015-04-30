package com.mokylin.gm.scheduler.util;

import com.alibaba.dubbo.common.json.JSON;
import com.alibaba.dubbo.common.json.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author 李朝(Li.Zhao)
 * @since 2015/4/8.
 */

public abstract class JsonUtils {
    private static final Logger log = LoggerFactory.getLogger(JsonUtils.class);

    public static String json(Object o){
        if(o==null){
            return null;
        }
        try {
            return JSON.json(o);
        } catch (IOException e) {
            log.warn(e.getMessage(),e);
            return null;
        }
    }

    public static <T> T parse(String json,Class<T> clazz){
        if(json==null || clazz==null){
            return null;
        }
        try {
            return JSON.parse(json,clazz);
        } catch (ParseException e) {
            log.warn(e.getMessage(),e);
            return null;
        }
    }
}

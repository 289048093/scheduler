package com.mokylin.gm.scheduler.persist.dbm.util;

import com.mokylin.gm.scheduler.persist.dbm.annotation.EnumConverter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author 李朝(Li.Zhao)
 * @since 2014/11/20.
 */

public abstract class CustomConverter<T,E> implements EnumConverter<T,E>{

    private Class<T> fieldClazz;

    private Class<T> dbFieldClazz;

    public Class<T> getFieldClazz() {
        if(fieldClazz!=null){
            return fieldClazz;
        }
        Type[] types = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
        return fieldClazz = (Class<T>) types[0];
    }

    public Class<T> getDbFieldClazz() {
        if(dbFieldClazz!=null){
            return dbFieldClazz;
        }
        Type[] types = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
        return dbFieldClazz = (Class<T>) types[1];
    }
}

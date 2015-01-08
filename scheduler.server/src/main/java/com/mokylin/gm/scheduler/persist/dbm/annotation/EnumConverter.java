package com.mokylin.gm.scheduler.persist.dbm.annotation;

/**
 * @author 李朝(Li.Zhao)
 * @since 2014/11/20.
 */

public interface EnumConverter<T,V> {
    T convert(V v);
}

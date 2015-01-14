package com.mokylin.gm.scheduler.persist.dbm.annotation;

/**
 * 实体类与数据库枚举类型转换
 * @param <T> 实体字段类型
 * @param <V> 数据库字段类型
 * @author 李朝(Li.Zhao)
 * @since 2014/11/20.
 */
public interface EnumConverter<T,V> {

    V serialize(T v);

    T deSerialize(V t);
}

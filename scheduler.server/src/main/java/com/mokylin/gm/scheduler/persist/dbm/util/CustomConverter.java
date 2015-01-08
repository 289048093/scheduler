package com.mokylin.gm.scheduler.persist.dbm.util;

/**
 * @author 李朝(Li.Zhao)
 * @since 2014/11/20.
 */

public abstract class CustomConverter<T,E> {

    private Class<T> fieldClazz;

    public Class<T> getFieldClazz() {
        return fieldClazz;
    }

    public void setFieldClazz(Class<T> fieldClazz) {
        this.fieldClazz = fieldClazz;
    }

    public abstract  E serialize(T e);

    public abstract  T deSerialize(E o);
}

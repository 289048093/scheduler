package com.mokylin.gm.scheduler.persist.dbm.annotation;

/**
 * 枚举类型持久换取值类型
 *
 * @author 李朝(Li.Zhao)
 * @since 2014/11/20.
 */

public enum EnumValueType {
    /**
     * 取 {@link Enum#name()} 的值
     */
    NAME,
    /**
     * 取 {@link Enum#toString()} 的值
     */
    STRING,


    /**
     * 自定义
     */
    CUSTOM
}

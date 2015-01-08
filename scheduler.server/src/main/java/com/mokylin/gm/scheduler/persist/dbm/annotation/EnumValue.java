package com.mokylin.gm.scheduler.persist.dbm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 枚举类型持久化，默认取 {@link Enum#name()}的值
 * @author 李朝(Li.Zhao)
 * @since 2014/11/20.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumValue {
    EnumValueType value() default EnumValueType.NAME;

    /**
     * use {@link Custom}
     * @return
     */
    String method() default "";
}

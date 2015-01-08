package com.mokylin.gm.scheduler.persist.dbm.annotation;

import com.mokylin.gm.scheduler.persist.dbm.util.CustomConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 李朝(Li.Zhao)
 * @since 2014/11/20.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Custom {
    Class<? extends CustomConverter> converter();
}

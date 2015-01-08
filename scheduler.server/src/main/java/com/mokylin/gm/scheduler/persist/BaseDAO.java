package com.mokylin.gm.scheduler.persist;

import com.mokylin.gm.scheduler.util.DBUtils;

import java.sql.Connection;
import java.util.List;

/**
 * @author 李朝(Li.Zhao)
 * @since 2015/1/8.
 */

public abstract class BaseDAO<T> implements DAO<T> {

    protected Connection getConn(){
        return DBUtils.getConn();
    }
}

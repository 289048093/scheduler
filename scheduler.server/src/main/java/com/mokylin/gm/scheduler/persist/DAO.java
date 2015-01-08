package com.mokylin.gm.scheduler.persist;

import com.mokylin.gm.scheduler.entity.CronScheduler;

import java.sql.SQLException;
import java.util.List;

/**
 * @author 李朝(Li.Zhao)
 * @since 2015/1/8.
 */

public interface DAO<T> {

    long insert(T t) throws SQLException;

    void update(T t) throws SQLException;

    void delete(T t) throws SQLException;

    void delete(long id) throws SQLException;

    T get(long id) throws SQLException;

    List<T> findAll() throws SQLException;

    List<T> find(String sql, List<Object> values) throws SQLException;
}

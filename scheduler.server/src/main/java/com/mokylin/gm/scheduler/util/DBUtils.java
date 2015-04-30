package com.mokylin.gm.scheduler.util;

import com.mokylin.gm.scheduler.entity.CronScheduler;
import org.quartz.Scheduler;
import org.quartz.impl.StdScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

/**
 * @author 李朝(Li.Zhao)
 * @since 2015/1/7.
 */

public class DBUtils {
    private static final Logger log = LoggerFactory.getLogger(DBUtils.class);

    public static Connection getConn() {
        return ConnectionManager.getInstance().getConnection();
    }

}

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

    private static Connection conn;

    static {
        try {
            Class.forName(ConfigInfo.getInstance().getString(ConfigInfo.DB_DRIVER));
            ConfigInfo cfg = ConfigInfo.getInstance();
            String dbUrl = cfg.getString(ConfigInfo.DB_URL);
            String dbUsername = cfg.getString(ConfigInfo.DB_JDBC_USER);
            String dbPassword = cfg.getString(ConfigInfo.DB_JDBC_PASSWORD);
            String pattern = dbUrl.contains("?")?"%s&user=%s&password=%s":"%s?user=%s&password=%s";
            String url =  String.format(pattern, dbUrl, dbUsername, dbPassword);
            conn = DriverManager.getConnection(url);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public static Connection getConn() {
        return conn;
    }

}

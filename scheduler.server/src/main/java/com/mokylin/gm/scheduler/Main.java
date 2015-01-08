package com.mokylin.gm.scheduler;

import com.alibaba.dubbo.rpc.protocol.memcached.MemcachedProtocol;
import com.mokylin.gm.scheduler.entity.CronScheduler;
import com.mokylin.gm.scheduler.jobloader.ClassHelper;
import com.mokylin.gm.scheduler.persist.CronSchedulerDAO;
import com.mokylin.gm.scheduler.persist.DAO;
import com.mokylin.gm.scheduler.util.ConfigInfo;
import com.mokylin.gm.scheduler.util.Constant;
import com.mokylin.gm.scheduler.util.DBUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;


/**
 * @author 李朝(Li.Zhao)
 * @since 2015/1/4.
 */

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static Scheduler scheduler;

    private static JobManager jobManager = new JobManagerImpl();

    static {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static void init() {
        ConfigInfo.setConfigPath("scheduler.properties");

        ClassHelper.init();

        jobManager.initLoad();
    }

    private static void startDubbo(){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"provider.xml"});
        context.start();
    }

    public static void main(String[] args) {

        init();

        startDubbo();
    }


}

package com.mokylin.gm.scheduler;

import com.alibaba.dubbo.rpc.protocol.memcached.MemcachedProtocol;
import com.mokylin.gm.scheduler.entity.CronScheduler;
import com.mokylin.gm.scheduler.jobloader.ClassHelper;
import com.mokylin.gm.scheduler.persist.CronSchedulerDAO;
import com.mokylin.gm.scheduler.persist.DAO;
import com.mokylin.gm.scheduler.util.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * @author 李朝(Li.Zhao)
 * @since 2015/1/4.
 */

public class Main {

    private static String cfgDir;

    static {
        cfgDir = new File(Main.class.getClassLoader().getResource("").getPath()).getParent()+"/conf/";
    }


    private static void init() {
        DOMConfigurator.configure(cfgDir + "log4j.xml");

        SystemUtils.addClassPath(cfgDir);
        for(File file:new File(cfgDir).listFiles()){
            SystemUtils.addClassPath(file.getAbsolutePath());
        }
        ConfigInfo.setConfigPath(cfgDir + "scheduler.properties");
        ClassHelper.init();

//        try {
//            FileUtils.copyFile(new File(cfgDir + "provider.xml"), new File("file:"+cfgDir+"/provider.xml"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        JobManagerImpl.getInstance().initLoad();
    }

    private static void startDubbo(){

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("file:"+cfgDir+"provider.xml");
        context.start();
    }

    public static void main(String[] args) throws IOException {

        init();

        startDubbo();

    }


}

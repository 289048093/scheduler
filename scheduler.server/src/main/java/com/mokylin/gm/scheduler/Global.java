package com.mokylin.gm.scheduler;

import com.alibaba.dubbo.config.ApplicationConfig;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;

/**
 * @author 李朝(Li.Zhao)
 * @since 2015/1/19.
 */

public class Global {
    private static ClassPathXmlApplicationContext context = null;


    public static void startDubbo(String cfgDir){
        if(context!=null)return;
        context = new ClassPathXmlApplicationContext("file:"+cfgDir+"provider.xml");
        context.start();
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                context.stop();
                context.close();
            }
        });
    }

    public static ClassPathXmlApplicationContext getContext(){
        return context;
    }

    public static ApplicationConfig getApplicationConfig(){
        Map<?,ApplicationConfig> beans = Global.getContext().getBeansOfType(ApplicationConfig.class);
        return beans.values().iterator().next();
    }
}

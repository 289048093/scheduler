package com.mokylin.gm.scheduler.jobloader;

import com.mokylin.gm.scheduler.util.ConfigInfo;
import com.mokylin.gm.scheduler.util.FileHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author 李朝(Li.Zhao)
 * @since 2015/1/7.
 */

public class ClassHelper {
    private static final Logger log = LoggerFactory.getLogger(ClassHelper.class);

    public static DynamicClassLoader dClassLoader = new DynamicClassLoader(Thread.currentThread().getContextClassLoader());

    public static void init(){
        try {
            ConfigInfo.setConfigPath("scheduler.properties");
            String jobDir = FileHelper.getJobDir();
            dClassLoader.addFolder(jobDir);
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }
    }


    public static Class forName(String className) throws ClassNotFoundException {
            return dClassLoader.loadClass(className);
    }

}

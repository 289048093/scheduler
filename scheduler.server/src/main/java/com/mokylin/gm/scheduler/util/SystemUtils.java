package com.mokylin.gm.scheduler.util;

import java.io.File;

/**
 * @author 李朝(Li.Zhao)
 * @since 2015/1/9.
 */

public class SystemUtils {
    public static String getOsName(){
        return  System.getProperty("os.name");
    }

    public static void addClassPath(String... paths){
        String split = File.pathSeparator;
        String property = System.getProperty("java.class.path");
        StringBuilder classPath = new StringBuilder(property==null?"":property);
        for(String path:paths){
            classPath.append(split).append(path);
        }
        System.setProperty("java.class.path",classPath.toString());
    }
}

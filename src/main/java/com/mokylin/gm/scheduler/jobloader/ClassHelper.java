package com.mokylin.gm.scheduler.jobloader;

/**
 * @author 李朝(Li.Zhao)
 * @since 2015/1/7.
 */

public class ClassHelper {
    public static DynamicClassLoader dClassLoader = new DynamicClassLoader();

    public static Class forName(String className) throws ClassNotFoundException {
        try {
            return  Class.forName(className);
        } catch (ClassNotFoundException e) {
            return dClassLoader.loadClass(className);
        }
    }
}

package com.mokylin.gm.scheduler.jobloader;

import com.mokylin.gm.scheduler.util.ConfigInfo;
import com.mokylin.gm.scheduler.util.FileHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author 李朝(Li.Zhao)
 * @since 2015/1/7.
 */

public class ClassHelper {
    private static final Logger log = LoggerFactory.getLogger(ClassHelper.class);

    public static DynamicClassLoader dClassLoader;

    public static void init(){
        try {
            dClassLoader =  new DynamicClassLoader(Thread.currentThread().getContextClassLoader());
            Set<File> jobDirs = FileHelper.getAllJobDirs();
            for(File dir:jobDirs){
                dClassLoader.addFolder(dir.getAbsolutePath());
            }
            Thread.currentThread().setContextClassLoader(dClassLoader);
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }
    }


    public static Class forName(String className) throws ClassNotFoundException {
            return dClassLoader.loadClass(className);
    }

    public static void addNewClassPath(String... dirs) throws IOException {
        dClassLoader.addFolder(dirs);
    }

    public static void reloadJobPath(){
        try {
            Set<File> newJobDirs = FileHelper.getAllJobDirs();
            if(newJobDirs!=null){
                for(File dir:newJobDirs){
                    ClassHelper.addNewClassPath(dir.getAbsolutePath());
                }
            }

        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }
    }
}

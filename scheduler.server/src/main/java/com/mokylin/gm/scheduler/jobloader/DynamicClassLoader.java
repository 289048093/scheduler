/*
 *  Copyright Beijing 58 Information Technology Co.,Ltd.
 *
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package com.mokylin.gm.scheduler.jobloader;

import com.mokylin.gm.scheduler.util.ConfigInfo;
import com.mokylin.gm.scheduler.util.FileHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.security.SecureClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * A ClassLoader for dynamic load class from jar
 *
 * @author Service Platform Architecture Team (spat@58.com)
 */
public class DynamicClassLoader extends SecureClassLoader {

    private static Logger logger = LoggerFactory.getLogger(DynamicClassLoader.class);

    /**
     * jar list load class from this
     */
    private static Set<String> jarList = new HashSet<>();

    private static Set<String> foldList = new HashSet<>();

    /**
     * class cache
     */
    private Map<String,Class<?>> classCache = new HashMap<String,Class<?>>();


    public DynamicClassLoader(ClassLoader parent){
        super(parent);
    }

    public DynamicClassLoader() {

    }

    /**
     * dynamic find class from jar
     * @param jarPath
     * @param className
     * @param fromCache
     * @return
     * @throws ClassNotFoundException
     */
    public Class<?> findClass(String jarPath, String className, boolean fromCache) throws ClassNotFoundException {

        logger.debug("find class jarPath: " + jarPath + "  className: " + className + "  fromCache:" + fromCache);

        if(fromCache && classCache.containsKey(className)) {
            return classCache.get(className);
        }

        String classPath = className.replace('.', '/').concat(".class");
        byte[] clsByte = null;
        if(jarPath==null || jarPath.equalsIgnoreCase("")) {
            for(String jp : jarList) {
                jarPath = jp;
                clsByte = getClassByte(jp, classPath);
                if(clsByte != null) {
                    break;
                }
            }
//			clsByte = getClassByte(classPath);
        } else {
            clsByte = getClassByte(jarPath, classPath);
        }

        if(clsByte == null) {
            throw new ClassNotFoundException(className);
        }

        URL url = null;
        try {
            url = new URL("file", "", jarPath);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return findClass(className, clsByte, url);
    }

        @Override
    public URL getResource(String name) {
        URL resource = super.getResource(name);
        if (resource == null) {
            File file = null;
            for (String fold : foldList) {
                file = new File(fold, name);
                if (file.exists()) {
                    try {
                        resource = file.toURI().toURL();
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                    return resource;
                }
            }
        }
        return resource;
    }


    /**
     *
     * @param className
     * @param clsByte
     * @return
     */
    public Class<?> findClass(String className, byte[] clsByte, URL url) {
        Class<?> cls = null;
        try {
            CodeSource cs = new CodeSource(url, (java.security.cert.Certificate[]) null);
            ProtectionDomain pd = new ProtectionDomain(cs, null, this, null);
            cls = super.defineClass(className, clsByte, 0, clsByte.length, pd);
            resolveClass(cls);
            classCache.put(className, cls);
        } catch(Exception ex) {
            logger.error("define class error", ex);
        }

        return cls;
    }

    /**
     * dynamic find class from jar
     * @param jarPath
     * @param className
     * @return
     * @throws ClassNotFoundException
     */
    public Class<?> findClass(String jarPath, String className) throws ClassNotFoundException {
        return findClass(jarPath, className, true);
    }

    /**
     * dynamic find class from jar
     */
    @Override
    public Class<?> findClass(String className) throws ClassNotFoundException {
        return findClass("", className, true);
    }

    /**
     * dynamic find class from jar
     * @param className
     * @param fromCache
     * @return
     * @throws ClassNotFoundException
     */
    public Class<?> findClass(String className, boolean fromCache) throws ClassNotFoundException {
        return findClass("", className, fromCache);
    }

    /**
     * clear all class cache
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void clearAllClassCache(){
        logger.info("clear class cache:");
        try {
            Iterator it = classCache.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Class<?>> entry = (Map.Entry<String, Class<?>>) it.next();
                logger.debug("-----key:" + entry.getKey() + "  value:" + entry.getValue().getName());
            }
        } catch(Exception ex) {
            logger.error(ex.getMessage(),ex);
        }
        classCache.clear();
    }

    /**
     * add jar url
     * @param url
     * @throws Exception
     */
    public void addURL(String url) {
        if(!jarList.contains(url)){
            jarList.add(url);
//			try {
//				ucp.addURL(new URL("file", "", url));
//			} catch (MalformedURLException e) {
//				e.printStackTrace();
//			}
        }
    }

    /**
     * add folder jars
     * @param dirs
     * @throws IOException
     */
    public void addFolder(String... dirs) throws IOException {
        Set<String> jarList = FileHelper.getUniqueLibPath(dirs);
        Collections.addAll(foldList,dirs);
        for(String jar : jarList) {
            addURL(jar);
        }
    }

    /**
     * get jar list
     * @return
     */
    public Set<String> getJarList(){
        return jarList;
    }

//	/**
//	 * get class byte[] from jarList
//	 * @param classPath
//	 * @return
//	 */
//	private byte[] getClassByte(String classPath) {
//		byte[] clsByte = null;
//		for(String jarPath : jarList) {
//			clsByte = getClassByte(jarPath, classPath);
//			if(clsByte != null) {
//				break;
//			}
//		}
//		return clsByte;
//	}
//	
    /**
     * get class byte from jarPath
     * @param jarPath
     * @param classPath
     * @return
     */
    private byte[] getClassByte(String jarPath, String classPath) {
        JarFile jarFile = null;
        InputStream input = null;
        byte[] clsByte = null;
        try {
            jarFile = new JarFile(jarPath);  // read jar
            JarEntry entry = jarFile.getJarEntry(classPath); // read class file
            if(entry != null) {
                logger.debug("get class:" + classPath + "  from:" + jarPath);
                input = jarFile.getInputStream(entry);
                clsByte = new byte[input.available()];
                input.read(clsByte);
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(input != null) {
                try {
                    input.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            if(jarFile != null) {
                try {
                    jarFile.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return clsByte;
    }

    public static void main(String[] args) throws IllegalAccessException, InstantiationException, ClassNotFoundException, IOException {
        ConfigInfo.setConfigPath("E:\\workspace\\gm_platform\\gm_gccp\\code\\trunk\\scheduler\\scheduler.server\\target\\conf\\scheduler.properties");
        DynamicClassLoader cl = new DynamicClassLoader(Thread.currentThread().getContextClassLoader());
        Thread.currentThread().setContextClassLoader(cl);
        cl.addFolder("E:\\workspace\\gm_platform\\gm_gccp\\code\\trunk\\scheduler\\scheduler.server\\target\\jobs\\gmjob-impl");
        Class<?> aClass = cl.loadClass("com.mokylin.gm.job.GmJob");
        Object o = aClass.newInstance();
    }
}
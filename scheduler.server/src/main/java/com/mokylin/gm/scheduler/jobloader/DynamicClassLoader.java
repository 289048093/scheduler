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

import com.mokylin.gm.scheduler.util.FileHelper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
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
public class DynamicClassLoader extends URLClassLoader {

    private static final Logger logger = LoggerFactory.getLogger(DynamicClassLoader.class);

    /**
     * jar list load class from this
     */
    private Set<String> jarList = new HashSet<>();


    private Set<String> foldList = new HashSet<>();

    /**
     * class cache
     */
    private Map<String, Class<?>> classCache = new HashMap<String, Class<?>>();

    public DynamicClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
        addUrlFile(urls);
    }

    public DynamicClassLoader(URL[] urls) {
        super(urls);
        addUrlFile(urls);
    }

    public DynamicClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
        addUrlFile(urls);
    }


    public DynamicClassLoader(ClassLoader parent) {
        super(new URL[]{}, parent);
    }

    private void addUrlFile(URL[] urls) {
        for (URL u : urls) {
            String fileStr = u.getFile();
            File file = new File(fileStr);
            if (file.exists() && file.isDirectory()) {
                foldList.add(fileStr);
            } else {
                jarList.add(fileStr);
            }
        }
    }

    public DynamicClassLoader() {
        super(new URL[]{});
    }


//    @Override
//    public URL getResource(String name) {
//        URL resource = super.getResource(name);
//        if (resource == null) {
//            File file = null;
//            for (String fold : foldList) {
//                file = new File(fold, name);
//                if (file.exists()) {
//                    try {
//                        resource = file.toURI().toURL();
//                    } catch (Exception e) {
//                        logger.error(e.getMessage(), e);
//                    }
//                    return resource;
//                }
//            }
//        }
//        return resource;
//    }

    /**
     * add jar url
     *
     * @param url
     * @throws Exception
     */
    public void addURL(String url) throws MalformedURLException {
        if (!jarList.contains(url)) {
            File file = new File(url);
            if(file.exists() && file.isFile()){
                jarList.add(url);
            }
            if(file.exists() && file.isDirectory()){
                foldList.add(url);
            }
            addURL(file.toURI().toURL());
        }
    }

    /**
     * add folder jars
     *
     * @param dirs
     * @throws java.io.IOException
     */
    public void addFolder(String... dirs) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("class load add dir path:", Arrays.toString(dirs));
        }
        Collections.addAll(foldList, dirs);
        for(String dir:dirs){
            addURL(dir);
        }
        Set<String> jarList = FileHelper.getAllPath(dirs);
        for (String jar : jarList) {
            addURL(jar);
        }
    }

    /**
     * get jar list
     *
     * @return
     */
    public Collection<String> getJarList() {
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
     *
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
            if (entry != null) {
                logger.debug("get class:" + classPath + "  from:" + jarPath);
                input = jarFile.getInputStream(entry);
                clsByte = new byte[input.available()];
                input.read(clsByte);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(jarFile);
        }
        return clsByte;
    }
}
package com.mokylin.gm.scheduler.util;

/**
 * @author 李朝(Li.Zhao)
 * @since 2015/1/7.
 */

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * @author Service Platform Architecture Team (spat@58.com)
 */
public class FileHelper {

    private static final Logger log = LoggerFactory.getLogger(FileHelper.class);

    /**
     * create file
     *
     * @param filePath
     * @param content
     * @throws java.io.IOException
     */
    public static void createFile(String filePath, String content) throws IOException {
        FileWriter writer = null;
        try {
            writer = new FileWriter(filePath);
            writer.write(content);
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    /**
     * create multilevel folder
     *
     * @param path
     */
    public static void createFolder(String path) {
        File file = new File(path);

    }

    public static void createFolder(File file) {
        file = file.getAbsoluteFile();
        if (file.exists()) {
            if (file.isDirectory()) {
                return;
            }
            boolean delete = file.delete();
            if (!delete) {
                log.error("can not create dir:{},exist file named {} and can not delete", file.getName(), file.getName());
            }
        }
        File parent = file.getParentFile();
        if (!parent.exists()) {
            createFolder(parent);
        }
        boolean mkdir = file.mkdir();
        if (!mkdir) file.mkdir();
    }

    /**
     * move file
     *
     * @param oldPath
     * @param newPath
     */
    public static void moveFile(String oldPath, String newPath) {
        File fileOld = new File(oldPath);
        if (fileOld.exists()) {
            File fileNew = new File(newPath);
            fileOld.renameTo(fileNew);
        }
    }

    /**
     * delete file
     *
     * @param path
     */
    public static void deleteFile(String path) {
        File file = new File(path);
        file.deleteOnExit();
    }

    /**
     * get all file which in dir
     *
     * @param dir
     * @param extension
     * @return
     */
    public static List<File> getFiles(String dir, String... extension) {
        File f = new File(dir);
        if (!f.isDirectory()) {
            return null;
        }

        List<File> fileList = new ArrayList<File>();
        getFiles(f, fileList, extension);

        return fileList;
    }


    /**
     * get all jar/war/ear which in dir
     *
     * @param dirs
     * @return
     * @throws java.io.IOException
     */
    public static Set<String> getUniqueLibPath(String... dirs) throws IOException {
//        return getUniqueLibPath(dirs,"rar", "jar", "war", "ear","class","xml","properties");
        return getUniqueLibPath(dirs,"rar", "jar", "war", "ear");
    }

    public static Set<String> getAllPath(String... dirs) throws IOException {
//        return getUniqueLibPath(dirs,"rar", "jar", "war", "ear","class","xml","properties");
        return getUniqueLibPath(dirs,null);
    }


    /**
     * get all jar/war/ear which in dir
     *
     * @param dirs
     * @return
     * @throws java.io.IOException
     */
    public static Set<String> getUniqueLibPath(String[] dirs,String... ext) throws IOException {

        Set<String> jarList = new HashSet<String>();
        List<String> fileNameList = new ArrayList<String>();

        for (String dir : dirs) {
            List<File> fileList = FileHelper.getFiles(dir, ext);
            if (fileList != null) {
                for (File file : fileList) {
                    if (!fileNameList.contains(file.getName())) {
                        jarList.add(file.getCanonicalPath());
                        fileNameList.add(file.getName());
                    }
                }
            }
        }

        return jarList;
    }

    /**
     * @param f
     * @param fileList
     * @param extension
     */
    private static void getFiles(File f, List<File> fileList, String... extension) {
        File[] files = f.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    fileList.add(file);
                    getFiles(file, fileList, extension);
                } else if (file.isFile()) {

                    String fileName = file.getName().toLowerCase();
                    boolean isAdd = false;
                    if (extension != null) {
                        for (String ext : extension) {
                            if (fileName.endsWith(ext)) {
                                isAdd = true;
                                break;
                            }
                        }
                    }
                    if(extension == null){
                        isAdd = true;
                    }

                    if (isAdd) {
                        fileList.add(file);
                    }
                }
            }
        }
    }

    /**
     * 按行读取文件
     *
     * @param path
     * @throws java.io.IOException
     */
    public static String getContentByLines(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            throw new IOException("file not exist:" + path);
        }
        BufferedReader reader = null;
        StringBuilder sbContent = new StringBuilder();
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                sbContent.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sbContent.toString();
    }


    public static String getContextPath() {
        //noinspection ConstantConditions
        return FileHelper.class.getClassLoader().getResource("").getPath();
    }

    private static Set<File> allJobDirs = new HashSet<>();
    static{
        try {
            allJobDirs = getAllJobDirs();
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }
    }


    public static Set<File> getAllJobDirs() throws IOException {
        String jobDir = ConfigInfo.getInstance().getString(ConfigInfo.JOB_DIR);
        if (StringUtils.isBlank(jobDir)) {
            jobDir = new File(getContextPath(), ConfigInfo.DEFAULT_JOB_DIR).getCanonicalPath();
        }
        File dir = new File(jobDir);
        if (!dir.exists()) {
            createFolder(dir);
        }
        Collections.addAll(allJobDirs, dir.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        }));
        return allJobDirs;
    }

    public static Set<File> getNewJobDirs() throws IOException {
        String jobDir = ConfigInfo.getInstance().getString(ConfigInfo.JOB_DIR);
        if (StringUtils.isBlank(jobDir)) {
            jobDir = new File(getContextPath(), ConfigInfo.DEFAULT_JOB_DIR).getCanonicalPath();
        }
        File dir = new File(jobDir);
        if (!dir.exists()) {
            createFolder(dir);
        }
        Set<File> files = Sets.newHashSet(dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File dir) {
                return dir.isDirectory() && !allJobDirs.contains(dir);
            }
        }));
        allJobDirs.addAll(files);
        return files;
    }

}

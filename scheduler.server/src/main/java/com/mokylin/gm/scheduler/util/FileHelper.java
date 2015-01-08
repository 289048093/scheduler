package com.mokylin.gm.scheduler.util;

/**
 * @author 李朝(Li.Zhao)
 * @since 2015/1/7.
 */

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        Set<String> jarList = new HashSet<String>();
        List<String> fileNameList = new ArrayList<String>();

        for (String dir : dirs) {
            List<File> fileList = FileHelper.getFiles(dir, "rar", "jar", "war", "ear");
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
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                getFiles(files[i], fileList, extension);
            } else if (files[i].isFile()) {

                String fileName = files[i].getName().toLowerCase();
                boolean isAdd = false;
                if (extension != null) {
                    for (String ext : extension) {
                        if (fileName.lastIndexOf(ext) == fileName.length() - ext.length()) {
                            isAdd = true;
                            break;
                        }
                    }
                }

                if (isAdd) {
                    fileList.add(files[i]);
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

    public static String getJobDir() throws IOException {
        String jobDir = ConfigInfo.getInstance().getString(ConfigInfo.JOB_DIR);
        if (StringUtils.isBlank(jobDir)) {
            jobDir = new File(getContextPath(), ConfigInfo.DEFAULT_JOB_DIR).getCanonicalPath();
        }
        File dir = new File(jobDir);
        if (!dir.exists()) {
            createFolder(dir);
        }
        return jobDir;
    }

}

package com.mokylin.gm.scheduler.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author lizhao  2014/9/17.
 */

public class ConfigInfo {
    private final Logger log = LoggerFactory.getLogger(ConfigInfo.class);

    public static final String DB_DRIVER = "db_driver";
    public final static String DB_JDBC_USER = "db_jdbc_user";
    public final static String DB_JDBC_PASSWORD = "db_jdbc_password";
    public final static String DB_URL = "db_url";
    public final static String DB_PORT = "db_port";
    public final static String DB_NAME = "db_name";
    public final static String DB_POOL_SIZE = "db_pool_size";
    public final static String DB_BLOCK_SIZE = "db_block_size";
    public static final String UPLOAD_TMP_DIR = "upload_tmp_dir";



    private Properties prop = null;

    private static ScriptEngineManager manager = new ScriptEngineManager();
    private static ScriptEngine jsEngine = manager.getEngineByName("JavaScript");


    private static String configPath;

    public static String getConfigPath() {
        return configPath;
    }

    public static void setConfigPath(String configPath) {
        ConfigInfo.configPath = configPath;
    }

    private Object getEval(Enum key) throws ScriptException {
        return getEval(key.toString());
    }

    private Object getEval(String key) throws ScriptException {
        String val = getString(key);
        if (val == null) {
            return null;
        }
        return jsEngine.eval(val);
    }

    private static final Map<String, ConfigInfo> cfgs = new HashMap<>();

    private ConfigInfo(String fileName) {
        prop = new Properties();
        InputStream is = ConfigInfo.class.getClassLoader().getResourceAsStream(fileName);
        try {
            prop.load(is);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static ConfigInfo getInstance() {
       String cfgFileName = getConfigPath();
        ConfigInfo res = null;
        if ((res = cfgs.get(cfgFileName)) == null) {
            synchronized (cfgs) {
                if ((res = cfgs.get(cfgFileName)) == null) {
                    res = new ConfigInfo(cfgFileName);
                    cfgs.put(cfgFileName, res);
                }
            }
        }
        return res;
    }


    public String getString(Enum key) {
        return prop.getProperty(key.toString());
    }

    public long getLong(Enum key) {
        Object o = prop.get(key);
        if (o instanceof Long) {
            return (Long) o;
        }
        try {
            Object eval = getEval(key);
            if (eval instanceof Number) {
                long i = ((Number) eval).longValue();
                prop.put(key, i);
                return i;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return 0L;
    }

    public int getInt(Enum key) {
        return getInt(key.toString());
    }

    public Properties getProp() {
        return prop;
    }

    public String getString(String str) {
        return prop.getProperty(str);
    }


    public int getInt(String key) {
        Object o = prop.get(key);
        if (o instanceof Integer) {
            return (Integer) o;
        }
        try {
            Object eval = getEval(key);
            if (eval instanceof Number) {
                int i = ((Number) eval).intValue();
                prop.put(key, i);
                return i;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return 0;
    }

    public Map<String, String> getStringByRegex(String regex) {
        Map<String, String> res = new HashMap<>();
        for (Object key : prop.keySet()) {
            if (key.toString().matches(regex)) {
                res.put(key.toString(), getString(key.toString()));
            }
        }
        return res;
    }

    public Map<String, Integer> getIntByRegex(String regex) {
        Map<String, Integer> res = new HashMap<>();
        for (Object key : prop.keySet()) {
            if (key.toString().matches(regex)) {
                res.put(key.toString(), getInt(key.toString()));
            }
        }
        return res;
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(getString(key));
    }

}

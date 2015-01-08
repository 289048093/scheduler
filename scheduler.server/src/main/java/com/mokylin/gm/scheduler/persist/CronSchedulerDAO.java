package com.mokylin.gm.scheduler.persist;

import com.google.common.base.Joiner;
import com.mokylin.gm.scheduler.entity.CronScheduler;
import com.mokylin.gm.scheduler.persist.dbm.annotation.ID;
import com.mokylin.gm.scheduler.persist.dbm.cache.ClassInfoCache;
import com.mokylin.gm.scheduler.persist.dbm.util.DBMUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

/**
 * @author 李朝(Li.Zhao)
 * @since 2015/1/8.
 */

public class CronSchedulerDAO extends BaseDAO<CronScheduler> {

    private static final Logger log = LoggerFactory.getLogger(CronScheduler.class);

    private static String getTableName() {
        return DBMUtils.getDbTableName(CronScheduler.class);
    }

    static Field idField;
    static List<Field> otherFields = new ArrayList<>();

    static {
        Collection<Field> persistFields = ClassInfoCache.getPersistFields(CronScheduler.class);
        for (Field field : persistFields) {
            if (ClassInfoCache.getAnnotation(field, ID.class) != null) {
                idField = field;
                continue;
            }
            otherFields.add(field);
        }
    }

    private static String insertSql = null;

    private String getInsertSql() {
        if (insertSql != null) {
            return insertSql;
        }
        List<String> dbCols = new LinkedList<>();
        for (Field field : otherFields) {
            dbCols.add(DBMUtils.getDBColumnName(field));
        }
        return insertSql = String.format("insert into %s(%s) values(%s) ",
                getTableName(),
                Joiner.on(",").join(dbCols),
                StringUtils.repeat("?,", dbCols.size()).replaceAll(",$", ""));
    }

    @Override
    public long insert(CronScheduler cronScheduler) throws SQLException {
        Connection conn = getConn();
        String sql = getInsertSql();
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        for (int i = 0; i < otherFields.size(); i++) {
            try {
                stmt.setObject(i + 1, otherFields.get(i).get(cronScheduler));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        stmt.execute();

        ResultSet generatedKeys = stmt.getGeneratedKeys();
        return generatedKeys.next() ? generatedKeys.getLong(1) : -1;
    }

    private static String updateSql;

    private static String getUpdateSql() throws SQLException {
        if (updateSql != null) {
            return updateSql;
        }
        List<String> dbCols = new LinkedList<>();
        for (Field field : otherFields) {
            dbCols.add(DBMUtils.getDBColumnName(field));
        }
        if (idField == null) {
            throw new SQLException("must allocate identity field !");
        }
        return updateSql = String.format("update %s set %s where %s=?",
                getTableName(),
                Joiner.on("=?,").join(dbCols)+"=? ",
                DBMUtils.getDBColumnName(idField));

    }

    @Override
    public void update(CronScheduler cronScheduler) throws SQLException {
        if (cronScheduler.getId() == 0) {
            throw new SQLException("update entity id can not null");
        }
        PreparedStatement stmt = getConn().prepareStatement(getUpdateSql());
        int i;
        for (i = 0; i < otherFields.size(); i++) {
            try {
                stmt.setObject(i + 1, otherFields.get(i).get(cronScheduler));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        try {
            stmt.setObject(i+1, idField.get(cronScheduler));
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        }
        stmt.executeUpdate();
    }

    private static String deleteSql;

    private static String getDeleteSql() {
        if (deleteSql != null) {
            return deleteSql;
        }
        return deleteSql = String.format("delete from %s where %s=?", getTableName(), DBMUtils.getDBColumnName(idField));
    }

    @Override
    public void delete(CronScheduler cronScheduler) throws SQLException {
        delete(cronScheduler.getId());
    }

    @Override
    public void delete(long id) throws SQLException {
        PreparedStatement stmt = getConn().prepareStatement(getDeleteSql());
        stmt.setLong(1, id);
        stmt.execute();
    }

    private static String getSql;

    private static String getLoadSql() {
        if (getSql != null) {
            return getSql;
        }
        List<String> dbCols = new LinkedList<>();
        for (Field field : otherFields) {
            dbCols.add(DBMUtils.getDBColumnName(field));
        }
        return getSql = String.format("select %s,%s from %s where %s=?",
                DBMUtils.getDBColumnName(idField),
                Joiner.on(",").join(dbCols),
                DBMUtils.getDbTableName(CronScheduler.class),
                DBMUtils.getDBColumnName(idField));

    }

    @Override
    public CronScheduler get(long id) throws SQLException {
        PreparedStatement stmt = getConn().prepareStatement(getLoadSql());
        stmt.setLong(1, id);
        ResultSet rs = stmt.executeQuery();
        CronScheduler cs = null;
        if (rs.next()) {
            cs = new CronScheduler();
            cs.setId(rs.getLong(DBMUtils.getDBColumnName(idField)));
            for (Field field : otherFields) {
                copyValue(rs, cs, field);
            }
        }
        return cs;
    }

    private boolean copyValue(ResultSet rs, CronScheduler cs, Field field) throws SQLException {
        try {
            String dbColumnName = DBMUtils.getDBColumnName(field);
            Class<?> fieldClass = field.getType();
            if (Integer.class.equals(fieldClass)
                    || int.class.equals(fieldClass)) {
                field.set(cs, rs.getInt(dbColumnName));
                return true;
            }
            if (Short.class.equals(fieldClass)
                    || short.class.equals(fieldClass)) {
                field.set(cs, rs.getShort(dbColumnName));
                return true;
            }
            if (Byte.class.equals(fieldClass)
                    || byte.class.equals(fieldClass)) {
                field.set(cs, rs.getByte(dbColumnName));
                return true;
            }
            if (Long.class.equals(fieldClass)
                    || long.class.equals(fieldClass)) {
                field.set(cs, rs.getLong(dbColumnName));
                return true;
            }
            if (Double.class.equals(fieldClass)
                    || double.class.equals(fieldClass)) {
                field.set(cs, rs.getDouble(dbColumnName));
                return true;
            }
            if (Float.class.equals(fieldClass)
                    || float.class.equals(fieldClass)) {
                field.set(cs, rs.getFloat(dbColumnName));
                return true;
            }
            if (Boolean.class.equals(fieldClass)
                    || boolean.class.equals(fieldClass)) {
                field.set(cs, rs.getBoolean(dbColumnName));
                return true;
            }
            field.set(cs, rs.getObject(dbColumnName));
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        }
        return false;

    }


    private static String findAllSql;

    private static String getFindAllSql() {
        if (findAllSql != null) {
            return findAllSql;
        }
        List<String> dbCols = new LinkedList<>();
        for (Field field : otherFields) {
            dbCols.add(DBMUtils.getDBColumnName(field));
        }
        return findAllSql = String.format("select %s,%s from %s ",
                DBMUtils.getDBColumnName(idField),
                Joiner.on(",").join(dbCols),
                DBMUtils.getDbTableName(CronScheduler.class));

    }

    @Override
    public List<CronScheduler> findAll() throws SQLException {
        String sql = String.format(getFindAllSql());
        PreparedStatement stmt = getConn().prepareStatement(getFindAllSql());
        ResultSet rs = stmt.executeQuery();
        CronScheduler cs = null;
        List<CronScheduler> list = new LinkedList<>();
        while (rs.next()) {
            cs = new CronScheduler();
            cs.setId(rs.getLong(DBMUtils.getDBColumnName(idField)));
            for (Field field : otherFields) {
                copyValue(rs, cs, field);
            }
            list.add(cs);
        }
        return list;
    }

    @Override
    public List<CronScheduler> find(String sql, List<Object> values) throws SQLException {
        PreparedStatement stmt = getConn().prepareStatement(sql);
        if (values != null) {
            for (int i = 0; i < values.size(); i++) {
                Object obj = values.get(i);
                stmt.setObject(i + 1, values.get(i));
            }
        }
        ResultSet rs = stmt.executeQuery();
//        stmt.setObject();
        CronScheduler cs = null;
        List<CronScheduler> list = new LinkedList<>();
        while (rs.next()) {
            cs = new CronScheduler();
            cs.setId(rs.getLong(DBMUtils.getDBColumnName(idField)));
            for (Field field : otherFields) {
                copyValue(rs, cs, field);
            }
            list.add(cs);
        }
        return list;
    }
}

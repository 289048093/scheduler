package com.mokylin.gm.scheduler.persist;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.mokylin.gm.scheduler.entity.CronScheduler;
import com.mokylin.gm.scheduler.persist.dbm.annotation.Custom;
import com.mokylin.gm.scheduler.persist.dbm.annotation.EnumValue;
import com.mokylin.gm.scheduler.persist.dbm.annotation.EnumValueType;
import com.mokylin.gm.scheduler.persist.dbm.annotation.ID;
import com.mokylin.gm.scheduler.persist.dbm.cache.ClassInfoCache;
import com.mokylin.gm.scheduler.persist.dbm.util.CustomConverter;
import com.mokylin.gm.scheduler.persist.dbm.util.DBMUtils;
import com.mokylin.gm.scheduler.rpc.dto.JobStatus;
import com.mokylin.gm.scheduler.rpc.dto.Page;
import com.mokylin.gm.scheduler.rpc.dto.SchedulerDTO;
import com.mokylin.gm.scheduler.util.DBUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;
import java.util.Date;

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

    private static CronSchedulerDAO dao = new CronSchedulerDAO();

    private CronSchedulerDAO() {
    }

    public static CronSchedulerDAO getInstance() {
        return dao;
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
        try {
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
            long id = generatedKeys.next() ? generatedKeys.getLong(1) : -1;
            cronScheduler.setId(id);
            return id;
        } finally {
            conn.close();
        }
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
                Joiner.on("=?,").join(dbCols) + "=? ",
                DBMUtils.getDBColumnName(idField));

    }

    @Override
    public void update(CronScheduler cronScheduler) throws SQLException {
        if (cronScheduler.getId() == 0) {
            throw new SQLException("update entity id can not null");
        }
        Connection conn = getConn();
        try {
            PreparedStatement stmt = conn.prepareStatement(getUpdateSql());
            int i;
            for (i = 0; i < otherFields.size(); i++) {
                try {
                    stmt.setObject(i + 1, otherFields.get(i).get(cronScheduler));
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            try {
                stmt.setObject(i + 1, idField.get(cronScheduler));
            } catch (IllegalAccessException e) {
                log.error(e.getMessage(), e);
            }
            stmt.executeUpdate();
        } finally {
            conn.close();
        }
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
        Connection conn = getConn();
        try {
            PreparedStatement stmt = conn.prepareStatement(getDeleteSql());
            stmt.setLong(1, id);
            stmt.execute();
        } finally {
            conn.close();
        }
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
        Connection conn = getConn();
        try {
            PreparedStatement stmt = conn.prepareStatement(getLoadSql());
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            CronScheduler cs = null;
            if (rs.next()) {
                cs = getEntity(rs);
            }
            return cs;
        } finally {
            conn.close();
        }
    }

    private CronScheduler getEntity(ResultSet rs) throws SQLException {
        CronScheduler cs = new CronScheduler();
        cs.setId(rs.getLong(DBMUtils.getDBColumnName(idField)));
        for (Field field : otherFields) {
            Class<?> fieldClass = field.getType();
            String dbColumnName = DBMUtils.getDBColumnName(field);
            Object fieldVal = null;
            try {
                if (Enum.class.isAssignableFrom(fieldClass)) {
                    Custom customAnn = ClassInfoCache.getAnnotation(field, Custom.class);
                    if (customAnn != null) {
                        Class<? extends CustomConverter> converterClazz = customAnn.value();
                        CustomConverter converter = ClassInfoCache.getSingleton(converterClazz);
                        Class dbFieldClazz = converter.getDbFieldClazz();
                        Object dbVal = copyValue(rs, dbFieldClazz, dbColumnName);
                        fieldVal = converter.deSerialize(dbVal);
                    } else {
                        fieldVal = getEnumValue(field, rs);
                    }
                } else {
                    fieldVal = copyValue(rs, field.getType(), dbColumnName);
                }
                field.set(cs, fieldVal);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

        }
        return cs;
    }

    private Object getEnumValue(Field field, ResultSet rs) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        EnumValue enumAnn = ClassInfoCache.getAnnotation(field, EnumValue.class);
        String dbColumnName = DBMUtils.getDBColumnName(field);
        EnumValueType annValue = enumAnn == null ? EnumValueType.NAME : enumAnn.value();
        //noinspection unchecked
        Class<? extends Enum> fieldEnum = (Class<? extends Enum>) field.getType();
        try {
            switch (annValue) {
                case NAME:
                    return Enum.valueOf(fieldEnum, rs.getString(dbColumnName));
                case STRING:
                    return getEnumByToString(fieldEnum, rs.getString(dbColumnName));
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    private Object getEnumByToString(Class<? extends Enum> enumClass, Object fieldValue) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = ClassInfoCache.getMethod(enumClass, "values");
        Object[] objs = (Object[]) method.invoke(enumClass);
        for (Object obj : objs) {
            if (obj.equals(fieldValue)) {
                return obj;
            }
        }
        return null;
    }

    private Object getEnumByMethodValue(String methodName, Class<? extends Enum> enumClass, Object fieldValue) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Method valuesMethod = ClassInfoCache.getMethod(enumClass, "values");
        Object[] enums = (Object[]) valuesMethod.invoke(enumClass);
        Method getDBValueMethod = ClassInfoCache.getMethod(enumClass, methodName);
        fieldValue = convertVal(getDBValueMethod.getReturnType(), fieldValue);
        for (Object obj : enums) {
            Object enumMethodReturnValue = getDBValueMethod.invoke(obj);
            if (enumMethodReturnValue.equals(fieldValue)) {
                return obj;
            }
        }
        return null;
    }


    private Object convertVal(Class<?> fieldClass, Object fieldVal) {
        if (fieldVal == null) return null;
        if (Integer.class.equals(fieldClass)
                || int.class.equals(fieldClass)
                && fieldVal instanceof Number) {
            return ((Number) fieldVal).intValue();
        }
        if (Short.class.equals(fieldClass)
                || short.class.equals(fieldClass)
                && fieldVal instanceof Number) {
            return ((Number) fieldVal).shortValue();
        }
        if (Byte.class.equals(fieldClass)
                || byte.class.equals(fieldClass)
                && fieldVal instanceof Number) {
            return ((Number) fieldVal).byteValue();
        }
        if (Long.class.equals(fieldClass)
                || long.class.equals(fieldClass)
                && fieldVal instanceof Number) {
            return ((Number) fieldVal).longValue();
        }
        if (Double.class.equals(fieldClass)
                || double.class.equals(fieldClass)
                && fieldVal instanceof Number) {
            return ((Number) fieldVal).doubleValue();
        }
        if (Float.class.equals(fieldClass)
                || float.class.equals(fieldClass)
                && fieldVal instanceof Number) {
            return ((Number) fieldVal).floatValue();
        }
        return fieldVal;
    }

    private Object copyValue(ResultSet rs, Class<?> fieldClass, String dbColumnName) throws SQLException {

        if (Integer.class.equals(fieldClass)
                || int.class.equals(fieldClass)) {
            return rs.getInt(dbColumnName);
        }
        if (Short.class.equals(fieldClass)
                || short.class.equals(fieldClass)) {
            return rs.getShort(dbColumnName);
        }
        if (Byte.class.equals(fieldClass)
                || byte.class.equals(fieldClass)) {
            return rs.getByte(dbColumnName);
        }
        if (Long.class.equals(fieldClass)
                || long.class.equals(fieldClass)) {
            return rs.getLong(dbColumnName);
        }
        if (Double.class.equals(fieldClass)
                || double.class.equals(fieldClass)) {
            return rs.getDouble(dbColumnName);
        }
        if (Float.class.equals(fieldClass)
                || float.class.equals(fieldClass)) {
            return rs.getFloat(dbColumnName);
        }
        if (Boolean.class.equals(fieldClass)
                || boolean.class.equals(fieldClass)) {
            return rs.getBoolean(dbColumnName);
        }
        return rs.getObject(dbColumnName);
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
        Connection conn = getConn();
        try {
            PreparedStatement stmt = conn.prepareStatement(getFindAllSql());
            ResultSet rs = stmt.executeQuery();
            CronScheduler cs = null;
            List<CronScheduler> list = new LinkedList<>();
            while (rs.next()) {
                cs = getEntity(rs);
                list.add(cs);
            }
            return list;
        } finally {
            conn.close();
        }
    }

    @Override
    public List<CronScheduler> find(String sql, List<Object> values) throws SQLException {
        Connection conn = getConn();
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
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
                    try {
                        field.set(cs, copyValue(rs, field.getClass(), DBMUtils.getDBColumnName(field)));
                    } catch (IllegalAccessException e) {
                        log.error(e.getMessage(), e);
                    }
                }
                list.add(cs);
            }
            return list;
        } finally {
            conn.close();
        }
    }

    @Override
    public Page<CronScheduler> find(String sql, List<Object> values, int pageSize, int pageNo) throws SQLException {
        String countSql = "select count(*) " + sql.substring(sql.lastIndexOf("from"));
        Connection conn = getConn();
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            PreparedStatement countStmt = conn.prepareStatement(countSql);
            if (values != null) {
                for (int i = 0; i < values.size(); i++) {
                    Object obj = values.get(i);
                    stmt.setObject(i + 1, obj);
                    countStmt.setObject(i + 1, obj);
                }
            }
            ResultSet rs = stmt.executeQuery();
            List<CronScheduler> css = new LinkedList<>();
            while (rs.next()) {
                css.add(getEntity(rs));
            }
            Page<CronScheduler> page = new Page<>(pageNo, pageSize);
            page.setResult(css);
            ResultSet countRs = countStmt.executeQuery();
            if (countRs.next()) {
                page.setTotalCount(countRs.getInt(1));
            }
            return page;
        } finally {
            conn.close();
        }
    }


    public Page<SchedulerDTO> pageList(String job, String cron, Boolean disabled, String data, Date startTime, Date endTime, int pageSize, int pageNo) throws SQLException {
        StringBuilder sql = new StringBuilder(getFindAllSql()).append(" where 1=1 ");
        StringBuilder countSql = new StringBuilder("select count(*) from ")
                .append(DBMUtils.getDbTableName(CronScheduler.class))
                .append(" where 1=1 ");
        List<Object> values = new LinkedList<>();
        StringBuilder where = new StringBuilder();
        if (StringUtils.isNotBlank(job)) {
            String dbColumnName = DBMUtils.getDBColumnName(ClassInfoCache.getField(CronScheduler.class, "job"));
            where.append(" and ").append(dbColumnName).append("=? ");
            values.add(job);
        }
        if (StringUtils.isNotBlank(cron)) {
            String dbColumnName = DBMUtils.getDBColumnName(ClassInfoCache.getField(CronScheduler.class, "cron"));
            where.append(" and ").append(dbColumnName).append("=? ");
            values.add(cron);
        }
        if (disabled != null) {
            String dbColumnName = DBMUtils.getDBColumnName(ClassInfoCache.getField(CronScheduler.class, "disabled"));
            where.append(" and ").append(dbColumnName).append("=? ");
            values.add(disabled);
        }
        if (StringUtils.isNotBlank(data)) {
            String dbColumnName = DBMUtils.getDBColumnName(ClassInfoCache.getField(CronScheduler.class, "data"));
            where.append(" and ").append(dbColumnName).append("=? ");
            values.add(data);
        }
        if (startTime != null) {
            String dbColumnName = DBMUtils.getDBColumnName(ClassInfoCache.getField(CronScheduler.class, "startTime"));
            where.append(" and ").append(dbColumnName).append("<? ");
            values.add(disabled);
        }
        if (endTime != null) {
            String dbColumnName = DBMUtils.getDBColumnName(ClassInfoCache.getField(CronScheduler.class, "endTime"));
            where.append(" and ").append(dbColumnName).append(">? ");
            values.add(disabled);
        }
        Page<SchedulerDTO> page = new Page<>(pageNo, pageSize);
        sql.append(where).append(" limit ").append(page.getStart()).append(",").append(page.getLimit());
        countSql.append(where);
        Connection conn = getConn();
        try {
            PreparedStatement stmt = conn.prepareStatement(sql.toString());
            PreparedStatement countStmt = conn.prepareStatement(countSql.toString());
            for (int i = 0; i < values.size(); i++) {
                Object obj = values.get(i);
                stmt.setObject(i + 1, obj);
                countStmt.setObject(i + 1, obj);
            }
            ResultSet rs = stmt.executeQuery();
            List<SchedulerDTO> dtos = new LinkedList<>();
            SchedulerDTO dto;
            CronScheduler cs;
            while (rs.next()) {
                dto = new SchedulerDTO();
                cs = getEntity(rs);
                BeanUtils.copyProperties(cs, dto, new String[]{"status"});
                dto.setStatus(JobStatus.of(cs.getStatus()));
                dtos.add(dto);
            }
            page.setResult(dtos);
            ResultSet countRs = countStmt.executeQuery();
            if (countRs.next()) {
                page.setTotalCount(countRs.getInt(1));
            }
            return page;
        } finally {
            conn.close();
        }
    }


    public List<CronScheduler> list(String job, String cron, Boolean disabled, String data, String callbackUrl, String callbackVersion) throws SQLException {
        StringBuilder sql = new StringBuilder(getFindAllSql()).append(" where 1=1 ");
        List<Object> values = new LinkedList<>();
        StringBuilder where = new StringBuilder();
        if (StringUtils.isNotBlank(job)) {
            String dbColumnName = DBMUtils.getDBColumnName(ClassInfoCache.getField(CronScheduler.class, "job"));
            where.append(" and ").append(dbColumnName).append("=? ");
            values.add(job);
        }
        if (StringUtils.isNotBlank(cron)) {
            String dbColumnName = DBMUtils.getDBColumnName(ClassInfoCache.getField(CronScheduler.class, "cron"));
            where.append(" and ").append(dbColumnName).append("=? ");
            values.add(cron);
        }
        if (disabled != null) {
            String dbColumnName = DBMUtils.getDBColumnName(ClassInfoCache.getField(CronScheduler.class, "disabled"));
            where.append(" and ").append(dbColumnName).append("=? ");
            values.add(disabled);
        }
        if (StringUtils.isNotBlank(data)) {
            String dbColumnName = DBMUtils.getDBColumnName(ClassInfoCache.getField(CronScheduler.class, "params"));
            where.append(" and ").append(dbColumnName).append("=? ");
            values.add(data);
        }
        if (StringUtils.isNotBlank(callbackUrl)) {
            String dbColumnName = DBMUtils.getDBColumnName(ClassInfoCache.getField(CronScheduler.class, "callbackUrl"));
            where.append(" and ").append(dbColumnName).append("=? ");
            values.add(callbackUrl);
        }
        if (StringUtils.isNotBlank(callbackVersion)) {
            String dbColumnName = DBMUtils.getDBColumnName(ClassInfoCache.getField(CronScheduler.class, "callbackVersion"));
            where.append(" and ").append(dbColumnName).append("=? ");
            values.add(callbackVersion);
        }
        Connection conn = getConn();
        List<CronScheduler> cses;
        try {
            sql.append(where);
            PreparedStatement stmt = conn.prepareStatement(sql.toString());
            for (int i = 0; i < values.size(); i++) {
                Object obj = values.get(i);
                stmt.setObject(i + 1, obj);
            }
            ResultSet rs = stmt.executeQuery();
            cses = new LinkedList<>();
            while (rs.next()) {
                cses.add(getEntity(rs));
            }
        } finally {
            conn.close();
        }
        return cses;
    }
}

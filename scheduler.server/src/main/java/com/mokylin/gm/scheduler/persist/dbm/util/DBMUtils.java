package com.mokylin.gm.scheduler.persist.dbm.util;

import com.mokylin.gm.scheduler.entity.CronScheduler;
import com.mokylin.gm.scheduler.persist.dbm.annotation.*;
import com.mokylin.gm.scheduler.persist.dbm.cache.ClassInfoCache;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * @author 李朝(Li.Zhao)
 * @since 2015/1/8.
 */

public class DBMUtils {
    private final static Logger LOGGER = LoggerFactory.getLogger(DBMUtils.class);

    public static String getDbTableName(Class clazz) {

        Table annotation = ClassInfoCache.getAnnotation(clazz, Table.class);
        String tableName;
        if (annotation != null) {
            tableName = annotation.value();
        } else {
//            throw new IllegalArgumentException("该类没有指定Table");
            tableName = clazz.getSimpleName();
        }
        return tableName;
    }

    public static String getDBColumnName(Field field) {
        Column ann = ClassInfoCache.getAnnotation(field, Column.class);
        String value = ann.value();
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return field.getName();
    }

    /**
     * 通过mongoDB对象获取Model
     *
     * @param dbo
     * @return
     */
    public <T> T getModel(ResultSet dbo, Class<T> clazz) {
        if (dbo == null) {
            return null;
        }
        Collection<Field> fields = ClassInfoCache.getPersistFields(clazz);
        T t = null;
        try {
            t = clazz.newInstance();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        for (Field field : fields) {
            try {
                Column column = ClassInfoCache.getAnnotation(field, Column.class);
                String dbCol = column.value();
                if (StringUtils.isBlank(dbCol)) {
                    dbCol = field.getName();
                }
                Object dbVal = dbo.getObject(dbCol);
                if (dbVal == null) {
                    continue;
                }
                Object fieldVal = null;
                Class<?> fieldClass = field.getType();

                if (!fieldClass.isAssignableFrom(dbVal.getClass())) {
                    if (Enum.class.isAssignableFrom(fieldClass)) {
                        Custom customAnn = ClassInfoCache.getAnnotation(field, Custom.class);
                        if (customAnn != null) {
                            Class<? extends CustomConverter> converterClazz = customAnn.value();
                            CustomConverter converter = ClassInfoCache.getSingleton(converterClazz);
                            Class dbFieldClazz = converter.getDbFieldClazz();
                            dbVal = convertVal(dbFieldClazz, dbVal);
                            fieldVal = converter.deSerialize(dbVal);
                        } else {
                            fieldVal = getEnumValue(field, dbVal);
                        }
                    }
                    fieldVal = convertVal(fieldClass, dbVal);
                }
                field.set(t, fieldVal);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("dbObject to model,dbo:{},model:{}", dbo, t);
        }
        return t;
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
            LOGGER.error(e.getMessage(), e);
        }
        return false;

    }

    private Object getEnumValue(Field field, Object dbVal) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        EnumValue enumAnn = ClassInfoCache.getAnnotation(field, EnumValue.class);
        EnumValueType annValue = enumAnn == null ? EnumValueType.NAME : enumAnn.value();
        //noinspection unchecked
        Class<? extends Enum> fieldEnum = (Class<? extends Enum>) field.getType();
        switch (annValue) {
            case NAME:
                try {
                    return Enum.valueOf(fieldEnum, dbVal.toString());
                } catch (Exception e) {
                    return null;
                }
            case STRING:
                return getEnumByToString(fieldEnum, dbVal);
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
}

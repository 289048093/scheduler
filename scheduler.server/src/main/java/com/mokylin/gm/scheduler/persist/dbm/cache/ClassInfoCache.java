package com.mokylin.gm.scheduler.persist.dbm.cache;

import com.mokylin.gm.scheduler.persist.dbm.annotation.Column;

import java.lang.annotation.Annotation;
import java.lang.ref.SoftReference;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 李朝(Li.Zhao)
 * @since 2014/11/24.
 */

public class ClassInfoCache {
    private static final ConcurrentHashMap<Class<?>, SoftReference<Map<String, Field>>> fieldCache = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<Class<?>, SoftReference<Map<String, Field>>> persistFieldCache = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<Class<?>, SoftReference<Map<String, Method>>> methodCache = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<Class<?>, Object> singletonInstance = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<AnnotatedElement, SoftReference<Map<Class<? extends Annotation>, Annotation>>> annotationCache = new ConcurrentHashMap<>();


    public static <T> T getSingleton(Class<T> clazz) {
        Object t = singletonInstance.get(clazz);
        if (t != null) {
            return clazz.cast(t);
        }
        try {
            t = clazz.newInstance();
            Object o = singletonInstance.putIfAbsent(clazz, t);
            return clazz.cast(o != null ? o : t);
        } catch (Exception e) {
            throw new IllegalArgumentException(clazz.getName() + " class must has no args public constructor!");
        }
    }


    /**
     * 获取clazz的methodName方法
     *
     * @param clazz
     * @param methodName
     * @return
     */
    public static Method getMethod(Class<?> clazz, String methodName,Class<?>... paramTypes) {

        return getAllMethod(clazz).get(getMethodCacheKey(methodName,paramTypes));
    }

    /**
     * 获取所有方法
     *
     * @param clazz
     * @return
     */
    public static Map<String, Method> getAllMethod(Class<?> clazz) {
        Map<String, Method> methodMap = null;
        SoftReference<Map<String, Method>> ref = methodCache.get(clazz);
        if (ref != null) {
            methodMap = ref.get();
        }
        if (methodMap == null) {
            ref = methodCache.get(clazz);
            if (ref != null) {
                methodMap = ref.get();
            }
            if (methodMap == null) {
                methodMap = _getAllMethod(clazz);
                methodCache.put(clazz, new SoftReference<>(methodMap));
            }
        }
        return methodMap;
    }

    private static Map<String, Method> _getAllMethod(Class<?> clazz) {
        Map<String, Method> res = new HashMap<>();
        if (Object.class.equals(clazz)) {
            return res;
        }
        Method[] methods = clazz.getDeclaredMethods();
        if (methods.length == 0) {
            return res;
        }
        for (Method m : methods) {
            m.setAccessible(true);
            String name = m.getName();
            Class<?>[] parameterTypes = m.getParameterTypes();
            res.put(getMethodCacheKey(name,parameterTypes), m);
        }
        res.putAll(_getAllMethod(clazz.getSuperclass()));
        return res;
    }

    private static String getMethodCacheKey(String methodName,Class<?>[] parameterTypes){
        StringBuilder names = new StringBuilder(methodName);
        for(Class<?> ct:parameterTypes){
            names.append(ct.getName());
        }
        return names.toString();
    }

    /**
     * 获取所有可持久化的{@link java.lang.reflect.Field},返回可访问的Field,即设置了{@link java.lang.reflect.Field#setAccessible(boolean)} 为True
     *
     * @param clazz
     * @return 可持久化的Field
     */
    public static Collection<Field> getPersistFields(Class<?> clazz) {
        Map<String, Field> fields = null;
        SoftReference<Map<String, Field>> ref = persistFieldCache.get(clazz);
        if (ref != null) {
            fields = ref.get();
        }
        if (fields == null) {
            synchronized (persistFieldCache) {
                ref = persistFieldCache.get(clazz);
                if (ref != null) {
                    fields = ref.get();
                }
                if (fields == null) {
                    fields = new HashMap<>();
                    Map<String, Field> all = getAllFields(clazz);
                    for (Field field : all.values()) {
                        Column column = field.getAnnotation(Column.class);
                        if (column == null) {
                            continue;
                        }
                        fields.put(field.getName(), field);
                    }
                    persistFieldCache.put(clazz, new SoftReference<>(fields));
                }
            }
        }
        return fields.values();
    }

    /**
     * 从缓存中获取field
     *
     * @param clazz
     * @param fieldName
     * @return
     */
    public static Field getField(Class<?> clazz, String fieldName) {
        Map<String, Field> fieldMap = getAllFields(clazz);
        return fieldMap.get(fieldName);
    }

    /**
     * 获取field的annotation
     *
     * @param annotatedElement
     * @param annotationClazz
     * @param <T>
     * @return
     */
    public static <T extends Annotation> T getAnnotation(AnnotatedElement annotatedElement, Class<T> annotationClazz) {
        Map<Class<? extends Annotation>, Annotation> annotationMap = null;
        T res = null;
        SoftReference<Map<Class<? extends Annotation>, Annotation>> ref = annotationCache.get(annotatedElement);
        if (ref != null) {
            annotationMap = ref.get();
        }
        if (annotationMap != null) {
            //noinspection unchecked
            return (T) annotationMap.get(annotationClazz);
        }
        annotationMap = getAllAnnotation(annotatedElement);
        annotationCache.put(annotatedElement, new SoftReference<>(annotationMap));
        //noinspection unchecked
        return (T) annotationMap.get(annotationClazz);
    }

    private static Map<Class<? extends Annotation>, Annotation> getAllAnnotation(AnnotatedElement annotatedElement) {
        Map<Class<? extends Annotation>, Annotation> res = new HashMap<>();
        Annotation[] annotations = annotatedElement.getAnnotations();
        if (annotations == null) {
            return res;
        }
        for (Annotation a : annotations) {
            //noinspection unchecked
            res.put((Class<? extends Annotation>) a.getClass().getInterfaces()[0], a);
        }
        return res;
    }


    /**
     * 获取所有field
     *
     * @param clazz
     * @return
     */
    public static Map<String, Field> getAllFields(Class<?> clazz) {

        SoftReference<Map<String, Field>> ref = fieldCache.get(clazz);
        Map<String, Field> cache = null;
        if (ref != null)
            cache = ref.get();
        if (cache != null) {
            return cache;
        }
        cache = _getAllFields(clazz);
        fieldCache.put(clazz, new SoftReference<>(cache));
        return cache;
    }

    private static Map<String, Field> _getAllFields(Class<?> clazz) {
        Map<String, Field> res = new HashMap<>();
        if (Object.class.equals(clazz)) {
            return res;
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            res.put(f.getName(), f);
        }
        Class<?> superclass = clazz.getSuperclass();
        res.putAll(_getAllFields(superclass));
        return res;
    }

}

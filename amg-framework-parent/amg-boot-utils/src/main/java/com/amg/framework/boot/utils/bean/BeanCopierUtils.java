package com.amg.framework.boot.utils.bean;


import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.core.Converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/**
 * bean 复制工具
 * 建议简单复制场景下使用
 * 复杂对象复制使用 MapStruct
 */
public class BeanCopierUtils {

    /**
     * BeanCopier 缓存
     */
    private static final ConcurrentHashMap<String, BeanCopier> BEAN_COPIER_CACHE = new ConcurrentHashMap<>();



    public static <T, M> List<T> copyObjects(List<M> sources, Class<T> clazz) {
        return copyObjects(sources, clazz, null);
    }


    /**
     * 列表对象拷贝
     * @param sources 源列表
     * @param clazz 源列表对象Class
     * @param <T> 目标列表对象类型
     * @param <M> 源列表对象类型
     * @param converter 自定义转换规则
     * @return 目标列表
     */
    public static <T, M> List<T> copyObjects(List<M> sources, Class<T> clazz, Converter converter) {
//        if (Objects.isNull(sources) || Objects.isNull(clazz) || sources.isEmpty())
//            throw new IllegalArgumentException();
        if (Objects.isNull(clazz))
            throw new IllegalArgumentException();
        if (sources.isEmpty()) {
            return new ArrayList<T>();
        } else if (Objects.isNull(sources)) {
            return null;
        }
        return Optional.of(sources)
                .orElse(new ArrayList<>())
                .stream().map(m -> copyProperties(m, clazz, converter))
                .collect(Collectors.toList());
    }



    public static <T, M> T copyProperties(M source, Class<T> clazz){
        return copyProperties(source, clazz, null);
    }


    /**
     * 单个对象属性拷贝
     * @param source 源对象
     * @param clazz 目标对象Class
     * @param <T> 目标对象类型
     * @param <M> 源对象类型
     * @param converter 自定义转换规则
     * @return 目标对象
     */
    public static <T, M> T copyProperties(M source, Class<T> clazz, Converter converter){
//        if (Objects.isNull(source) || Objects.isNull(clazz))
//            throw new IllegalArgumentException();
        if (Objects.isNull(clazz))
            throw new IllegalArgumentException();
        if (Objects.isNull(source)) {
            return null;
        }
        T t = null;
        try {
            t = clazz.newInstance();
            if (converter != null) {
                genBeanCopier(source.getClass(), clazz, true).copy(source, t, converter);
            } else {
                genBeanCopier(source.getClass(), clazz).copy(source, t, converter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }


    /**
     * 单个对象属性拷贝
     * @param source1 源对象
     * @param source2 目标对象
     * @param <T> 目标对象类型
     * @param <M> 源对象类型
     * @return 目标对象
     */
    public static <T, M> void copyProperties(M source1, T source2){
//        if (Objects.isNull(source1) || Objects.isNull(source2))
//            throw new IllegalArgumentException();
        if (Objects.isNull(source2))
            throw new IllegalArgumentException();
        if (Objects.isNull(source1)) {
            source2 = null;
            return;
        }
        genBeanCopier(source1.getClass(), source2.getClass()).copy(source1, source2, null);
    }


    /**
     * 获取 BeanCopier
     * @param srcClazz 源文件的class
     * @param tgtClazz 目标文件的class
     * @return BeanCopier
     */
    private static BeanCopier genBeanCopier(Class<?> srcClazz, Class<?> tgtClazz) {
        return genBeanCopier(srcClazz, tgtClazz, false);
    }


    private static BeanCopier genBeanCopier(Class<?> srcClazz, Class<?> tgtClazz, boolean b) {
        BeanCopier beanCopier;
        String key = srcClazz.getName() + tgtClazz.getName();
        if (BEAN_COPIER_CACHE.containsKey(key)) {
            beanCopier = BEAN_COPIER_CACHE.get(key);
        } else {
            beanCopier = BeanCopier.create(srcClazz, tgtClazz, b);
            BEAN_COPIER_CACHE.put(key, beanCopier);
        }
        return beanCopier;
    }

}


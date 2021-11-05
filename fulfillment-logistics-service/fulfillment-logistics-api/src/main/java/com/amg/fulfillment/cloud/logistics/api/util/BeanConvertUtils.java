package com.amg.fulfillment.cloud.logistics.api.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seraph on 2021/5/25
 */

@Slf4j
public class BeanConvertUtils<S, T> {

    /**
     * 不需要自动转换属性
     */
    private String[] ignoreProperties;
    private boolean isCopy = true;
    private boolean isBatch = false;

    protected BeanConvertUtils(String[] ignoreProperties){
        this.ignoreProperties = ignoreProperties;
    }
    protected BeanConvertUtils(String[] ignoreProperties, boolean isBatch){
        this.ignoreProperties = ignoreProperties;
        this.isBatch = isBatch;
    }
    protected BeanConvertUtils(boolean isCopy, String[] ignoreProperties){
        this.isCopy = isCopy;
        this.ignoreProperties = ignoreProperties;
    }

    @SuppressWarnings("unchecked")
    private Class<S> getClassS() {
        return (Class<S>)((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @SuppressWarnings("unchecked")
    private Class<T> getClassT() {
        return (Class<T>)((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }

    public static <CT> CT copyProperties(Object source, Class<CT> cls) {
        if(source == null || cls == null) {
            return null;
        }
        try {
            CT ct = cls.newInstance();
            return copyProperties(source, ct);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static <CT> CT copyProperties(Object source, CT target) {
        if(source == null || target == null) {
            return target;
        }
        try {
            BeanUtils.copyProperties(source, target);
        } catch (BeansException e) {
            log.error(e.getMessage(), e);
        }
        return target;
    }

    public static <CT> List<CT> copyProperties(List<?> sources, Class<CT> cls) {
        if(sources.isEmpty())
        {
            return new ArrayList<>();
        }
        if(sources == null || cls == null) {
            return null;
        }

        List<CT> list = new ArrayList<CT>();
        try {
            for(Object o : sources) {
                CT t = cls.newInstance();
                BeanUtils.copyProperties(o, t);
                list.add(t);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return list;
    }

    /**
     * 对象转换
     * @param s
     * @return
     */
    public T convert(S s) {

        if(s == null) {
            return null;
        }

        T t = null;
        try {
            t = getClassT().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {}
        if(t == null) {
            return null;
        }
        this.convert(s, t);
        return t;
    }

    /**
     * bean属性拷贝
     * @param s
     * @param t
     */
    public void convert(S s, T t) {
        if(s == null || t == null) {
            return ;
        }
        if(isCopy) {
            BeanUtils.copyProperties(s, t, ignoreProperties);
        }
        this.convertObject(s, t);
    }



    public S reconvert(T t) {
        if(t == null) {
            return null;
        }
        S s = null;
        try {
            s = getClassS().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {}
        if(s == null) {
            return null;
        }
        this.reconvert(t, s);
        return s;
    }

    public void reconvert(T t, S s) {
        if(s == null || t == null) {
            return ;
        }
        if(isCopy) {
            BeanUtils.copyProperties(t, s, ignoreProperties);
        }
        this.reconvertObject(t, s);
    }


    /**
     * 对象批量转换
     * @param slist
     * @return
     */
    public List<T> convert(List<S> slist){
        if(slist == null || slist.isEmpty()) {
            return null;
        }
        if(this.isBatch) {
            return ExecutorUtils.convert(this, slist);
        }
        List<T> tlist = new ArrayList<T>();
        for(S s : slist) {
            tlist.add(this.convert(s));
        }
        return tlist;
    }

    /**
     * 对象批量转换
     * @param tlist
     * @return
     */
    public List<S> reconvert(List<T> tlist){

        if(tlist == null || tlist.isEmpty()) {
            return null;
        }
        if(this.isBatch) {
            return ExecutorUtils.reconvert(this, tlist);
        }
        List<S> slist = new ArrayList<S>();
        for(T t : tlist) {
            slist.add(this.reconvert(t));
        }
        return slist;
    }

    /**
     * 子类实现自定义转换
     * @param s
     * @param t
     */
    protected void reconvertObject(T t, S s)
    {

    }

    /**
     * 子类实现自定义转换
     * @param s
     * @param t
     */
    protected void convertObject(S s, T t)
    {

    }
}

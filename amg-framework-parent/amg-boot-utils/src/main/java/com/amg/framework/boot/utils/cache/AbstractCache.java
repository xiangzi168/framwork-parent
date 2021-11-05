package com.amg.framework.boot.utils.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.InitializingBean;


/**
 * guava内存缓存基类
 */
public abstract class AbstractCache<K, T> implements InitializingBean {

    private LoadingCache<K, T> cache;

    protected abstract CacheBuilder<Object, Object> getCacheBuilder(CacheBuilder<Object, Object> cacheBuilder);

    protected abstract CacheLoader<K, T> getCacheLoader();

    protected LoadingCache<K, T> getCache() {
        return cache;
    }

    public T getValue(K k) throws Exception {
        return this.cache.get(k);
    }

    public void setValue(K k, T t) {
        this.cache.put(k, t);
    }

    public void delValue(K k) {
        this.cache.invalidate(k);
    }

    @Override
    public void afterPropertiesSet() {
        CacheLoader<K, T> cacheLoader = this.getCacheLoader();
        CacheBuilder<Object, Object> cacheBuilder = this.getCacheBuilder(CacheBuilder.newBuilder());
        this.cache = cacheBuilder.build(cacheLoader);
    }

}

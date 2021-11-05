package com.amg.framework.boot.utils.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * 本地缓存工具类
 */
@Component
public class CacheUtils extends AbstractCache<String, Map<String, Object>> {

    // 过期时间: 30分钟
    private static final int EXPIRE_SEC_TIME = 1800;

    // 最多保存的key的数量
    private static final int MAX_KEY_SIZE = 5000;


    @Override
    protected CacheBuilder<Object, Object> getCacheBuilder(CacheBuilder<Object, Object> cacheBuilder) {
        return cacheBuilder.maximumSize(MAX_KEY_SIZE).expireAfterAccess(EXPIRE_SEC_TIME, TimeUnit.SECONDS);
    }


    @Override
    protected CacheLoader<String, Map<String, Object>> getCacheLoader() {
        return new CacheLoader<String, Map<String, Object>>() {
            @Override
            public Map<String, Object> load(String key) throws Exception {
                return new HashMap<>();
            }
        };
    }


    // 获取缓存
    public Object get(String key) {
        try {
            return super.getValue(key).get(key);
        } catch (Exception e) {
            return null;
        }
    }


    // 设置缓存
    public void set(String key, Object object) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, object);
        super.setValue(key, map);
    }


    // 删除缓存
    public void delete(String key) {
        super.delValue(key);
    }

}

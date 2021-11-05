package com.amg.framework.boot.mongodb.utils;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Map;

/**
 * @author zhengzhouyang
 * @date 2020/7/9 18:21
 * @describe    map转成mongo的Update对象
 */
public class MongoUtils {

    public static Update mongoUpdate(Map<String, Object> map){
        Update update = new Update();
        if (!map.isEmpty()) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                update.set(entry.getKey(), entry.getValue());
            }
        }
        return update;
    }

    public static Query getQuery() {
        return new Query(Criteria.where("isDeleted").is(false));
    }

    public static Query getQuery(boolean isDeleted) {
        return new Query(Criteria.where("isDeleted").is(isDeleted));
    }
}

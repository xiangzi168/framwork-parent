package com.amg.framework.boot.mongodb.utils;


import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;


@Component
public class MongodbUtils {

    @Autowired
    private MongoTemplate mongoTemplate;


    public <T> List<T> find(Query query, Class<T> entityClass) {
        return mongoTemplate.find(query, entityClass);
    }


    public <T> List<T> find(Query query, Class<T> entityClass, String collectionName) {
        return mongoTemplate.find(query, entityClass, collectionName);
    }


    public long count(Query query, Class<?> entityClass, String collectionName) {
        return mongoTemplate.count(query, entityClass, collectionName);
    }

    public long count(Query query, Class<?> entityClass) {
        return mongoTemplate.count(query, entityClass);
    }

    public <T> T findOne(Query query, Class<T> entityClass) {
        return mongoTemplate.findOne(query, entityClass);
    }


    public <T> T save(T t) {
        return mongoTemplate.save(t);
    }


    public <T> T save(T t, String collectionName) {
        return mongoTemplate.save(t, collectionName);
    }


    public <T> T insert(T t) {
        return mongoTemplate.insert(t);
    }

    public <T> Collection<T> insert(Collection<? extends T> batchToSave, String collectionName) {
        return mongoTemplate.insert(batchToSave, collectionName);
    }

    public <T> Collection<T> insert(Collection<? extends T> batchToSave, Class<?> entityClass) {
        return mongoTemplate.insert(batchToSave, entityClass);
    }

    public <T> T insert(T t, String collectionName) {
        return mongoTemplate.insert(t, collectionName);
    }

    public UpdateResult updateMulti(Query query, Update update, Class<?> entityClass) {
        return mongoTemplate.updateMulti(query, update, entityClass);
    }

    public UpdateResult upsert(Query query, Update update, Class<?> entityClass, String collectionName) {
        return mongoTemplate.upsert(query, update, entityClass, collectionName);
    }

    public UpdateResult updateFirst(Query query, Update update, Class<?> entityClass) {

        return mongoTemplate.updateFirst(query, update, entityClass);
    }

    public DeleteResult remove(Object object){
        return mongoTemplate.remove(object);
    }

    public DeleteResult remove(Object object, String collectionName){
        return mongoTemplate.remove(object, collectionName);
    }

    public DeleteResult remove(Query query, Class<?> entityClass) {
        return mongoTemplate.remove(query, entityClass);
    }

}

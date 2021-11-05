package com.amg.framework.boot.mongodb.model;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;


public class UpdateBatchModel {

    private Update update;
    private Query query;

    public UpdateBatchModel() {
    }

    public UpdateBatchModel(Update update, Query query) {
        this.update = update;
        this.query = query;
    }

    public static UpdateBatchModel newBuilder() {
        return new UpdateBatchModel();
    }

    public UpdateBatchModel setUpdate(Update update) {
        this.update(update);
        return this;
    }

    public UpdateBatchModel setQuery(Query query) {
        this.query(query);
        return this;
    }

    public Update getUpdate() {
        return update;
    }

    public void update(Update update) {
        this.update = update;
    }

    public Query getQuery() {
        return query;
    }

    public void query(Query query) {
        this.query = query;
    }
}

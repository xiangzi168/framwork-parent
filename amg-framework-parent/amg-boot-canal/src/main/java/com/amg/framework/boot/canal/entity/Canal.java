package com.amg.framework.boot.canal.entity;

import java.util.List;

/**
 * @author lyc
 * @date 2020/9/27 11:08
 * @describe
 */
public class Canal {
    /**
     * 消息id
     */
    private long id;

    /**
     * 库名
     */
    private String database;

    /**
     * 表名
     */
    private String table;

    /**
     * 事件类型
     */
    private String eventType;

    /**
     * 之前 数据
     */
    private List<Column> beforeList;

    /**
     * 之后 数据
     */
    private List<Column> afterList;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public List<Column> getBeforeList() {
        return beforeList;
    }

    public void setBeforeList(List<Column> beforeList) {
        this.beforeList = beforeList;
    }

    public List<Column> getAfterList() {
        return afterList;
    }

    public void setAfterList(List<Column> afterList) {
        this.afterList = afterList;
    }

    @Override
    public String toString() {
        return "Canal{" +
                "id=" + id +
                ", database='" + database + '\'' +
                ", table='" + table + '\'' +
                ", eventType='" + eventType + '\'' +
                ", beforeList=" + beforeList +
                ", afterList=" + afterList +
                '}';
    }
}

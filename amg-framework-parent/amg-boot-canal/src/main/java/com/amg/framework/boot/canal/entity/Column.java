package com.amg.framework.boot.canal.entity;

/**
 * @author lyc
 * @date 2020/9/27 11:26
 * @describe
 */
public class Column {
    /**
     * 数据库列
     */
    private String name;

    /**
     * 值
     */
    private String value;

    /**
     * 是否更新
     */
    private boolean update;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    @Override
    public String toString() {
        return "Column{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", update=" + update +
                '}';
    }
}

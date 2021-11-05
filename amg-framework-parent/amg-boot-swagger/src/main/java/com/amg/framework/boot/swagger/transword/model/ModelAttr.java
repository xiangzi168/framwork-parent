package com.amg.framework.boot.swagger.transword.model;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 属性实体
 */
public class ModelAttr implements Serializable {

    private static final long serialVersionUID = -4074067438450613643L;

    /**
     * 类名
     */
    private String className = StringUtils.EMPTY;
    /**
     * 属性名
     */
    private String name = StringUtils.EMPTY;
    /**
     * 类型
     */
    private String type = StringUtils.EMPTY;

    /**
     * 属性描述
     */
    private String description;
    /**
     * 嵌套属性列表
     */
    private List<ModelAttr> properties = new ArrayList<>();

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ModelAttr> getProperties() {
        return properties;
    }

    public void setProperties(List<ModelAttr> properties) {
        this.properties = properties;
    }
}

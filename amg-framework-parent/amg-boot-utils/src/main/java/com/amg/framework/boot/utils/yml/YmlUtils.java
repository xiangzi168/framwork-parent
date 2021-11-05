package com.amg.framework.boot.utils.yml;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

import java.util.Properties;


/**
 * yml 文件解析工具类
 */
public class YmlUtils {

    private static String bootstrap_file = "bootstrap.yml";


    /**
     * 根据文件名 key 获取值
     * @param key
     * @return
     */
    public static String getValue(String key) {
        return getValue(key, null);
    }


    /**
     * 根据文件名 key 获取值
     * @param key
     * @return
     */
    public static String reTryGetValue(String key) {
        String value = getValue(key, null);
        if (StringUtils.isBlank(value)) {
            value = getValue(key, "application.yml");
        }
        return value;
    }


    /**
     *根据文件名 key 名称获取值
     * @param key
     * @param fileName 文件必须放在 classpath
     * @return
     */
    public static String getValue(String key, String fileName) {
        if (StringUtils.isBlank(fileName))
            fileName = bootstrap_file;
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource(fileName));
        return yaml.getObject().getProperty(key);
    }


    public static Properties getYamlProperties() {
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource(bootstrap_file));
        return yaml.getObject();
    }

}

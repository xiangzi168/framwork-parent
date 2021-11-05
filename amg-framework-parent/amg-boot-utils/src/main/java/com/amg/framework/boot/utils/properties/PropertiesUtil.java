package com.amg.framework.boot.utils.properties;

import org.apache.commons.lang3.StringUtils;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * properties 文件解析工具类
 */
public class PropertiesUtil {

    private static Properties props;

    synchronized static private void load(String fileName) {
        if (StringUtils.isBlank(fileName))
            throw new IllegalArgumentException();
        props = new Properties();
        InputStream in = null;
        try {
            in = PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName);
            props.load(in);
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
            } catch (IOException e) {
            }
        }
    }


    public static String getProperty(String key, String fileName) {
        if (props == null)
            load(fileName);
        return props.getProperty(key);
    }

}
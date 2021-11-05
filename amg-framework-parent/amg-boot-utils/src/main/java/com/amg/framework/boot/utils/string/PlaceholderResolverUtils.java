package com.amg.framework.boot.utils.string;



import java.util.Map;

import cn.hutool.core.util.ReUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.PropertyPlaceholderHelper;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.Date;



/**
 * 占位符解析器
 */
@Component
public class PlaceholderResolverUtils {

    @Autowired
    public Environment env;

    public static Environment environment;

    @PostConstruct
    public void init() {
        environment = env;
    }

    private static final PropertyPlaceholderHelper defaultHelper = new PropertyPlaceholderHelper("${", "}");

    private static PlaceholderResolverUtils defaultResolver = new PlaceholderResolverUtils();

    private PropertyPlaceholderHelper propertyPlaceholderHelper;


    private PlaceholderResolverUtils() {
        propertyPlaceholderHelper = defaultHelper;
    }

    private PlaceholderResolverUtils(String placeholderPrefix, String placeholderSuffix) {
        propertyPlaceholderHelper = new PropertyPlaceholderHelper(placeholderPrefix, placeholderSuffix);
    }

    /**
     * 获取默认的占位符解析器，即占位符前缀为"${", 后缀为"}"
     *
     * @return
     */
    public static PlaceholderResolverUtils getDefaultResolver() {
        return defaultResolver;
    }

    public static PropertyPlaceholderHelper.PlaceholderResolver getResolver() {
        return resolver;
    }

    public static PlaceholderResolverUtils getResolver(String placeholderPrefix, String placeholderSuffix) {
        return new PlaceholderResolverUtils(placeholderPrefix, placeholderSuffix);
    }

    public static String replacePlaceholders(String placeholders) {
        if (ReUtil.isMatch("\\$\\{(.*?)\\}", placeholders)) {
            return defaultHelper.replacePlaceholders(placeholders, getResolver());
        }
        return placeholders;
    }

    /**
     * 解析带有指定占位符的模板字符串
     * 如：content =  ${0}今年${1}岁<br/>
     * values = {"xiaoming", "18"}<br/>
     * result 小明今年18岁<br/>
     *
     * @param content 要解析的带有占位符的模板字符串
     * @param values  按照模板占位符索引位置设置对应的值
     * @return
     */
    public String resolve(String content, String... values) {
        return propertyPlaceholderHelper.replacePlaceholders(content, placeholderName -> {
            return values[Integer.valueOf(placeholderName)];
        });
    }

    /**
     * 解析带有指定占位符的模板字符串
     * 如：content =  ${0}今年${1}岁<br/>
     * values = {"xiaoming", "18"}<br/>
     * result 小明今年18岁<br/>
     *
     * @param content 要解析的带有占位符的模板字符串
     * @param values  按照模板占位符索引位置设置对应的值
     * @return
     */
    public String resolve(String content, Object[] values) {
        return propertyPlaceholderHelper.replacePlaceholders(content, placeholderName -> {
            return String.valueOf(values[Integer.valueOf(placeholderName)]);
        });
    }

    /**
     * 根据替换规则来替换指定模板中的占位符值
     *
     * @param content             要解析的字符串
     * @param placeholderResolver 解析规则回调
     * @return
     */
    public String resolveByRule(String content, PropertyPlaceholderHelper.PlaceholderResolver placeholderResolver) {
        return propertyPlaceholderHelper.replacePlaceholders(content, placeholderResolver);
    }

    /**
     * 替换模板中占位符内容，占位符的内容即为map key对应的值，key为占位符中的内容。<br/><br/>
     * 如：content = ${name}今年${age}岁<br/>
     * valueMap = name -> 小明; age -> 18<br/>
     * result 小明今年18岁<br/>
     *
     * @param content  模板内容。
     * @param valueMap 值映射
     * @return 替换完成后的字符串。
     */
    public String resolveByMap(String content, final Map<String, Object> valueMap) {
        return propertyPlaceholderHelper.replacePlaceholders(content, placeholderName -> {
            return String.valueOf(valueMap.get(placeholderName));
        });
    }

    /**
     * 根据对象中字段路径(即类似js访问对象属性值)替换模板中的占位符 <br/><br/>
     * 如 content = product:${id}:detail:${detail.id} <br/>
     * obj = Product.builder().id(1).detail(Detail.builder().id(2).build()).build(); <br/>
     * 经过解析返回 product:1:detail:2 <br/>
     *
     * @param content 要解析的内容
     * @param obj     填充解析内容的对象(如果是基本类型，则所有占位符都替换为当前基本类型)
     * @return
     */
    public String resolveByObject(String content, final Object obj) {
        if (obj instanceof Map) {
            return resolveByMap(content, (Map) obj);
        }
        return propertyPlaceholderHelper.replacePlaceholders(content, placeholderName -> {
            return String.valueOf(getValueByFieldPath(obj, placeholderName));
        });
    }

    /**
     * 获取指定对象中指定字段路径的值
     * $(user.name)
     *
     * @param obj       取值对象
     * @param fieldPath 字段路径(形如 user.name)
     * @return
     */
    private Object getValueByFieldPath(Object obj, String fieldPath) {
        String[] fieldNames = fieldPath.split("\\.");
        Object result = null;
        for (String fieldName : fieldNames) {
            result = getFieldValue(obj, fieldName);
            if (result == null) {
                throw new RuntimeException("result is null");
            }
            obj = result;
        }
        return result;
    }

    private Object getFieldValue(Object obj, String fieldName) {
        Class clazz = obj.getClass();
        if (isBaseType(clazz)) {
            return obj;
        }
        while (clazz != Object.class && clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(obj);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (IllegalAccessException e) {
                throw new RuntimeException("fieldName cannot Access");
            }
        }
        throw new IllegalStateException("fieldName donot exist");
    }


    /**
     * 判断class是否为常用类型
     *
     * @param clazz
     * @return
     */
    private boolean isBaseType(Class clazz) {
        return Enum.class.isAssignableFrom(clazz) || CharSequence.class.isAssignableFrom(clazz)
                || Number.class.isAssignableFrom(clazz) || Date.class.isAssignableFrom(clazz);
    }


    private static PropertyPlaceholderHelper.PlaceholderResolver resolver = new PropertyPlaceholderHelper.PlaceholderResolver() {
        @Override
        public String resolvePlaceholder(String placeholderName) {
            String value = environment.getProperty(placeholderName);
            if (value == null)
                throw new IllegalArgumentException("Could not resolve placeholder \"${"+ placeholderName + "\"}");
            return value;
        }
    };

}
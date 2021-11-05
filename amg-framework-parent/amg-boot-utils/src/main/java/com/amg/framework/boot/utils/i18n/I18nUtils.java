package com.amg.framework.boot.utils.i18n;

import com.amg.framework.boot.utils.spring.SpringContextUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.Locale;


/**
 * 国际化工具类
 */
public class I18nUtils {

    /**
     * 动态替换国际化
     * @param str
     * @param param
     * @return
     */
    public static String i18n(String str, Object ... param) {
        return MessageFormat.format(str, param);
    }


    /**
     * 获取国际化结果
     *
     * @param str en_us
     * @return
     */
    public static String i18nByLanguage(String str,String languageType) {
        Locale locale=null;
        if (StringUtils.isEmpty(languageType)||"zh-CN".equals(languageType)){
            locale=Locale.getDefault();
        }else if ("en_us".equals(languageType)){
            locale=Locale.US;
        }
        return SpringContextUtil.getApplicationContext().getMessage(str, null, locale);
    }


    /**
     * 获取国际化结果
     *
     * @param str en_us
     * @return
     */
    public static String i18n(String str) {
        try {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = requestAttributes.getRequest();
            Locale locale = LocaleContextHolder.getLocale();
            String header = request.getHeader("i18n");
            if (StringUtils.isNotBlank(header))
                locale = new Locale(header.split("_")[0], header.split("_")[1]);
            return SpringContextUtil.getApplicationContext().getMessage(str, null, locale);
        } catch (Exception e) {
            return str;
        }
    }


    /**
     * 获取国际化结果
     *
     * @param str en_us
     * @return
     */
    public static String i18nParams(String str, Object... params) {
        try {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = requestAttributes.getRequest();
            Locale locale = LocaleContextHolder.getLocale();
            String header = request.getHeader("i18n");
            if (StringUtils.isNotBlank(header)){
                locale = new Locale(header.split("_")[0], header.split("_")[1]);
            }
            return SpringContextUtil.getApplicationContext().getMessage(str, params, locale);
        } catch (Exception e) {
            return str;
        }
    }

}

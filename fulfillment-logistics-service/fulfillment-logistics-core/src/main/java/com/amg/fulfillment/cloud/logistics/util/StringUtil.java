package com.amg.fulfillment.cloud.logistics.util;

/**
 * @ClassName StringUtil
 * @Description TODO
 * @Author 35112
 * @Date 2021/7/31 17:45
 **/
public class StringUtil {
    public static String cleanUpTheLetter(String s) {
        s = s.replaceAll("[a-zA-Z]", " ");
        return s;
    }

    public static String cleanSpacing(String s) {
        String replace = null;
        if (s != null) {
            replace = s.replace(" ", "");
        }
        return replace;
    }
}

package com.amg.framework.boot.utils.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegexUtils {

    private final static String version_no = "^v\\d+.\\d+.\\d+$";

    private final static String ip_port = "^\\d+.\\d+.\\d+.\\d+:\\d+$";

    public static boolean matcherVersionNo(String s) {
        Pattern pattern = Pattern.compile(version_no);
        Matcher matcher = pattern.matcher(s);
        return matcher.find();
    }

    public static boolean matcherIpPort(String s) {
        Pattern pattern = Pattern.compile(ip_port);
        Matcher matcher = pattern.matcher(s);
        return matcher.find();
    }

}

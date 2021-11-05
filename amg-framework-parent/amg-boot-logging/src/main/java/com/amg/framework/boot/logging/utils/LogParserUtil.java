package com.amg.framework.boot.logging.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LogParserUtil {

    private static final String pattenExp = "\\[\\d+\\-\\d+\\-\\d+ \\d+\\:\\d+\\:\\d+\\.\\d+\\]";

    private static final String pattenTrace = "\\[[0-9a-z]{32}\\]";

    private static Pattern pattern;

    private static Pattern patternTrace;

    static {
        pattern = Pattern.compile(pattenExp);
        patternTrace = Pattern.compile(pattenTrace);
    }

    public static String parserDate(String line) {
        Matcher matcher = pattern.matcher(line);
        boolean isFind = matcher.find();
        if (isFind) {
            return matcher.group().replaceAll("\\[", "").replaceAll("\\]", "");
        }
        return null;
    }

    public static String parserTraceId(String line) {
        Matcher matcher = patternTrace.matcher(line);
        boolean isFind = matcher.find();
        if (isFind) {
            return matcher.group().replaceAll("\\[", "").replaceAll("\\]", "");
        }
        return null;
    }

}


package com.amg.framework.cloud.grpc.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.amg.framework.boot.logging.constant.LogTraceConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


public class UserContext {

    private final static ThreadLocal<Map<String, String>> header = new TransmittableThreadLocal();

    public static String getCurrentUserName() {
        return getHeaderValue("oss-name");
    }


    public static String getCurrentUserFullName() {
        return getHeaderValue("oss-fullname");
    }


    public static String getCurrentUserEmail() {
        return getHeaderValue("oss-email");
    }


    public static String getCurrentUserAccess() {
        return getHeaderValue("oss-access");
    }


    public static String getCurrentUserRequestId() {
        return getHeaderValue("request-id");
    }


    public static void setLocalHeader(String key, String value) {
        Map<String, String> map = header.get();
        if (map == null) {
            map = new HashMap<>();
            map.put(key, value);
            header.set(map);
        } else {
            map.put(key, value);
        }
    }


    public static String getHeaderValue(String key) {
        Map<String, String> map = header.get();
        if (map == null) {
            return getRequestHeaderValue(key);
        } else {
            String value = map.get(key);
            if (StringUtils.isBlank(value)) {
                return getRequestHeaderValue(key);
            }
            return value;
        }
    }


    public static String getRequestHeaderValue(String key) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null)
            return null;
        HttpServletRequest request = requestAttributes.getRequest();
        if (request == null)
            return null;
        return request.getHeader(key) == null ? null : request.getHeader(key).trim();
    }

}

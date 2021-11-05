package com.amg.framework.cloud.grpc.utils;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amg.framework.boot.base.exception.handle.ExceptionHandle;
import com.amg.framework.boot.utils.json.FastJsonUtils;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import com.google.protobuf.GeneratedMessageV3.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * GRPC 对象转换工具类
 */
@Slf4j
public class GrpcJsonFormatUtils {

    /**
     * grpc 对象转 json 字符串
     * @param message
     * @return
     */
    public static String toJsonString(Message message) {
        StringBuilder sb = null;
        try {
            if (message == null)
                return null;
            sb = new StringBuilder();
            JsonFormat.printer()
                    .includingDefaultValueFields()
                    .appendTo(message, sb);
        } catch (Exception e) {
            log.error(ExceptionHandle.getExceptionMessage(e), e);
            return null;
        }
        return sb.toString();
    }


    public static String toJsonString(List<? extends Message> messages) {
        List<JSONObject> jsonObjects = new ArrayList<>();
        for (Message msg : messages) {
            String jsonstr = toJsonString(msg);
            if (StringUtils.isBlank(jsonstr))
                throw new NullPointerException();
            jsonObjects.add(JSONObject.parseObject(jsonstr));
        }
        return JSONArray.toJSONString(jsonObjects);
    }


    public static void grpcCopyToGrpc(Message source, Message target) {
        try {
            if (source == null || target == null)
                throw new NullPointerException();
            jsonToGrpc((Builder) target.newBuilderForType(), toJsonString(source));
        } catch (Exception e) {
            log.error(ExceptionHandle.getExceptionMessage(e), e);
        }
    }


    public static void grpcCopyToGrpc(Message source, Builder target) {
        try {
            if (source == null || target == null)
                throw new NullPointerException();
            jsonToGrpc(target, toJsonString(source));
        } catch (Exception e) {
            log.error(ExceptionHandle.getExceptionMessage(e), e);
        }
    }


    public static <T extends Message> T grpcCopyToGrpc(Message source, Class<T> target) {
        try {
            if (source == null || target == null)
                return null;
            return jsonToGrpc(target, toJsonString(source));
        } catch (Exception e) {
            log.error(ExceptionHandle.getExceptionMessage(e), e);
            return null;
        }
    }


    public static <T extends Message> List<T> grpcCopyToGrpc(List<? extends Message> sources, Class<T> target) {
        try {
            List<T> lists = new ArrayList<>();
            if (CollectionUtils.isEmpty(sources) || target == null)
                return lists;
            for (Message msg : sources) {
                T t = grpcCopyToGrpc(msg, target);
                if (t != null) {
                    lists.add(t);
                }
            }
            return lists;
        } catch (Exception e) {
            log.error(ExceptionHandle.getExceptionMessage(e), e);
            return null;
        }
    }


    /**
     * json 字符串转 grpc 对象
     * @param json
     * @return
     */
    public static <T extends Message> T jsonToGrpc(Builder builder, String json) {
        try {
            if (builder == null || StringUtils.isBlank(json))
                return null;
            JsonFormat.parser().ignoringUnknownFields().merge(json, builder);
            return (T) builder.build();
        } catch (Exception e) {
            log.error(ExceptionHandle.getExceptionMessage(e), e);
            return null;
        }
    }


    public static <T extends Message> T jsonToGrpc(Class<T> tClass, String json) {
        try {
            if (tClass == null || StringUtils.isBlank(json))
                return null;
            return jsonToGrpc(getBuilder(tClass), json);
        } catch (Exception e) {
            log.error(ExceptionHandle.getExceptionMessage(e), e);
            return null;
        }
    }


    public static <T extends Message> List<T> jsonToGrpcList(Class<T> tClass, String json) {
        List<T> lists = new ArrayList<>();
        if (tClass == null || StringUtils.isBlank(json))
            return lists;
        JSONArray jsonArray = JSONArray.parseArray(json);
        for (int i=0; i<jsonArray.size(); i++) {
            T t = jsonToGrpc(tClass, jsonArray.get(i).toString());
            if (t == null)
                throw new NullPointerException();
            lists.add(t);
        }
        return lists;
    }


    /**
     * java 对象转 grpc 对象
     * @param object
     * @param builder
     * @return
     */
    public static <T extends Message> T javaToGrpc(Object object, Builder builder) {
        try {
            if (object == null || builder == null)
                return null;
            if (!BeanUtil.isBean(object.getClass()))
                throw new IllegalArgumentException(object.getClass().getName() + " is not a JavaBean");
            Map<String, Object> jsonObject = JSON.parseObject(FastJsonUtils.toJSONString(object));
            for (String key : jsonObject.keySet()) {
                if (!key.endsWith("_"))
                    key += "_";
            }
            JsonFormat.parser().ignoringUnknownFields().merge(FastJsonUtils.toJSONString(jsonObject), builder);
            return (T) builder.build();
        } catch (Exception e) {
            log.error(ExceptionHandle.getExceptionMessage(e), e);
            return null;
        }
    }


    public static <T extends Message> List<T> javaToGrpc(List lists, Builder builder) {
        List<T> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(lists) || builder == null)
            return result;
        for (Object obj : lists) {
            T t = (T) javaToGrpc(obj, builder.build().getClass());
            if (t != null) {
                result.add(t);
            }
        }
        return result;
    }


    public static <T extends Message> List<T> javaToGrpc(List lists, Class<T> tClass) {
        List<T> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(lists) || tClass == null)
            return result;
        for (Object obj : lists) {
            T t = javaToGrpc(obj, tClass);
            if (t != null) {
                result.add(t);
            }
        }
        return result;
    }


    public static <T extends Message> T javaToGrpc(Object object, Class<T> tClass) {
        try {
            if (object == null || tClass == null)
                return null;
            return javaToGrpc(object, getBuilder(tClass));
        } catch (Exception e) {
            log.error(ExceptionHandle.getExceptionMessage(e), e);
            return null;
        }
    }


    public static <T> T grpcToJava(Message message, T object) {
        try {
            if (message == null || object == null)
                return null;
            return JSON.parseObject(toJsonString(message), (Type) object.getClass());
        } catch (Exception e) {
            log.error(ExceptionHandle.getExceptionMessage(e), e);
            return null;
        }
    }


    public static <T> T grpcToJava(Message message, Class<T> tClass) {
        try {
            if (message == null || tClass == null)
                return null;
            return JSON.parseObject(toJsonString(message), tClass);
        } catch (Exception e) {
            log.error(ExceptionHandle.getExceptionMessage(e), e);
            return null;
        }
    }


    public static <T> List<T> grpcToJava(List<? extends Message> messages, Class<T> tClass) {
        List<T> lists = new ArrayList<>();
        if (CollectionUtils.isEmpty(messages) || tClass == null)
            return lists;
        for (Message msg : messages) {
            T t = grpcToJava(msg, tClass);
            if (t != null) {
                lists.add(t);
            }
        }
        return lists;
    }


    public static <T> List<T> grpcToJava(List<? extends Message> messages, T object) {
        List<T> lists = new ArrayList<>();
        if (CollectionUtils.isEmpty(messages) || object == null)
            return lists;
        for (Message msg : messages) {
            T t = grpcToJava(msg, object);
            if (t != null) {
                lists.add(t);
            }
        }
        return lists;
    }


    public static Builder getBuilder(Class tclass) {
        try {
            Method method = tclass.getMethod("newBuilder");
            return (Builder) method.invoke(null);
        } catch (Exception e) {
            log.error(ExceptionHandle.getExceptionMessage(e), e);
            return null;
        }
    }

}

package com.amg.framework.boot.utils.json;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.amg.framework.boot.utils.date.DateUtils;
import java.math.BigInteger;
import java.util.Date;


public class FastJsonUtils {

    private static SerializeConfig sc = new SerializeConfig();

    public static String toJSONString(Object object) {
        sc.put(Date.class, new SimpleDateFormatSerializer(DateUtils.FORMAT_2)); // 日期格式处理
        sc.put(BigInteger.class, ToStringSerializer.instance);
        sc.put(Long.class, ToStringSerializer.instance);
        sc.put(Long.TYPE, ToStringSerializer.instance);
        return JSONObject.toJSONString(object, sc,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.DisableCircularReferenceDetect);
    }

}

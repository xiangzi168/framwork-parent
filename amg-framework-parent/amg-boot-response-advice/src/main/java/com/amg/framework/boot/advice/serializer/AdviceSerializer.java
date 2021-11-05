package com.amg.framework.boot.advice.serializer;

import com.amg.framework.boot.utils.common.Utils;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.amg.framework.boot.utils.json.FastJsonUtils;


public class AdviceSerializer {

    private static SerializeConfig sc = new SerializeConfig();

    public static void printSerializer(Object object) {
        Utils.print(FastJsonUtils.toJSONString(object));
    }

}

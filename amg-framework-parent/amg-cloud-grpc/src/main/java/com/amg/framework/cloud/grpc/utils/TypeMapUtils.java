package com.amg.framework.cloud.grpc.utils;

import java.util.HashMap;
import java.util.Map;


public class TypeMapUtils {

    private static final Map<Class, Class> primitiveClazz;

    static {
        primitiveClazz = new HashMap();
        primitiveClazz.put(Integer.class, int.class);
        primitiveClazz.put(Byte.class, byte.class);
        primitiveClazz.put(Character.class, char.class);
        primitiveClazz.put(Short.class, short.class);
        primitiveClazz.put(Long.class, long.class);
        primitiveClazz.put(Float.class, float.class);
        primitiveClazz.put(Double.class, double.class);
        primitiveClazz.put(Boolean.class, boolean.class);
    }

    public static Class getClass(Class clazz) {
        return primitiveClazz.get(clazz);
    }

}

package com.amg.framework.boot.canal.hander;

import com.amg.framework.boot.canal.annotation.CanalEventPoint;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lyc
 * @date 2020/10/9 15:34
 * @describe
 */
public class ListenerPoint {
    private Object target;
    private Map<Method, CanalEventPoint> invokeMap = new HashMap<>();

    public ListenerPoint(Object target, Method method, CanalEventPoint anno) {
        this.target = target;
        this.invokeMap.put(method, anno);
    }

    public Object getTarget() {
        return target;
    }

    public Map<Method, CanalEventPoint> getInvokeMap() {
        return invokeMap;
    }
}

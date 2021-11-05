package com.amg.framework.cloud.grpc.context;

import cn.hutool.core.util.ReflectUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.amg.framework.boot.utils.spring.SpringContextUtil;
import com.amg.framework.cloud.grpc.config.CircuitConfig;
import com.amg.framework.cloud.grpc.model.RemoteInfo;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;


@Component
public class GrpcContext {

    private static ThreadLocal response = new TransmittableThreadLocal();

    private static ConcurrentHashMap objectCache = new ConcurrentHashMap();

    private static Semaphore semaphore;

    private static ConcurrentHashMap<String, RemoteInfo> remoteInfoMap = new ConcurrentHashMap();

    public static CircuitConfig circuitConfig;


    @Autowired
    public void setCircuitConfig(CircuitConfig circuitConfig) {
        GrpcContext.circuitConfig = circuitConfig;
    }

    @PostConstruct
    public void setSemaphore() {
        GrpcContext.semaphore = new Semaphore(circuitConfig.getConcurrentThread());
    }

    public static Object getObject(Class obj) {
        Object o = null;
        if ((o = objectCache.get(obj)) != null) {
            return o;
        } else {
            o = ReflectUtil.newInstance(obj);
            objectCache.put(obj, o);
            return o;
        }
    }


    public static Method getObjectMethod(Class obj, String methodName) {
        Map<String, Method> map = null;
        if ((map = (Map) objectCache.get(obj)) != null) {
            return map.get(methodName);
        } else {
            map = new HashMap<>();
            Method[] methods = obj.getDeclaredMethods();
            for(Method md : methods) {
                map.put(md.getName(), md);
            }
            objectCache.put(obj, map);
            return getObjectMethod(obj, methodName);
        }
    }


    public static void buildRemoteInfo(String className, Class clazz, GrpcClient annotation) {
        if (StringUtils.isNotBlank(className) && clazz != null && annotation != null) {
            remoteInfoMap.put(className, new RemoteInfo(annotation.value(), annotation.fallback(),
                    clazz, validation(clazz, annotation)));
        }
    }


    public static RemoteInfo getRemoteInfo(String className) {
        return remoteInfoMap.get(className);
    }


    public static Method validation(Class clazz, GrpcClient grpcClient) {
        try {
            if (StringUtils.isNotBlank(grpcClient.fallback())) {
                Method method = clazz.getMethod(grpcClient.fallback(), new Class[]{RequestContext.class});
                if (method.getReturnType() == null) {
                    throw new NoSuchMethodException(method.getName() + " must be contain return type");
                }
                return method;
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    public static boolean isUsingFallback(String applicationName, String rpcMethodName) {
        CircuitConfig.SubCircuitConfig config = SpringContextUtil.getBean(CircuitConfig.class).getServers().get(applicationName);
        if (config != null) {
            Map<String, CircuitConfig.RpcCircuitConfig> rpcConfigMap = config.getRpc();
            if (MapUtils.isNotEmpty(rpcConfigMap) && StringUtils.isNotBlank(rpcMethodName)) {
                CircuitConfig.RpcCircuitConfig rpcConfig = rpcConfigMap.get(rpcMethodName);
                if (rpcConfig != null) {
                    return rpcConfig.isEnableFallback();
                }
            }
            return config.isEnableFallback();
        }
        return SpringContextUtil.getBean(CircuitConfig.class).isEnableFallback();
    }


    public static boolean isUsingRetry(String applicationName, String rpcMethodName) {
        CircuitConfig.SubCircuitConfig config = circuitConfig.getServers().get(applicationName);
        if (config != null) {
            Map<String, CircuitConfig.RpcCircuitConfig> rpcConfigMap = config.getRpc();
            if (MapUtils.isNotEmpty(rpcConfigMap) && StringUtils.isNotBlank(rpcMethodName)) {
                CircuitConfig.RpcCircuitConfig rpcConfig = rpcConfigMap.get(rpcMethodName);
                if (rpcConfig != null) {
                    return rpcConfig.isEnableRetry();
                }
            }
            return config.isEnableRetry();
        }
        return circuitConfig.isEnableRetry();
    }


    public static int getRetryTimes(String applicationName, String rpcMethodName) {
        CircuitConfig.SubCircuitConfig config = circuitConfig.getServers().get(applicationName);
        if (config != null) {
            Map<String, CircuitConfig.RpcCircuitConfig> rpcConfigMap = config.getRpc();
            if (MapUtils.isNotEmpty(rpcConfigMap) && StringUtils.isNotBlank(rpcMethodName)) {
                CircuitConfig.RpcCircuitConfig rpcConfig = rpcConfigMap.get(rpcMethodName);
                if (rpcConfig != null) {
                    return rpcConfig.getRetryTimes();
                }
            }
            return config.getRetryTimes();
        }
        return circuitConfig.getRetryTimes();
    }


    public static long getFallbackTimeout(String applicationName, String rpcMethodName) {
        if (isUsingFallback(applicationName, rpcMethodName)) {
            CircuitConfig.SubCircuitConfig subConfig = circuitConfig.getServers().get(applicationName);
            if (subConfig != null) {
                Map<String, CircuitConfig.RpcCircuitConfig> rpcConfigMap = subConfig.getRpc();
                if (MapUtils.isNotEmpty(rpcConfigMap) && StringUtils.isNotBlank(rpcMethodName)) {
                    CircuitConfig.RpcCircuitConfig rpcConfig = rpcConfigMap.get(rpcMethodName);
                    if (rpcConfig != null) {
                        return rpcConfig.getTimeout();
                    }
                }
                return subConfig.getTimeout();
            }
            return circuitConfig.getTimeout();
        }
        return circuitConfig.getDefaultTimeout();
    }

    public static Object getGrpcResult() {
        return response.get();
    }

    public static void returnGrpcResult(Object obj) {
        response.remove();
        response.set(obj);
    }

    public static void removeGrpcResult() {
        response.remove();
    }

    public static Semaphore getSemaphore() {
        return semaphore;
    }

}

package com.amg.framework.cloud.grpc.thread;

import cn.hutool.core.thread.ThreadUtil;
import com.amg.framework.boot.utils.spring.SpringContextUtil;
import com.amg.framework.boot.utils.thread.ThreadPoolUtil;
import com.amg.framework.cloud.grpc.context.GrpcContext;
import com.amg.framework.cloud.grpc.context.RequestContext;
import com.amg.framework.cloud.grpc.exception.GrpcException;
import com.amg.framework.cloud.grpc.model.InvokeError;
import com.amg.framework.cloud.grpc.model.RemoteInfo;
import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.*;
import io.grpc.stub.ClientCalls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.*;


public class CircuitExecute {

    private static Logger log = LoggerFactory.getLogger(CircuitExecute.class);

    private static ExecutorService threadPoolExecutor = null;

    static {
        if (threadPoolExecutor == null) {
            synchronized (CircuitExecute.class) {
                if (threadPoolExecutor == null) {
                    threadPoolExecutor = ThreadPoolUtil.getNewInstance(Runtime.getRuntime().availableProcessors(), Integer.MAX_VALUE,
                            new SynchronousQueue<Runnable>(), "grpc");
                }
            }
        }
    }


    public static Object circuit(Channel channel, MethodDescriptor method, CallOptions callOptions, Object object, String className) {
        boolean permit = false;
        try {
            permit = GrpcContext.getSemaphore().tryAcquire();
            Object result = executeWithPermit(channel, method, callOptions, object, permit, className);
            return resolveResult(result, channel, method, callOptions, object, className);
        } catch (Exception e) {
            log.error("【Circuit Error】", e);
            throw new GrpcException(e);
        } finally {
            if (permit) {
                GrpcContext.getSemaphore().release();
            }
        }
    }


    private static Object execute(Channel channel, MethodDescriptor method, CallOptions callOptions, Object object, String className) {
        ListenableFuture responseFuture = null;
        ClientCall call = null;
        try {
            call = channel.newCall(method,
                    callOptions.withOption(ClientCalls.STUB_TYPE_OPTION, ClientCalls.StubType.BLOCKING).withExecutor(threadPoolExecutor));
            responseFuture = ClientCalls.futureUnaryCall(call, object);
            long timeout = GrpcContext.getFallbackTimeout(GrpcContext.getRemoteInfo(className).getApplicationName(), method.getFullMethodName());
            return responseFuture.get(timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            log.error("【Execute TimeoutException】", e);
            callCancel(call, e);
            return new InvokeError("-1", new GrpcException("invoke remote server " +
                    GrpcContext.getRemoteInfo(className).getApplicationName() + ":" + method.getFullMethodName() + " timeout"));
        } catch (Exception e) {
            log.error("【Execute Exception】", e);
            callCancel(call, e);
            if (e.getCause() != null && e.getCause().getMessage() != null && e.getCause().getMessage().startsWith("UNAVAILABLE")) {
                // 重试响应码
                return new InvokeError("-1", new GrpcException(e.getCause() != null ? e.getCause() : e));
            } else {
                // 降级响应码
                return new InvokeError("0", new GrpcException(e.getCause() != null ? e.getCause() : e));
            }
        } finally {
            responseFuture.cancel(true);
        }
    }


    /**
     * 获得资源执行
     */
    private static Object executeWithPermit(Channel channel, MethodDescriptor method, CallOptions callOptions, Object object, boolean permit, String className) {
        if (permit) {
            return execute(channel, method, callOptions, object, className);
        } else {
            return new InvokeError("0", new GrpcException("Concurrent Thread Limit"));
        }
    }


    /**
     * 解析响应
     */
    public static Object resolveResult(Object result, Channel channel, MethodDescriptor method, CallOptions callOptions, Object object, String className) {
        if (isInvokeError(result)) {
            // 判断是否重试
            boolean retry = isHaveRetry(result, method, className);
            if (retry) {
                return retry(channel, method, callOptions, object, className);
            }
            return handleFallback(method, object, result, className);
        }
        return result;
    }


    /**
     * 重试
     */
    public static Object retry(Channel channel, MethodDescriptor method, CallOptions callOptions, Object object, String className) {
        int retryTimes = GrpcContext.getRetryTimes(GrpcContext.getRemoteInfo(className).getApplicationName(), method.getFullMethodName());
        Object result = null;
        for (int i = 0; i< retryTimes; i ++) {
            ThreadUtil.sleep(500); // 延迟500毫秒重试
            log.warn("Retry the {} for the {}th time", method.getFullMethodName(), i + 1);
            result = execute(channel, method, callOptions, object, className);
            if (!isHaveRetry(result, method, className)) {
                break;
            }
        }
        // 是否错误响应
        if (isInvokeError(result)) {
            return handleFallback(method, object, result, className);
        }
        return result;
    }


    /**
     * 降级处理
     */
    public static Object handleFallback(MethodDescriptor method, Object object, Object result, String className) {
        if (isEnableFallback(method, className)) {
            try {
                RemoteInfo remoteInfo = GrpcContext.getRemoteInfo(className);
                Object obj = SpringContextUtil.getBean(remoteInfo.getUpperClass());
                if (remoteInfo.getMethod() != null) {
                    return remoteInfo.getMethod().invoke(obj, new Object[]{new RequestContext
                            (method.getFullMethodName(), object, ((InvokeError) result).getThrowable())});
                }
            } catch (Exception e) {
                log.error("【HandleFallback Error】", e);
                throw new GrpcException(e);
            }
        }
        Throwable e = ((InvokeError) result).getThrowable();
        if (e instanceof GrpcException) {
            throw (GrpcException) e;
        } else {
            throw new GrpcException(e);
        }
    }


    /**
     * 判断是否重试
     */
    public static boolean isHaveRetry(Object result, MethodDescriptor method, String className) {
        boolean isRetry = GrpcContext.isUsingRetry(GrpcContext.getRemoteInfo(className).getApplicationName(), method.getFullMethodName());
        if (isInvokeError(result)) {
            return isRetry && ((InvokeError) result).getErrorCode().equals("-1");
        }
        return false;
    }


    /**
     * 判断是否开启降级处理
     */
    public static boolean isEnableFallback(MethodDescriptor method, String className) {
        RemoteInfo remoteInfo = GrpcContext.getRemoteInfo(className);
        return GrpcContext.isUsingFallback(remoteInfo.getApplicationName(), method.getFullMethodName());
    }


    /**
     * 判断是否错误响应
     */
    public static boolean isInvokeError(Object result) {
        return result instanceof InvokeError;
    }


    private static void callCancel(ClientCall call, Throwable throwable) {
        try {
            call.cancel(throwable.getMessage(), throwable);
        } catch (Exception e) {
            log.error("【CallCancel Error】", e);
        }
    }

}

package com.amg.framework.cloud.grpc.handle;

import com.amg.framework.boot.base.enums.ResponseCodeEnum;
import com.amg.framework.boot.base.exception.GlobalException;
import com.amg.framework.boot.base.exception.handle.ExceptionHandle;
import com.amg.framework.boot.logging.constant.LogTraceConstant;
import com.amg.framework.boot.utils.json.FastJsonUtils;
import com.amg.framework.cloud.grpc.context.GrpcContext;
import com.amg.framework.cloud.grpc.exception.GrpcException;
import com.amg.framework.cloud.grpc.utils.GrpcJsonFormatUtils;
import com.amg.framework.cloud.grpc.utils.TypeMapUtils;
import com.google.protobuf.Message;
import io.grpc.stub.StreamObserver;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import java.lang.reflect.*;
import java.util.Collection;


/**
 * 响应处理
 */
public class ResponseHandle {

    private static Logger logger = LoggerFactory.getLogger(ResponseHandle.class);

    public static void handle(ProceedingJoinPoint pj, Exception exception) throws Exception {
        try {
            // 获取注解的方法参数列表
            Object[] args = pj.getArgs();
            StreamObserver streamObserver = (StreamObserver) args[args.length - 1];

            // 获取被注解的方法
            MethodInvocationProceedingJoinPoint mjp = (MethodInvocationProceedingJoinPoint) pj;
            MethodSignature signature = (MethodSignature) mjp.getSignature();
            Class target = (Class) pj.getTarget().getClass().getGenericSuperclass();
            Method method = target.getMethod(signature.getName(), signature.getParameterTypes());
            Parameter[] parameters = method.getParameters();
            ParameterizedType p = (ParameterizedType) parameters[1].getParameterizedType();
            Class c = (Class) p.getActualTypeArguments()[0];
            Object result = buildResult(c, exception);

            // 完成grpc请求
            streamObserver.onNext(result);
            streamObserver.onCompleted();


            String logStr = "【GRPC接口响应日志】\r\n";
            logStr += "[GRPC METHOD]: " + MDC.get(LogTraceConstant.TRACE_METHOD_NAME) + "\r\n";
            logStr += "[CLASS METHOD]: " + signature.getMethod().toString() + "\r\n";
            logStr += "[RESPONSE DATA]: " + GrpcJsonFormatUtils.toJsonString((Message) result);
            logger.debug(logStr);

        } catch (Exception e) {
            logger.error("【ResponseHandle】" + ExceptionHandle.getExceptionMessage(e), e);
            throw e;
        } finally {
            GrpcContext.removeGrpcResult();
        }
    }


    private static Object buildResult(Class clazz, Exception exception) throws Exception {
        // 获取方法执行结果
        Object result = GrpcContext.getGrpcResult();
       // validateGrpcResult(clazz, result, exception);

        // 缓存拿对象
        Object o0 = GrpcContext.getObject(clazz);
        Method newBuilder = o0.getClass().getMethod("newBuilder");
        Object o1 = newBuilder.invoke(o0);

        Method setRequestId = o1.getClass().getMethod("setRequestId", String.class);
        Object o2 = setRequestId.invoke(o1, MDC.get("requestId"));

        Method setErrorCode = o2.getClass().getMethod("setErrorCode", String.class);
        Object o3 = setErrorCode.invoke(o2, exception == null ? ResponseCodeEnum.RETURN_CODE_100200.getCode()
                : (exception instanceof GlobalException ? ((GlobalException) exception).getErrorCode()
                : ResponseCodeEnum.RETURN_CODE_100500.getCode()));

        Method setMsg = o3.getClass().getMethod("setMsg", String.class);
        Object o4 = setMsg.invoke(o3, exception == null ? "请求成功" : (exception instanceof GlobalException
                ? exception.getMessage() : ExceptionHandle.getExceptionMessage(exception)));

        Method setSuccess = o4.getClass().getMethod("setSuccess", boolean.class);
        Object o5 = setSuccess.invoke(o4, exception == null ? true : false);

        Object o6 = null;

        if (result != null && exception == null) {
            if (result instanceof Iterable) {
                Method setData = o5.getClass().getMethod("addAllData", Iterable.class);
                o6 = setData.invoke(o5, result);
            } else {
                Method setData = null;
                try {
                    // 判断是否有集合操作方法
                    setData = o5.getClass().getMethod("addAllData", Iterable.class);
                    setData = o5.getClass().getMethod("addData", TypeMapUtils.getClass(result.getClass()) == null ? result.getClass() : TypeMapUtils.getClass(result.getClass()));
                    o6 = setData.invoke(o5, result);
                } catch (Exception e) {
                    setData = o5.getClass().getMethod("setData", TypeMapUtils.getClass(result.getClass()) == null ? result.getClass() : TypeMapUtils.getClass(result.getClass()));
                    o6 = setData.invoke(o5, result);
                }
            }
        } else {
            o6 = o5;
        }

        Method build = o6.getClass().getMethod("build");
        Object o7 = build.invoke(o6);

        return o7;
    }


    private static void validateGrpcResult(Class clazz, Object result, Exception e) {
        if (result != null) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if ("data_".equals(field.getName())) {
                    Type type = field.getGenericType();
                    ParameterizedType tp = (ParameterizedType) type;
                    if (tp.getRawType().getClass().isInstance(Collection.class)) {
                        // 获取集合类泛型
                        ParameterizedType p = (ParameterizedType) field.getGenericType();
                        Class c = (Class) p.getActualTypeArguments()[0];

                        if (result.getClass() != c) {
                            e = new GrpcException("the Method 'returnGrpcResult(...)' must be return type '" + c.toString() + "'");
                        }
                    } else {
                        if (result.getClass() != tp.getRawType().getClass()) {
                            e = new GrpcException("the Method 'returnGrpcResult(...)' must be return type '" + tp.getRawType().getClass() + "'");
                        }
                    }
                }
            }
        }
    }

}

package com.amg.framework.cloud.grpc.aspect;

import com.amg.framework.boot.base.exception.handle.ExceptionHandle;
import com.amg.framework.boot.logging.constant.LogTraceConstant;
import com.amg.framework.cloud.grpc.handle.ResponseHandle;
import com.amg.framework.cloud.grpc.utils.GrpcJsonFormatUtils;
import com.google.protobuf.Message;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class ResponseAspect {

    private static Logger logger = LoggerFactory.getLogger(ResponseAspect.class);

    public ResponseAspect() {
    }
    
    @Pointcut("@within(net.devh.boot.grpc.server.service.GrpcService) && execution(void com.amg.*.cloud..*.*(.., io.grpc.stub.StreamObserver))")
    public void aspect() {
    }

    @Around("aspect()")
    public void around(ProceedingJoinPoint pjp) throws Throwable {

        try {
            Object obj = pjp.getArgs()[0];
            MethodSignature signature = (MethodSignature) pjp.getSignature();
            String logStr = "【GRPC接口请求日志】\r\n";
            logStr += "[GRPC METHOD]: " + MDC.get(LogTraceConstant.TRACE_METHOD_NAME) + "\r\n";
            logStr += "[CLASS METHOD]: " + signature.getMethod().toString() + "\r\n";
            logStr += "[REQUEST PARAMETERS]: " + GrpcJsonFormatUtils.toJsonString((Message) obj);
            logger.debug(logStr);

            // 执行rpc
            pjp.proceed();

            // 统一处理响应
            ResponseHandle.handle(pjp, null);

        } catch (Exception exception) {
            logger.error("【ResponseAspect】" + ExceptionHandle.getExceptionMessage(exception), exception);
            ResponseHandle.handle(pjp, exception);
            throw exception;
        }
    }

}

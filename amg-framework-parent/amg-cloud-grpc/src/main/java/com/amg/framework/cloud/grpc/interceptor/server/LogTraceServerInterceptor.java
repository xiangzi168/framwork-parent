package com.amg.framework.cloud.grpc.interceptor.server;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.amg.framework.boot.logging.constant.LogTraceConstant;
import com.amg.framework.cloud.grpc.context.UserContext;
import io.grpc.*;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;


/**
 * 服务端拦截 requestId
 */
@GrpcGlobalServerInterceptor
public class LogTraceServerInterceptor implements ServerInterceptor {

    private static Logger logger = LoggerFactory.getLogger(LogTraceServerInterceptor.class);

    private static final Metadata.Key<String> TRACE_ID =
            Metadata.Key.of(LogTraceConstant.TRACE_ID, Metadata.ASCII_STRING_MARSHALLER);

    private static final Metadata.Key<String> TRACE_STAGE_ID =
            Metadata.Key.of(LogTraceConstant.TRACE_STAGE_ID, Metadata.ASCII_STRING_MARSHALLER);

    private static final Metadata.Key<String> OSS_NAME =
            Metadata.Key.of("oss-name", Metadata.ASCII_STRING_MARSHALLER);

    private static final Metadata.Key<String> OSS_FULLNAME =
            Metadata.Key.of("oss-fullname", Metadata.ASCII_STRING_MARSHALLER);

    private static final Metadata.Key<String> OSS_EMAIL =
            Metadata.Key.of("oss-email", Metadata.ASCII_STRING_MARSHALLER);

    private static final Metadata.Key<String> OSS_ACCESS =
            Metadata.Key.of("oss-access", Metadata.ASCII_STRING_MARSHALLER);

    private static final Metadata.Key<String> REQUESTID =
            Metadata.Key.of("request-id", Metadata.ASCII_STRING_MARSHALLER);


    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
        ServerCall<ReqT, RespT> call = new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(serverCall) {
            @Override
            public void sendHeaders(Metadata headers) {
                super.sendHeaders(headers);
            }
        };
        ServerCall.Listener<ReqT> delegate = serverCallHandler.startCall(call, metadata);
        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(delegate) {
            @Override
            public void onHalfClose() {
                long startTime = System.currentTimeMillis();
                try {
                    String requestId = metadata.get(TRACE_ID);
                    String stageId = metadata.get(TRACE_STAGE_ID);
                    MDC.put(LogTraceConstant.TRACE_ID, requestId == null ? UUID.randomUUID().toString().replaceAll("\\-", "") : requestId);
                    MDC.put(LogTraceConstant.TRACE_STAGE_ID, RandomUtil.randomNumbers(10));
                    MDC.put(LogTraceConstant.TRACE_STAGE_PARENT_ID, stageId == null ? "0" : stageId);
                    MDC.put(LogTraceConstant.TRACE_METHOD_NAME, serverCall.getMethodDescriptor().getFullMethodName());

                    UserContext.setLocalHeader("oss-name", metadata.get(OSS_NAME));
                    UserContext.setLocalHeader("oss-fullname", metadata.get(OSS_FULLNAME));
                    UserContext.setLocalHeader("oss-email", metadata.get(OSS_EMAIL));
                    UserContext.setLocalHeader("oss-access", metadata.get(OSS_ACCESS));
                    UserContext.setLocalHeader("request-id", metadata.get(REQUESTID));

                    logger.debug("grpc server execute start");
                    super.onHalfClose();
                    logger.debug("grpc server execute complete, executeTime#" + (System.currentTimeMillis() - startTime));
                } catch (Exception e) {
                    logger.error("【LogTraceServerInterceptor Error】", e);
                    logger.debug("grpc server execute complete, executeTime#" + (System.currentTimeMillis() - startTime));
                   // call.close(Status.INTERNAL.withCause(e).withDescription(ExceptionHandle.getExceptionMessage(e)), new Metadata());
                }
            }
        };
    }

}

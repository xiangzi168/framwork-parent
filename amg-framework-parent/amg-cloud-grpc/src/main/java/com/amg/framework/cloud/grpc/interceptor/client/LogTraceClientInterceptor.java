package com.amg.framework.cloud.grpc.interceptor.client;

import cn.hutool.core.util.RandomUtil;
import com.amg.framework.boot.logging.constant.LogTraceConstant;
import com.amg.framework.cloud.grpc.context.UserContext;
import io.grpc.*;
import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;


/**
 * 客户端设置 requestId
 */
@GrpcGlobalClientInterceptor
public class LogTraceClientInterceptor implements ClientInterceptor {

    private static Logger logger = LoggerFactory.getLogger(LogTraceClientInterceptor.class);

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
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method,
                                                               CallOptions callOptions,
                                                               Channel next) {
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                long startTime = System.currentTimeMillis();
                String stage = RandomUtil.randomNumbers(10);
                logger.debug("remote grpc request 【{}】 method 【{}】 execute start", stage, method.getFullMethodName());
                String requestId = MDC.get(LogTraceConstant.TRACE_ID);
                String stageId = MDC.get(LogTraceConstant.TRACE_STAGE_ID);
                //String stageParentId = MDC.get(LogTraceConstant.TRACE_STAGE_PARENT_ID);
                //headers.put(TRACE_STAGE_PARENT_ID, stageParentId == null ? "0" : stageParentId);

                try {
                    headers.put(TRACE_ID, requestId);
                    headers.put(TRACE_STAGE_ID, stageId);
                    if (UserContext.getCurrentUserName() != null)
                        headers.put(OSS_NAME, UserContext.getCurrentUserName());
                    if (UserContext.getCurrentUserFullName() != null)
                        headers.put(OSS_FULLNAME, UserContext.getCurrentUserFullName());
                    if (UserContext.getCurrentUserEmail() != null)
                        headers.put(OSS_EMAIL, UserContext.getCurrentUserEmail());
                    if (UserContext.getCurrentUserAccess() != null)
                        headers.put(OSS_ACCESS, UserContext.getCurrentUserAccess());
                    if (UserContext.getCurrentUserRequestId() != null)
                        headers.put(REQUESTID, UserContext.getCurrentUserRequestId());
                } catch (Exception e) {
                }

                super.start(new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {
                    public void onClose(Status status, Metadata trailers) {
                        delegate().onClose(status, trailers);
                        logger.debug("remote grpc request 【{}】 method 【{}】 execute complete execute time: {}ms", stage,
                                method.getFullMethodName(), System.currentTimeMillis() - startTime);
                    }
                }, headers);
            }
        };
    }

}

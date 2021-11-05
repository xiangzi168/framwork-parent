package com.amg.framework.cloud.grpc.exception;

import com.amg.framework.boot.base.exception.handle.ExceptionHandle;

public class GrpcException extends RuntimeException {

    public GrpcException(String message) {
        super(message);
    }

    public GrpcException(Exception exception) {
        super(ExceptionHandle.getExceptionMessage(exception), exception);
    }

    public GrpcException(Throwable throwable) {
        super(ExceptionHandle.getExceptionMessage(throwable), throwable);
    }

}

package com.amg.framework.cloud.grpc.model;

public class InvokeError {

    private String errorCode;

    private Throwable throwable;

    public InvokeError(String errorCode, Throwable throwable) {
        this.errorCode = errorCode;
        this.throwable = throwable;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
}

package com.amg.framework.cloud.grpc.context;

import io.grpc.StatusRuntimeException;


public class RequestContext {

    private String methodName;
    private Object parameters;
    private Throwable throwable;

    public RequestContext(String methodName, Object parameters, Throwable throwable) {
        this.methodName = methodName;
        this.parameters = parameters;
        this.throwable = throwable;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public Throwable getGrpcException() {
        Throwable t = throwable.getCause();
        if (t != null) {
            if (t instanceof StatusRuntimeException) {
                return t;
            }
        }
        return null;
    }

}

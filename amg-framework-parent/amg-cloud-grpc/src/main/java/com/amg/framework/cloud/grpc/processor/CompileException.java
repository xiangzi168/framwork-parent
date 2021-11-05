package com.amg.framework.cloud.grpc.processor;


public class CompileException extends RuntimeException{

    public CompileException(String message) {
        super(message);
    }

    public CompileException(Exception exception) {
        super(exception);
    }

}

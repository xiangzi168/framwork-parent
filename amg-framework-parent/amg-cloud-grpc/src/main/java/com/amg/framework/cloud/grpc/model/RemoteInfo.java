package com.amg.framework.cloud.grpc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.lang.reflect.Method;

@Data
@AllArgsConstructor
public class RemoteInfo {

    private String applicationName;
    private String fallback;
    private Class upperClass;
    private Method method;

}

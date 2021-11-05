package io.grpc.grpcswagger.openapi.v2;

import lombok.Data;

@Data
public class Parameter {
    private String in = "body";
    private String name;
    private String description;
    private boolean required = true;
    private ParameterSchema schema;
}

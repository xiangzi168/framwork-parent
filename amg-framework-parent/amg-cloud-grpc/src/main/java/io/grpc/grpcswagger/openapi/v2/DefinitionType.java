package io.grpc.grpcswagger.openapi.v2;

import java.util.Map;

import lombok.Data;

@Data
public class DefinitionType {
    private String type;
    private String title;
    private Map<String, FieldProperty> properties;
}

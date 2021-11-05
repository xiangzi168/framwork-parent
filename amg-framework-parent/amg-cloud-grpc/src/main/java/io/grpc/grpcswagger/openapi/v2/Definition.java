package io.grpc.grpcswagger.openapi.v2;

import java.util.Map;

import lombok.Data;

@Data
public class Definition {
    private Map<String, DefinitionType> definitionTypeMap;
}

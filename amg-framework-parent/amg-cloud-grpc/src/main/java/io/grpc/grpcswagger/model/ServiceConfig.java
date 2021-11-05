package io.grpc.grpcswagger.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceConfig {
    private String service;
    private String endPoint;
}

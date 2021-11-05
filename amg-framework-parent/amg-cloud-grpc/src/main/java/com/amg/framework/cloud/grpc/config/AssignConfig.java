package com.amg.framework.cloud.grpc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;


@Data
@Configuration
@ConfigurationProperties(prefix = "grpc.server.assign")
public class AssignConfig {

    private boolean enable = true;

    private String defaultStrategy = "priorityHighlevelStrategy";

    private Map<String, Map> customStrategy = new HashMap<>();

}

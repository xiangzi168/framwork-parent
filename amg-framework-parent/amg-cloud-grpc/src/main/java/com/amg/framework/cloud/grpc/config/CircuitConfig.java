package com.amg.framework.cloud.grpc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
@Configuration
@ConfigurationProperties(prefix = "spring.circuit")
public class CircuitConfig {

    private boolean enableRetry;
    private boolean enableFallback;
    private int retryTimes;
    private long timeout;
    private int concurrentThread = 5000; // 默认5000并发线程
    private long defaultTimeout = 30000; // 默认降级超时时间
    private Map<String, SubCircuitConfig> servers = new HashMap<>();

    @Data
    public static class SubCircuitConfig {
        private Map<String, RpcCircuitConfig> rpc;
        private boolean enableRetry;
        private boolean enableFallback;
        private long timeout;
        private int retryTimes;
    }

    @Data
    public static class RpcCircuitConfig {
        private boolean enableRetry;
        private boolean enableFallback;
        private long timeout;
        private int retryTimes;
    }
}

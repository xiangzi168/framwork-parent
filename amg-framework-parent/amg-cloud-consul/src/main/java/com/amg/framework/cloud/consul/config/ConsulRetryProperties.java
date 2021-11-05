package com.amg.framework.cloud.consul.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("spring.cloud.consul.retry")
public class ConsulRetryProperties {
    /**
     * 监测间隔（单位：ms）
     */
    private long initialInterval = 10000L;
    /**
     * 间隔因子（备用）
     */
    private double multiplier = 1.1D;
    /**
     * 最大间隔（备用）
     */
    private long maxInterval = 20000L;
    /**
     * 重试次数（备用）
     */
    private int maxAttempts = 6;

    public long getInitialInterval() {
        return initialInterval;
    }

    public void setInitialInterval(long initialInterval) {
        this.initialInterval = initialInterval;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public long getMaxInterval() {
        return maxInterval;
    }

    public void setMaxInterval(long maxInterval) {
        this.maxInterval = maxInterval;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }
}

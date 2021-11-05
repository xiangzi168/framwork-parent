package com.amg.framework.boot.redisson.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix="spring.redis")
public class RedissonProperties {

    private int database;

    /**
     * 等待节点回复命令的时间。该时间从命令发送成功时开始计时
     */
    private int timeout;

    private String password;

    private String mode;

    private String host;

    private String port;

    /**
     * 池配置
     */
    private RedissonPoolProperties lettuce;

    /**
     * 单机信息配置
     */
    private RedissonSingleProperties single;

    /**
     * 集群 信息配置
     */
    private RedissonClusterProperties cluster;

    /**
     * 哨兵配置
     */
    private RedissonSentinelProperties sentinel;

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public RedissonPoolProperties getLettuce() {
        return lettuce;
    }

    public void setLettuce(RedissonPoolProperties lettuce) {
        this.lettuce = lettuce;
    }

    public RedissonSingleProperties getSingle() {
        return single;
    }

    public void setSingle(RedissonSingleProperties single) {
        this.single = single;
    }

    public RedissonClusterProperties getCluster() {
        return cluster;
    }

    public void setCluster(RedissonClusterProperties cluster) {
        this.cluster = cluster;
    }

    public RedissonSentinelProperties getSentinel() {
        return sentinel;
    }

    public void setSentinel(RedissonSentinelProperties sentinel) {
        this.sentinel = sentinel;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}

package com.amg.framework.boot.canal.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author chen.qian
 * @date 2018/3/16
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConfigurationProperties(prefix = "canal.client")
public class CanalConfig {

    private boolean clusterEnabled;


    private Set<String> zookeeperAddress = new LinkedHashSet<>();


    private String host = "127.0.0.1";


    private int port = 10001;


    private String userName = "";


    private String password = "";


    private int batchSize = 1000;


    private String filter;


    private int retryCount = 5;


    private long acquireInterval = 1000;

    private String destination;

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }


    public boolean getClusterEnabled() {
        return clusterEnabled;
    }

    public void setClusterEnabled(boolean clusterEnabled) {
        this.clusterEnabled = clusterEnabled;
    }

    public Set<String> getZookeeperAddress() {
        return zookeeperAddress;
    }

    public void setZookeeperAddress(Set<String> zookeeperAddress) {
        this.zookeeperAddress = zookeeperAddress;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public long getAcquireInterval() {
        return acquireInterval;
    }

    public void setAcquireInterval(long acquireInterval) {
        this.acquireInterval = acquireInterval;
    }



}

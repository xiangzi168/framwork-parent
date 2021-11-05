package com.amg.framework.boot.redisson.config;


import java.util.List;

/**
 * @describe 集群配置
 */
public class RedissonClusterProperties {


    /**
     * 获取失败 最大重定向次数
     */
    private int maxRedirects;

    /**
     * 集群节点
     */
    private List<String> nodes;

    public int getMaxRedirects() {
        return maxRedirects;
    }

    public void setMaxRedirects(int maxRedirects) {
        this.maxRedirects = maxRedirects;
    }

    public List<String> getNodes() {
        return nodes;
    }

    public void setNodes(List<String> nodes) {
        this.nodes = nodes;
    }
}

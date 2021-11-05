package com.amg.framework.boot.redisson.config;

import java.util.List;

/**
 * @describe 哨兵配置
 */
public class RedissonSentinelProperties {
    /**
     * 哨兵master 名称
     */
    private String master;

    /**
     * 哨兵节点
     */
    private List<String> nodes;

    /**
     * 哨兵配置
     */
    private boolean masterOnlyWrite;

    /**
     *
     */
    private int failMax;

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public List<String> getNodes() {
        return nodes;
    }

    public void setNodes(List<String> nodes) {
        this.nodes = nodes;
    }

    public boolean isMasterOnlyWrite() {
        return masterOnlyWrite;
    }

    public void setMasterOnlyWrite(boolean masterOnlyWrite) {
        this.masterOnlyWrite = masterOnlyWrite;
    }

    public int getFailMax() {
        return failMax;
    }

    public void setFailMax(int failMax) {
        this.failMax = failMax;
    }
}

package com.amg.framework.boot.canal.client;

/**
 * @author lyc
 * @date 2020/9/26 17:43
 * @describe
 */
public interface CanalClient {
    void start();
    void stop();
    void process();
}

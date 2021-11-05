package com.amg.framework.cloud.rocketmq.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;


@Configuration
public class RocketMQConfig {

    @Value("${rocketmq.listener.enable:false}")
    private boolean rocketmqListenerEnable;

    public static boolean rocketMQListenerEnable;

    @PostConstruct
    public void init() {
        rocketMQListenerEnable = rocketmqListenerEnable;
    }

}

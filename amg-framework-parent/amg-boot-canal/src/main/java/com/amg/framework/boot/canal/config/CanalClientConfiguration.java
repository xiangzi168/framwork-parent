package com.amg.framework.boot.canal.config;

import com.amg.framework.boot.canal.hander.SimpleMessageHandler;
import com.amg.framework.boot.canal.hander.SimpleOption;
import com.amg.framework.boot.canal.client.CanalClient;
import com.amg.framework.boot.canal.client.SimpleCanalClient;
import com.amg.framework.boot.canal.util.ApplicationContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;


/**
 * @author lyc
 * @date 2020/10/9 15:42
 * @describe
 */
public class CanalClientConfiguration {

    private final static Logger logger = LoggerFactory.getLogger(CanalClientConfiguration.class);

    @Autowired
    private CanalConfig canalConfig;


    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ApplicationContextUtil beanUtil() {
        return new ApplicationContextUtil();
    }

    @Bean
    public SimpleMessageHandler simpleMessageHandler(){
        SimpleMessageHandler simpleMessageHandler = new SimpleMessageHandler();
        simpleMessageHandler.setOption(new SimpleOption());
        return simpleMessageHandler;
    }

    @Bean(destroyMethod = "stop")
    public CanalClient canalClient() {
        SimpleCanalClient canalClient = new SimpleCanalClient(simpleMessageHandler(),canalConfig);
        canalClient.start();
        return canalClient;
    }
}

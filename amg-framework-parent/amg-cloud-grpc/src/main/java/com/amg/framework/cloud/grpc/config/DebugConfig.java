package com.amg.framework.cloud.grpc.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;


@Profile("dev")
@Component
public class DebugConfig extends InstantiationAwareBeanPostProcessorAdapter implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        if (StringUtils.isBlank(applicationContext.getEnvironment().getProperty("consul.register.local"))) {
            System.setProperty("spring.application.name", applicationContext.getEnvironment().
                    getProperty("spring.application.name") + "-local");
        }
    }

}

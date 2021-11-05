package com.amg.framework.cloud.consul.config;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryClient;
import org.springframework.cloud.consul.serviceregistry.ConsulAutoRegistration;
import org.springframework.cloud.consul.serviceregistry.ConsulServiceRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import com.amg.framework.cloud.consul.retry.ConsulRetryRegistry;


@Configuration
@ConditionalOnClass({
        ConsulDiscoveryClient.class,
        ConsulAutoRegistration.class,
        ConsulServiceRegistry.class
})
@Import({ConsulRetryProperties.class})
@ComponentScan("com")
@EnableEncryptableProperties
public class ConsulRetryAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean({ConsulRetryRegistry.class})
    public ConsulRetryRegistry consulRetryRegistry(
            ConsulAutoRegistration consulAutoRegistration,
            ConsulServiceRegistry consulServiceRegistry,
            DiscoveryClient discoveryClient,
            ConsulRetryProperties properties
    ) {
        return new ConsulRetryRegistry(
                consulAutoRegistration,
                consulServiceRegistry,
                discoveryClient,
                properties
        );
    }
}

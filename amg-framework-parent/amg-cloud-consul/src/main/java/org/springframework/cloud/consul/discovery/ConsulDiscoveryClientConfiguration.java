/*
 * Copyright 2013-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.consul.discovery;

import com.ecwid.consul.v1.ConsulClient;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.CommonsClientAutoConfiguration;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClientAutoConfiguration;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.consul.ConditionalOnConsulEnabled;
import org.springframework.cloud.consul.config.ConsulPropertySourceLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Spencer Gibb
 * @author Olga Maciaszek-Sharma
 * @author Tim Ysewyn
 */
@Configuration
@ConditionalOnConsulEnabled
@ConditionalOnProperty(value = "spring.cloud.consul.discovery.enabled", matchIfMissing = true)
@ConditionalOnDiscoveryEnabled
@EnableConfigurationProperties
@AutoConfigureBefore({ SimpleDiscoveryClientAutoConfiguration.class,
        CommonsClientAutoConfiguration.class })
public class ConsulDiscoveryClientConfiguration {


    /**
     * Name of the catalog watch task scheduler bean.
     * @Deprecated Moved to {@link ConsulCatalogWatchAutoConfiguration}.
     */
    @Deprecated
    public static final String CATALOG_WATCH_TASK_SCHEDULER_NAME = ConsulCatalogWatchAutoConfiguration.CATALOG_WATCH_TASK_SCHEDULER_NAME;

    @Bean
    @ConditionalOnMissingBean
    public ConsulDiscoveryProperties consulDiscoveryProperties(InetUtils inetUtils) {
        return new ConsulDiscoveryProperties(inetUtils);
    }

    @Bean
    @ConditionalOnMissingBean
    public ConsulDiscoveryClient consulDiscoveryClient(ConsulClient consulClient,
                                                       ConsulDiscoveryProperties discoveryProperties,
                                                       ConsulPropertySourceLocator consulPropertySourceLocator) {
        return new ConsulDiscoveryClient(consulClient, discoveryProperties, consulPropertySourceLocator);
    }

}

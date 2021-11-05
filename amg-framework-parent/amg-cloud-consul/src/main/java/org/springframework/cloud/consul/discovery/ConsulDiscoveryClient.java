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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.health.model.HealthService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.consul.config.ConsulPropertySource;
import org.springframework.cloud.consul.config.ConsulPropertySourceLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.util.StringUtils;
import static org.springframework.cloud.consul.discovery.ConsulServerUtils.findHost;
import static org.springframework.cloud.consul.discovery.ConsulServerUtils.getMetadata;

/**
 * @author Spencer Gibb
 * @author Joe Athman
 * @author Tim Ysewyn
 */
public class ConsulDiscoveryClient extends InstantiationAwareBeanPostProcessorAdapter implements DiscoveryClient, BeanFactoryAware, ApplicationContextAware {

   // private static final Log log = LogFactory.getLog(ConsulDiscoveryClient.class);

    private static Logger log = LoggerFactory.getLogger(ConsulDiscoveryClient.class);

    private final ConsulClient client;

    private final ConsulDiscoveryProperties properties;

    private CompositePropertySource compositePropertySource;

    private ConfigurableEnvironment env;

    public ConsulDiscoveryClient(ConsulClient client,
                                 ConsulDiscoveryProperties properties,
                                 ConsulPropertySourceLocator consulPropertySourceLocator) {
        this.client = client;
        this.properties = properties;
        this.compositePropertySource = consulPropertySourceLocator.getComposite();
        this.env = consulPropertySourceLocator.getEnv();
    }

    @Override
    public String description() {
        return "Spring Cloud Consul Discovery Client";
    }

    @Override
    public List<ServiceInstance> getInstances(final String serviceId) {
        return getInstances(serviceId, QueryParams.DEFAULT);
    }

    public List<ServiceInstance> getInstances(final String serviceId,
                                              final QueryParams queryParams) {
        List<ServiceInstance> instances = new ArrayList<>();

        addInstancesToList(instances, serviceId, queryParams);

        return instances;
    }

    private void addInstancesToList(List<ServiceInstance> instances, String serviceId,
                                    QueryParams queryParams) {

        String aclToken = this.properties.getAclToken();
        Response<List<HealthService>> services;
        if (StringUtils.hasText(aclToken)) {
            services = this.client.getHealthServices(serviceId,
                    this.properties.getDefaultQueryTag(),
                    this.properties.isQueryPassing(), queryParams, aclToken);
        }
        else {
            services = this.client.getHealthServices(serviceId,
                    this.properties.getDefaultQueryTag(),
                    this.properties.isQueryPassing(), queryParams);
        }
        for (HealthService service : services.getValue()) {
            String host = findHost(service);

            Map<String, String> metadata = getMetadata(service);
            boolean secure = false;
            if (metadata.containsKey("secure")) {
                secure = Boolean.parseBoolean(metadata.get("secure"));
            }
            instances.add(new DefaultServiceInstance(service.getService().getId(),
                    serviceId, host, service.getService().getPort(), secure, metadata));
        }
    }

    public List<ServiceInstance> getAllInstances() {
        List<ServiceInstance> instances = new ArrayList<>();

        Response<Map<String, List<String>>> services = this.client
                .getCatalogServices(QueryParams.DEFAULT);
        for (String serviceId : services.getValue().keySet()) {
            addInstancesToList(instances, serviceId, QueryParams.DEFAULT);
        }
        return instances;
    }

    @Override
    public List<String> getServices() {
        String aclToken = this.properties.getAclToken();

        if (StringUtils.hasText(aclToken)) {
            return new ArrayList<>(
                    this.client.getCatalogServices(QueryParams.DEFAULT, aclToken)
                            .getValue().keySet());
        }
        else {
            return new ArrayList<>(this.client.getCatalogServices(QueryParams.DEFAULT)
                    .getValue().keySet());
        }
    }

    @Override
    public int getOrder() {
        return this.properties.getOrder();
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
            throw new IllegalArgumentException("BeanFactory Exception : " + beanFactory);
        }
        beanFactory.getBean(this.getClass());
        log.warn("start processConfigValue");
        // 处理配置
        processConfigValue();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }

    /**
     * Depreacted local resolver.
     */
    @Deprecated
    public interface LocalResolver {

        String getInstanceId();

        Integer getPort();

    }


    public void processConfigValue() {
        log.warn("getActiveProfile {}", getActiveProfile());
        if ("test".equals(getActiveProfile()) || "prod".equals(getActiveProfile())) {
            Collection<PropertySource<?>> propertySource = compositePropertySource.getPropertySources();
            for (PropertySource p : propertySource) {
                ConsulPropertySource consulPropertySource = (ConsulPropertySource) p;
                if (consulPropertySource.getPropertyNames().length > 0) {
                    String[] propertyNames = consulPropertySource.getPropertyNames();
                    for (String key : propertyNames) {
                        if (key.equals("spring.redis.host")) { // redis
                            consulPropertySource.setProperty(key, getInstancesIp("redis"));
                        } else if (key.equals("rocketmq.nameServer")) { // rocketmq
                            consulPropertySource.setProperty(key, getRocketmqNameServer("rocketmq"));
                        } else if (key.equals("spring.data.mongodb.uri")) { // mongodb
                            consulPropertySource.setProperty(key, consulPropertySource.getProperty(key).toString().replaceAll
                                    ("(\\d+\\.\\d+\\.\\d+\\.\\d+)\\:(\\d+)", getInstancesAddress("mongodb")));
                        } else if (key.equals("scheduler.server.addresses")) { // 任务中心
                            consulPropertySource.setProperty(key, consulPropertySource.getProperty(key).toString().replaceAll
                                    ("(\\d+\\.\\d+\\.\\d+\\.\\d+)\\:(\\d+)", getInstancesAddress("amg-scheduler-platform")));
                        } else if (consulPropertySource.getProperty(key).toString().startsWith("jdbc:mysql://")) { // mysql
                            consulPropertySource.setProperty(key, consulPropertySource.getProperty(key).toString().replaceAll
                                    ("(\\d+\\.\\d+\\.\\d+\\.\\d+)\\:(\\d+)", getInstancesAddress("mysql")));
                        }
                    }
                }
            }
        }
    }


    public String getInstancesAddress(String instancesName) {
        return getInstancesIp(instancesName) + ":" + getInstancesPort(instancesName);
    }


    public String getInstancesIp(String instancesName) {
        List<ServiceInstance> serviceInstances = this.getInstances(instancesName);
        if (CollectionUtils.isNotEmpty(serviceInstances)) {
            log.warn("server {} IP is {}", instancesName, serviceInstances.get(0).getHost());
            return serviceInstances.get(0).getHost();
        }
        return null;
    }


    public String getInstancesPort(String instancesName) {
        List<ServiceInstance> serviceInstances = this.getInstances(instancesName);
        if (CollectionUtils.isNotEmpty(serviceInstances)) {
            log.warn("server {} PORT is {}", instancesName, String.valueOf(serviceInstances.get(0).getPort()));
            return String.valueOf(serviceInstances.get(0).getPort());
        }
        return null;
    }


    public String getActiveProfile() {
        String[] profiles = env.getActiveProfiles();
        if(ArrayUtils.isNotEmpty(profiles)){
            return profiles[0];
        }
        return null;
    }


    public String getRocketmqNameServer(String name) {
        List<String> nameServerList = new ArrayList<>();
        List<ServiceInstance> serviceInstances = this.getInstances(name);
        for (ServiceInstance s : serviceInstances) {
            nameServerList.add(s.getHost() + ":" + s.getPort());
        }
        if (CollectionUtils.isNotEmpty(nameServerList)) {
            return nameServerList.stream().collect(Collectors.joining(";"));
        }
        return null;
    }


//    public String getProdString(String name) {
//        if ("prod".equals(getActiveProfile()) && !"amg-scheduler-platform".equals(name)) {
//            return name + "-prod";
//        }
//        return name;
//    }

}

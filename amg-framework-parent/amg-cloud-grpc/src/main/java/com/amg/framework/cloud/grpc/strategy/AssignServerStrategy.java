package com.amg.framework.cloud.grpc.strategy;

import com.amg.framework.boot.utils.regex.RegexUtils;
import com.amg.framework.boot.utils.spring.SpringContextUtil;
import com.amg.framework.cloud.grpc.config.AssignConfig;
import io.grpc.LoadBalancer;
import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import java.util.*;


@Slf4j
public abstract class AssignServerStrategy {

    public abstract AssignServerStrategy biuldStrategy(LoadBalancer.PickSubchannelArgs args, Object obj);

    public abstract Set<String> getVersions(LoadBalancer.PickSubchannelArgs args, String serverName);

    public List<LoadBalancer.Subchannel> getServiceSubchannel(List<LoadBalancer.Subchannel> list, Set<String> address) {
        List<LoadBalancer.Subchannel> serverList = new ArrayList();
        Set<LoadBalancer.Subchannel> ipSet = new HashSet<>();
        if (CollectionUtils.isNotEmpty(address) && CollectionUtils.isNotEmpty(list)) {
            for (String ip : address) {
                for (LoadBalancer.Subchannel sub : list) {
                    String addresses = sub.getAddresses().getAddresses().get(0).toString().replaceAll("\\/", "");
                    if (ip.equals(addresses)) {
                        ipSet.add(sub);
                    }
                }
            }
            serverList.addAll(new ArrayList<>(ipSet));
        }
        return serverList;
    }

    public List<ServiceInstance> getServiceInstances(String serverName) {
        DiscoveryClient discoveryClient = SpringContextUtil.getBean(DiscoveryClient.class);
        return discoveryClient.getInstances(serverName);
    }

    public AssignConfig getAssignConfig() {
        AssignConfig assignConfig = SpringContextUtil.getBean(AssignConfig.class);
        return assignConfig;
    }

    public Set<String> getAssignInstanceAddress(Set<String> versions, String serverName) {
        Set<String> set = new HashSet<>();
        List<ServiceInstance> serviceInstances = getServiceInstances(serverName);
        try {
            for (String v : versions) {
                if (RegexUtils.matcherVersionNo(v)) {
                    for (ServiceInstance serviceInstance : serviceInstances) {
                        Set<String> keys = serviceInstance.getMetadata().keySet();
                        for (String key : keys) {
                            if (key.equals(v)) {
                                set.add(serviceInstance.getHost() + ":" + (StringUtils.isBlank(serviceInstance.getMetadata().get("gRPC.port"))
                                        ? serviceInstance.getPort()
                                        : serviceInstance.getMetadata().get("gRPC.port")));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("getAssignInstanceAddress error", e);
        }
        return set;
    }

    public static AssignServerStrategy chooseStrategy(AssignConfig assignConfig, LoadBalancer.PickSubchannelArgs args, String serverName) {
        AssignServerStrategy assignServerStrategy = null;
        Map map = assignConfig.getCustomStrategy();
        String defaultStrategy = assignConfig.getDefaultStrategy();
        if (MapUtils.isNotEmpty(map)) {
            Map customStrategy = (Map) map.get(serverName);
            if (MapUtils.isNotEmpty(customStrategy)) {
                if (customStrategy.get("ipStrategy") != null) {
                    assignServerStrategy = SpringContextUtil.getBean(IpStrategy.class).biuldStrategy(args, customStrategy.get("ipStrategy"));
                }
                if (assignServerStrategy == null) {
                    if (customStrategy.get("headerStrategy") != null) {
                        assignServerStrategy = SpringContextUtil.getBean(HeaderStrategy.class).biuldStrategy(args, customStrategy.get("headerStrategy"));
                    }
                }
                if (assignServerStrategy == null) {
                    if (customStrategy.get("rpcMethodStrategy") != null) {
                        assignServerStrategy = SpringContextUtil.getBean(RpcMethodStrategy.class).biuldStrategy(args, customStrategy.get("rpcMethodStrategy"));
                    }
                }
                if (assignServerStrategy == null) {
                    if (customStrategy.get("assignVersionStrategy") != null) {
                        assignServerStrategy = SpringContextUtil.getBean(AssignVersionStrategy.class).biuldStrategy(args, customStrategy.get("assignVersionStrategy"));
                    }
                }
            }
        }
        if (assignServerStrategy == null) {
            assignServerStrategy = ((AssignServerStrategy) SpringContextUtil.getBean(defaultStrategy)).biuldStrategy(args, null);
        }
        return assignServerStrategy;
    }


    public LoadBalancer.PickResult resolve(String version, boolean blank, String serverName) {
        if (blank) {
            return LoadBalancer.PickResult.withError(Status.UNAVAILABLE.withDescription("No servers found for "
                    + serverName + " " + version));
        }
        return null;
    }

}

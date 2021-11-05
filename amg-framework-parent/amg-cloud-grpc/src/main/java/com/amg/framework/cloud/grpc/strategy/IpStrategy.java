package com.amg.framework.cloud.grpc.strategy;

import com.amg.framework.boot.utils.regex.RegexUtils;
import io.grpc.LoadBalancer;
import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Component;
import java.util.*;


@Slf4j
@Component
public class IpStrategy extends AssignServerStrategy {

    @Override
    public Set<String> getVersions(LoadBalancer.PickSubchannelArgs args, String serverName) {
        Map map = getAssignConfig().getCustomStrategy();
        Set<String> set = new HashSet<>();
        if (MapUtils.isNotEmpty(map)) {
            Map customStrategy = (Map) map.get(serverName);
            if (MapUtils.isNotEmpty(customStrategy)) {
                if (customStrategy.get("ipStrategy") != null) {
                    List<String> iplist = new ArrayList(((Map) customStrategy.get("ipStrategy")).values());
                    if (CollectionUtils.isNotEmpty(iplist)) {
                        for (String ip : iplist) {
                            if (RegexUtils.matcherIpPort(ip)) {
                                set.add(ip);
                            }
                        }
                        return set;
                    }
                }
            }
        }
        return set;
    }

    @Override
    public AssignServerStrategy biuldStrategy(LoadBalancer.PickSubchannelArgs args, Object obj) {
        List<String> iplist = new ArrayList(((Map) obj).values());
        if (CollectionUtils.isNotEmpty(iplist)) {
            for (String ip : iplist) {
                if (!RegexUtils.matcherIpPort(ip)) {
                    return null;
                }
            }
            return this;
        }
        return null;
    }

    public Set<String> getAssignInstanceAddress(Set<String> ips, String serverName) {
        Set<String> set = new HashSet<>();
        List<ServiceInstance> serviceInstances = getServiceInstances(serverName);
        try {
            if (CollectionUtils.isNotEmpty(ips)) {
                for (String ip : ips) {
                    if (RegexUtils.matcherIpPort(ip)) {
                        for (ServiceInstance serviceInstance : serviceInstances) {
                            String ipAddress = serviceInstance.getHost() + ":" + (StringUtils.isBlank(serviceInstance.getMetadata().get("gRPC.port"))
                                    ? serviceInstance.getPort()
                                    : serviceInstance.getMetadata().get("gRPC.port"));
                            if (ip.equals(ipAddress)) {
                                set.add(ipAddress);
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

    public LoadBalancer.PickResult resolve(String ips, boolean blank, String serverName) {
        if (blank) {
            return LoadBalancer.PickResult.withError(Status.UNAVAILABLE.withDescription("No servers found for "
                    + serverName + " " + ips));
        }
        return null;
    }

}

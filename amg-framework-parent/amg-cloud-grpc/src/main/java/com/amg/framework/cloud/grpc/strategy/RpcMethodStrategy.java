package com.amg.framework.cloud.grpc.strategy;

import io.grpc.LoadBalancer;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Component
public class RpcMethodStrategy extends AssignServerStrategy {

    @Override
    public Set<String> getVersions(LoadBalancer.PickSubchannelArgs args, String serverName) {
        Map map = getAssignConfig().getCustomStrategy();
        Set<String> set = new HashSet<>();
        if (MapUtils.isNotEmpty(map)) {
            Map customStrategy = (Map) map.get(serverName);
            if (MapUtils.isNotEmpty(customStrategy)) {
                if (customStrategy.get("rpcMethodStrategy") != null) {
                    Map<String, String> m = (Map<String, String>) customStrategy.get("rpcMethodStrategy");
                    if (MapUtils.isNotEmpty(m)) {
                        String rpcMethod = args.getMethodDescriptor().getFullMethodName();
                        if (StringUtils.isNotBlank(m.get(rpcMethod))) {
                            set.add(m.get(rpcMethod));
                            if (CollectionUtils.isNotEmpty(set)) {
                                return set;
                            }
                        }
                    }
                }
            }
        }
        return set;
    }


    @Override
    public AssignServerStrategy biuldStrategy(LoadBalancer.PickSubchannelArgs args, Object obj) {
        Map<String, String> map = (Map) obj;
        if (MapUtils.isNotEmpty(map)) {
            String rpcMethod = args.getMethodDescriptor().getFullMethodName();
            if (map.get(rpcMethod) != null) {
                return this;
            }
        }
        return null;
    }


}

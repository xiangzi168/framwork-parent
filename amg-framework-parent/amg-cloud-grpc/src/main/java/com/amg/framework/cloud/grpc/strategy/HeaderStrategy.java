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
public class HeaderStrategy extends AssignServerStrategy {

    @Override
    public AssignServerStrategy biuldStrategy(LoadBalancer.PickSubchannelArgs args, Object obj) {
        Map<String, String> map = (Map) obj;
        Set<String> header = args.getHeaders().keys();
        if (MapUtils.isNotEmpty(map) && CollectionUtils.isNotEmpty(header)) {
            for (String h : header) {
                if (map.get(h) != null) {
                    return this;
                }
            }
        }
        return null;
    }

    @Override
    public Set<String> getVersions(LoadBalancer.PickSubchannelArgs args, String serverName) {
        Map map = getAssignConfig().getCustomStrategy();
        Set<String> set = new HashSet<>();
        if (MapUtils.isNotEmpty(map)) {
            Map customStrategy = (Map) map.get(serverName);
            if (MapUtils.isNotEmpty(customStrategy)) {
                if (customStrategy.get("headerStrategy") != null) {
                    Set<String> header = args.getHeaders().keys();
                    Map<String, String> m = (Map<String, String>) customStrategy.get("headerStrategy");
                    if (MapUtils.isNotEmpty(m) && CollectionUtils.isNotEmpty(header)) {
                        for (String h : header) {
                            if (StringUtils.isNotBlank(m.get(h))) {
                                set.add(m.get(h));
                            }
                        }
                        if (CollectionUtils.isNotEmpty(set)) {
                            return set;
                        }
                    }
                }
            }
        }
        return set;
    }

}

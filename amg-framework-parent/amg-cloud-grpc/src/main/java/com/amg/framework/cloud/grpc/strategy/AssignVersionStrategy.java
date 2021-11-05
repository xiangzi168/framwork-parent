package com.amg.framework.cloud.grpc.strategy;

import com.amg.framework.boot.utils.regex.RegexUtils;
import io.grpc.LoadBalancer;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;
import java.util.*;


@Component
public class AssignVersionStrategy extends AssignServerStrategy {

    @Override
    public Set<String> getVersions(LoadBalancer.PickSubchannelArgs args, String serverName) {
        Map map = getAssignConfig().getCustomStrategy();
        Set<String> set = new HashSet<>();
        if (MapUtils.isNotEmpty(map)) {
            Map customStrategy = (Map) map.get(serverName);
            if (MapUtils.isNotEmpty(customStrategy)) {
                if (customStrategy.get("assignVersionStrategy") != null) {
                    List<String> vlist = new ArrayList(((Map) customStrategy.get("assignVersionStrategy")).values());
                    if (CollectionUtils.isNotEmpty(vlist)) {
                        for (String v : vlist) {
                            if (RegexUtils.matcherVersionNo(v)) {
                                set.add(v);
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
        List<String> vlist = new ArrayList(((Map) obj).values());
        if (CollectionUtils.isNotEmpty(vlist)) {
            for (String v : vlist) {
                if (!RegexUtils.matcherVersionNo(v)) {
                    return null;
                }
            }
            return this;
        }
        return null;
    }

}

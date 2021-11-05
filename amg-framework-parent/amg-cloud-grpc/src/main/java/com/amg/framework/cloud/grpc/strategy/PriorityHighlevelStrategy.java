package com.amg.framework.cloud.grpc.strategy;

import com.amg.framework.boot.utils.hash.MapUtils;
import com.amg.framework.boot.utils.regex.RegexUtils;
import io.grpc.LoadBalancer;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Component;
import java.util.*;


@Component
public class PriorityHighlevelStrategy extends AssignServerStrategy {

    @Override
    public AssignServerStrategy biuldStrategy(LoadBalancer.PickSubchannelArgs args, Object obj) {
        return this;
    }

    @Override
    public Set<String> getVersions(LoadBalancer.PickSubchannelArgs args, String serverName) {
        Set<String> set = new HashSet<>();
        Map<Integer, String> map = new TreeMap();
        for (ServiceInstance serviceInstance : getServiceInstances(serverName)) {
            Set<String> keys = serviceInstance.getMetadata().keySet();
            for (String key : keys) {
                // 判断是否有版本号
                if (RegexUtils.matcherVersionNo(key)) {
                    String k = key.replaceAll("v", "").replaceAll("\\.", "");
                    map.put(Integer.valueOf(k), key);
                }
            }
        }
        if (org.apache.commons.collections4.MapUtils.isNotEmpty(map)) {
            Map<Integer, String> resultMap = MapUtils.sortMapByKey(map);
            set.add(resultMap.get(new ArrayList(resultMap.keySet()).get(0)));
        }
        return set;
    }

    public List<LoadBalancer.Subchannel> getServiceSubchannel(List<LoadBalancer.Subchannel> list, Set<String> address) {
        return CollectionUtils.isEmpty(address) ? list : super.getServiceSubchannel(list, address);
    }

}

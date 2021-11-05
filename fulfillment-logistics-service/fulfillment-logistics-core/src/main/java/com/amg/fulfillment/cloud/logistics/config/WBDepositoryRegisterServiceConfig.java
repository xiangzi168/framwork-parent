package com.amg.fulfillment.cloud.logistics.config;

import com.alibaba.fastjson.JSON;
import com.amg.framework.boot.utils.spring.SpringContextUtil;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsProviderDO;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsProviderMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class WBDepositoryRegisterServiceConfig {

    @Autowired
    private LogisticsProviderMapper logisticsProviderMapper;

    public static List<LogisticsProvider> providers = new ArrayList<>();

    private static LogisticsProvider DEFAULTPROVIDER = new LogisticsProvider();
    @PostConstruct
    public void queryDataBase() {
        List<LogisticsProviderDO> providerDOList = logisticsProviderMapper.selectList(null);
        if (providerDOList.isEmpty()) {
            throw new RuntimeException("加载物流提供表失败！！！！！");
        }
        List<LogisticsProvider> searchProviders = providerDOList.stream().map(item -> {
            LogisticsProvider provider = new LogisticsProvider();
            provider.setLogisticName(item.getLogisticsName());
            provider.setLogisticCode(item.getLogisticsCode());
            provider.setServiceId(item.getDepositoryRegisterCode());
            return provider;
        }).collect(Collectors.toList());
        providers.addAll(searchProviders);
        log.info("加载物流提供表数据成功，加载数据内容是：{}", JSON.toJSONString(providers));
    }


    public static String getServicerIdFromCode(String code, String channelCode) {
        String servicerIdFromCode = getServicerIdByCode(code);
        if (SpringContextUtil.getActiveProfile().equals("prod")) {
            return servicerIdFromCode + channelCode;
        } else {
            return servicerIdFromCode;
        }
    }

    private static String getServicerIdByCode(String code) {
        if (providers.isEmpty()) {
            throw new RuntimeException("加载物流提供表失败！！！！！");
        }
        return providers.stream().filter(item -> item.getLogisticCode().equals(code))
                .findFirst().orElse(DEFAULTPROVIDER).getServiceId();
    }

    @Data
    public static class LogisticsProvider {
        private String logisticCode;
        private String logisticName;
        private String serviceId;
    }


}

package com.amg.fulfillment.cloud.logistics.mq.consumer;

import com.amg.fulfillment.cloud.logistics.entity.LogisticsPackageDO;
import com.amg.fulfillment.cloud.logistics.service.IDeliveryPackageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Seraph on 2021/6/1
 */

@Slf4j
@Component
@RocketMQMessageListener(topic = "${mq.logistic.logistic-unfinish-package.topic}",
        selectorExpression = "*",
        consumerGroup = "logistic-unfinish-package",consumeThreadMax = 5)
public class UnfinishedDeliveryPackageConsumer implements RocketMQListener<List<LogisticsPackageDO>> {

    @Autowired
    private IDeliveryPackageService deliveryPackageService;

    @Override
    public void onMessage(List<LogisticsPackageDO> logisticsPackageDOList) {
        deliveryPackageService.refreshDeliveryPackage(logisticsPackageDOList);
    }
}

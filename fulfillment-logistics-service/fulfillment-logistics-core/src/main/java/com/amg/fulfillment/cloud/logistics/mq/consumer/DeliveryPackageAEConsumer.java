package com.amg.fulfillment.cloud.logistics.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.amg.fulfillment.cloud.logistics.api.dto.msg.DeliveryPackageMsgDto;
import com.amg.fulfillment.cloud.logistics.dto.logistic.DeliveryPackageDto;
import com.amg.fulfillment.cloud.logistics.service.IDeliveryPackageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seraph on 2021/5/21
 */

@Slf4j
@Component
//@RocketMQMessageListener(topic = "${mq.order.order-aePlaceOrderSuccess-logistics.topic}",
//        selectorExpression = "*",
//        consumerGroup = "order-aePlaceOrderSuccess-logistics")
public class DeliveryPackageAEConsumer implements RocketMQListener<String> {

    @Autowired
    private IDeliveryPackageService deliveryPackageService;

    @Override
    public void onMessage(String message) {
        log.info("接受到 AE 发货包裹单物流 消息：{}", message);
        this.processMsg(message);
    }

    private void processMsg(String message)
    {
        List<DeliveryPackageDto.DeliveryPackageItemDto> deliveryPackageItemDtoList = new ArrayList<>();
        List<DeliveryPackageMsgDto> deliveryPackageMsgDtoList = JSON.parseArray(message, DeliveryPackageMsgDto.class);
        deliveryPackageMsgDtoList.forEach(deliveryPackageMsgDto -> {
            List<DeliveryPackageMsgDto.DeliveryPackagelogisticsDto> deliveryPackagelogisticsDtoList = deliveryPackageMsgDto.getLogisticsList();
            deliveryPackagelogisticsDtoList.forEach(deliveryPackagelogisticsDto -> {
                DeliveryPackageDto.DeliveryPackageItemDto deliveryPackageItemDto = new DeliveryPackageDto.DeliveryPackageItemDto();
                BeanUtils.copyProperties(deliveryPackageMsgDto, deliveryPackageItemDto);

                deliveryPackageItemDto.setLogisticsCode(deliveryPackagelogisticsDto.getLogisticsCode());
                deliveryPackageItemDto.setLogisticsTrackingCode(deliveryPackagelogisticsDto.getLogisticsTrackingCode());
                deliveryPackageItemDto.setLogisticsChannelStatus(deliveryPackagelogisticsDto.getLogisticsStatus());
                deliveryPackageItemDtoList.add(deliveryPackageItemDto);
            });
        });

        DeliveryPackageDto deliveryPackageDto = new DeliveryPackageDto();
        deliveryPackageDto.setList(deliveryPackageItemDtoList);
        deliveryPackageService.updateAeDeliveryPackage(deliveryPackageDto);
    }

}

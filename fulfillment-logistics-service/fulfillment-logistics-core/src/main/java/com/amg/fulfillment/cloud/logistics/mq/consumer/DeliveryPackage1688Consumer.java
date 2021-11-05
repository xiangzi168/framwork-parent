package com.amg.fulfillment.cloud.logistics.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amg.fulfillment.cloud.logistics.dto.logistic.LogisticOrderDto;
import com.amg.fulfillment.cloud.logistics.service.IDeliveryProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seraph on 2021/5/22
 */

@Slf4j
@Component
@RocketMQMessageListener(topic = "${mq.order.order-delivery-logistics.topic}",
        selectorExpression = "*",
        consumerGroup = "order-delivery-logistics")
public class DeliveryPackage1688Consumer implements RocketMQListener<String> {

    @Autowired
    private IDeliveryProductService deliveryProductService;

    @Override
    public void onMessage(String message) {
        log.info("接受到 1688 自动发货包裹单物流 消息：{}", message);
        this.processMessage(message);
    }

    private void processMessage(String message)
    {
        List<LogisticOrderDto> logisticOrderDtoList = new ArrayList<>();
        JSONArray jsonArray = JSON.parseArray(message);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            logisticOrderDtoList.add(JSON.toJavaObject(jsonObject, LogisticOrderDto.class));
        }
        //自动发货功能
        deliveryProductService.autoOutDepository(logisticOrderDtoList);
    }
}

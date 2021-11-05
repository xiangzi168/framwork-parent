package com.amg.fulfillment.cloud.logistics.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.amg.fulfillment.cloud.logistics.api.dto.msg.DepositoryProcessMsgDto;
import com.amg.fulfillment.cloud.logistics.service.IDeliveryProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Seraph on 2021/5/22
 */

@Slf4j
@Component
@RocketMQMessageListener(topic = "${mq.logistic.logistic-depository-third-notice.topic}",
        selectorExpression = "${mq.logistic.logistic-depository-third-notice.tag1}",
        consumerGroup = "${mq.logistic.logistic-depository-third-notice.topic}")
public class DepositoryDeliveryResultConsumer implements RocketMQListener<String> {

    @Autowired
    private IDeliveryProductService deliveryProductService;

    @Override
    public void onMessage(String message) {
        log.info("接受到 仓库出库结果 消息：{}", message);
        this.processMsg(message);
    }

    private void processMsg(String message)
    {
        DepositoryProcessMsgDto depositoryProcessMsgDto = JSON.toJavaObject(JSON.parseObject(message), DepositoryProcessMsgDto.class);
        deliveryProductService.depositoryDeliveryResult(depositoryProcessMsgDto);
    }
}

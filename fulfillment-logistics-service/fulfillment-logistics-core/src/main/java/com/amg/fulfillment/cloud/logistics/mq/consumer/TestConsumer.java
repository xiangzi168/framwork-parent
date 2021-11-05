package com.amg.fulfillment.cloud.logistics.mq.consumer;


import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 消息监听 selectorExpression为tag 默认为 *
 */
//@Component
//@RocketMQlogisticsListener(topic = "test_topic", selectorExpression="*", consumerGroup = "test_group")
public class TestConsumer implements RocketMQListener<MessageExt> {

    @Override
    public void onMessage(MessageExt logistics) {
        byte[] body = logistics.getBody();
        String msg = new String(body);
        System.out.println(msg);
    }

}

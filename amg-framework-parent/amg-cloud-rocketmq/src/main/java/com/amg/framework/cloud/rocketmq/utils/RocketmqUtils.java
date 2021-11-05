package com.amg.framework.cloud.rocketmq.utils;

import com.amg.framework.boot.utils.string.PlaceholderResolverUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;


/**
 * Rocketmq 操作工具类
 */
@Component
public class RocketmqUtils {

    private static Logger logger = LoggerFactory.getLogger(RocketmqUtils.class);

    @Autowired
    private RocketMQTemplate rocketMQTemplate;


    /**
     * 发送单向消息
     * @param topic
     * @param obj
     * @return
     */
    public void sendOneWay(String topic, Object obj) {
        rocketMQTemplate.sendOneWay(placeholderResolver(topic), obj);
    }


    /**
     * 发送同步消息
     * @param topic
     * @param obj
     * @return
     */
    public SendResult syncSend(String topic, Object obj) {
        return rocketMQTemplate.syncSend(placeholderResolver(topic), obj);
    }


    /**
     * 发送同步有序消息
     * @param topic
     * @param obj
     * @return
     */
    public SendResult syncSendOrderly(String topic, Object obj, String hashKey) {
        return rocketMQTemplate.syncSendOrderly(placeholderResolver(topic), obj, hashKey);
    }


    /**
     * 发送异步消息
     * @param topic
     * @param obj
     * @param sendCallback
     * @return
     */
    public void asyncSend(String topic, Object obj, SendCallback sendCallback) {
        rocketMQTemplate.asyncSend(placeholderResolver(topic), obj, sendCallback);
    }


    /**
     * 发送异步有序消息
     * @param topic
     * @param obj
     * @param sendCallback
     * @return
     */
    public void asyncSendOrderly(String topic, Object obj, String hashKey, SendCallback sendCallback) {
        rocketMQTemplate.asyncSendOrderly(placeholderResolver(topic), obj, hashKey, sendCallback);
    }


    /**
     * 发送同步延时消息
     * @param topic
     * @param obj
     * @param timeout
     * 延时级别：1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
     * @return
     */
    public SendResult syncSendDelay(String topic, Object obj, long timeout, int delayLevel) {
        return rocketMQTemplate.syncSend(placeholderResolver(topic), MessageBuilder.withPayload(obj).build(), timeout, delayLevel);
    }

    /**
     * 发送异步延时消息
     * @param topic
     * @param obj
     * @param timeout
     * 延时级别：1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
     * @return
     */
    public void asyncSendDelay(String topic, Object obj,SendCallback sendCallback, long timeout, int delayLevel) {
        rocketMQTemplate.asyncSend(placeholderResolver(topic),MessageBuilder.withPayload(obj).build(),sendCallback,timeout,delayLevel);
    }


    /**
     * 解析占位符
     * @param topic
     * @return
     */
    public String placeholderResolver(String topic) {
        String str1 = "";
        String str2 = "";
        if (topic.contains(":")) {
            str1 = PlaceholderResolverUtils.replacePlaceholders(topic.split(":", 2)[0]);
            str2 = PlaceholderResolverUtils.replacePlaceholders(topic.split(":", 2)[1]);
        } else {
            str1 = PlaceholderResolverUtils.replacePlaceholders(topic);
        }
        return str1 + (StringUtils.isBlank(str2) ? "" : ":" + str2);
    }

}

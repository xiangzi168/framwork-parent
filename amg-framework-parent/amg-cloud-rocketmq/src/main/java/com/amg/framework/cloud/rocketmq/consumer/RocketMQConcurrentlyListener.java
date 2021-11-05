package com.amg.framework.cloud.rocketmq.consumer;

import com.amg.framework.boot.utils.spring.SpringContextUtil;
import com.amg.framework.boot.utils.string.PlaceholderResolverUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import javax.annotation.PostConstruct;


/**
 * 无序并行消费监听
 * MessageListenerOrderly 有序
 * MessageListenerConcurrently 无序，并行处理
 */
public abstract class RocketMQConcurrentlyListener implements MessageListenerConcurrently {

    private String topic;
    private String tag;
    private String consumerGroup;
    private int consumeMessageBatchSize = 20; // 默认批处理消费20条
    private int consumeMessageBatchMaxSize = 5000; // 最多批处理消费5000条
    private String consumerGroupSuffix = "-" + SpringContextUtil.appName.split("\\-")[1] + "Group";
    private DefaultMQPushConsumer consumer;

    public RocketMQConcurrentlyListener(String topic) {
        this(topic, "*", null);
    }

    public RocketMQConcurrentlyListener(String topic, String consumerGroup, int consumeMessageBatchSize) {
        this(topic, "*", consumerGroup, consumeMessageBatchSize);
    }

    public RocketMQConcurrentlyListener(String topic, int consumeMessageBatchSize) {
        this(topic, "*", null, consumeMessageBatchSize);
    }

    public RocketMQConcurrentlyListener(String topic, String consumerGroup) {
        this(topic, "*", consumerGroup);
    }

    public RocketMQConcurrentlyListener(String topic, String tag, String consumerGroup) {
        this.topic = topic;
        this.tag = tag;
        this.consumerGroup = consumerGroup;
    }

    public RocketMQConcurrentlyListener(String topic, String tag, String consumerGroup, int consumeMessageBatchSize) {
        this.topic = topic;
        this.tag = tag;
        this.consumerGroup = consumerGroup;
        this.consumeMessageBatchSize = consumeMessageBatchSize;
    }

    @PostConstruct
    public void start() throws MQClientException {
        this.topic = PlaceholderResolverUtils.replacePlaceholders(this.topic);
        this.tag = PlaceholderResolverUtils.replacePlaceholders(this.tag);
        if (StringUtils.isBlank(this.topic) || StringUtils.isBlank(this.tag)) {
            throw new IllegalArgumentException("topic and tag cannot be empty");
        }
        if (StringUtils.isBlank(this.consumerGroup)) {
            this.consumerGroup = (this.topic + "-" + this.tag + this.consumerGroupSuffix).replaceAll("\\-\\*", "");
        } else {
            this.consumerGroup += this.consumerGroupSuffix;
        }
        consumer = new DefaultMQPushConsumer(this.consumerGroup);
        consumer.subscribe(this.topic, this.tag);
        consumer.setConsumeMessageBatchMaxSize((this.consumeMessageBatchSize =
                (this.consumeMessageBatchSize < 0 ? 1 : this.consumeMessageBatchSize)) > this.consumeMessageBatchMaxSize
                ? this.consumeMessageBatchMaxSize : this.consumeMessageBatchSize);
        consumer.setPullBatchSize(this.consumeMessageBatchSize > consumer.getPullBatchSize()
                ? consumer.getConsumeMessageBatchMaxSize() : consumer.getPullBatchSize());
        // CONSUME_FROM_LAST_OFFSET 默认策略，从队列尾部往头部消费
        // CONSUME_FROM_FIRST_OFFSET 从队列头部往尾部消费
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        consumer.setConsumeThreadMin(Runtime.getRuntime().availableProcessors());
        consumer.setConsumeThreadMax(Runtime.getRuntime().availableProcessors());
        consumer.registerMessageListener(SpringContextUtil.getBean(this.getClass()));
        consumer.start();
    }

}

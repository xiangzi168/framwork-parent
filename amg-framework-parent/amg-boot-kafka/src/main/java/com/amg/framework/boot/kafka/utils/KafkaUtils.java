package com.amg.framework.boot.kafka.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * kafka 工具类
 */
@Component
public class KafkaUtils<K, V> {

    private final static Logger LOGGER = LoggerFactory.getLogger(KafkaUtils.class);

    @Autowired
    private KafkaTemplate<K, V> kafkaTemplate;


    /**
     * 发送消息
     * 发后即忘，它只管往Kafka中发送消息而并不关心消息是否正确到达,这种发送方式的性能最高，可靠性也最差。
     * @param topic
     * @param data
     * @return
     */
    public void send(String topic, @Nullable V data) {
        kafkaTemplate.send(topic, data);
    }

    /**
     * 发送消息带key,key是用来指定消息的键,同一个key的消息会被划分到同一个分区中
     * 发后即忘，它只管往Kafka中发送消息而并不关心消息是否正确到达,这种发送方式的性能最高，可靠性也最差。
     * @param topic
     * @param key
     * @param data
     */
    public void send(String topic, K key, @Nullable V data) {
        kafkaTemplate.send(topic, key, data);
    }

    /**
     * 发送消息制定分区
     * 发后即忘，它只管往Kafka中发送消息而并不关心消息是否正确到达,这种发送方式的性能最高，可靠性也最差。
     * @param topic
     * @param partition
     * @param key
     * @param data
     */
    public void send(String topic, Integer partition, K key, @Nullable V data) {
        kafkaTemplate.send(topic, partition, key, data);
    }

    /**
     * 同步发送
     * @param topic
     * @param data
     * @return
     */
    public boolean sendSync(String topic, @Nullable V data) {
        try {
            kafkaTemplate.send(topic, data).get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("kafka send sync msg error", e);
            return false;
        }
        return true;
    }

    /**
     * 同步发送带key
     * @param topic
     * @param key
     * @param data
     * @return
     */
    public boolean sendSync(String topic, K key, @Nullable V data) {
        try {
            kafkaTemplate.send(topic, key, data).get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("kafka send sync msg error", e);
            return false;
        }
        return true;
    }

    /**
     * 同步发送带分区
     * @param topic
     * @param partition
     * @param key
     * @param data
     * @return
     */
    public boolean sendSync(String topic, Integer partition, K key, @Nullable V data) {
        try {
            kafkaTemplate.send(topic, partition, key, data).get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("kafka send sync msg error", e);
            return false;
        }
        return true;
    }

    /**
     * 同步发送并获取发送结果
     * @param topic
     * @param data
     * @return
     */
    public SendResult<K, V> sendWithResult(String topic, @Nullable V data) {
        ListenableFuture<SendResult<K, V>> future = kafkaTemplate.send(topic, data);
        SendResult<K, V> sendResult = null;
        try {
            sendResult = future.get();
            return sendResult;
        } catch (InterruptedException |ExecutionException e) {
            LOGGER.error("kafka send with result msg error", e);
        }
        return sendResult;
    }

    /**
     * 发送带超时时间
     * @param topic
     * @param data
     * @param timeout
     * @param timeUnit
     * @return
     */
    public boolean sendSync(String topic, @Nullable V data, long timeout, TimeUnit timeUnit) {
        try {
            kafkaTemplate.send(topic, data).get(timeout, timeUnit);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            LOGGER.error("kafka send sync msg error", e);
            return false;
        }
        return true;
    }

    /**
     * 异步发送
     * @param topic
     * @param data
     * @param callback
     */
    public void sendAsync(String topic, @Nullable V data, ListenableFutureCallback<SendResult<K, V>> callback) {
        ListenableFuture<SendResult<K, V>> future = kafkaTemplate.send(topic, data);
        future.addCallback(callback);
    }


}

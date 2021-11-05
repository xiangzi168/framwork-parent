package com.amg.fulfillment.cloud.logistics.mq.consumer;

import com.alibaba.fastjson.JSONObject;
import com.amg.fulfillment.cloud.logistics.model.bo.OrderLogisticsTracingModel;
import com.amg.fulfillment.cloud.logistics.service.impl.LogisticsStatusServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName LogisticsStateChangesFor1688Consumer
 * @Description 对接1688平台  物流状态变更消息
 * @Author qh
 * @Date 2021/7/27 15:16
 **/
@Slf4j
@Component
@RocketMQMessageListener(topic = "${mq.message.message-1688logisticsBuyerViewTrace-logistics.topic}",
        selectorExpression = "*")
public class LogisticsStateChangesFor1688Consumer implements RocketMQListener<String> {
    @Autowired
    LogisticsStatusServiceImpl logisticsStatusService;

    @Override
    public void onMessage(String s) {
       //消费消息
        log.info("物流状态监听，接受到物流状态信息" + s);
        OrderLogisticsTracingModel orderLogisticsTracingModel = null;
        try {
            JSONObject jsonObject = JSONObject.parseObject(s);
            JSONObject data = jsonObject.getJSONObject("data");
            JSONObject orderLogisticsTracingModel1 = data.getJSONObject("OrderLogisticsTracingModel");
            orderLogisticsTracingModel = orderLogisticsTracingModel1.toJavaObject(OrderLogisticsTracingModel.class);
        } catch (Exception e) {
            //JSON格式异常
            log.error("物流状态监听，接受到异常参数信息：参数中JSON对象为" + e.getMessage() + s);
        }
        try {
            logisticsStatusService.saveOrUpdateBasedOnMailNo(orderLogisticsTracingModel);
        } catch (Exception e) {
            //JSON格式异常
            log.error("物流状态监听，入库更新异常" + e.getMessage() + s);
        }
    }
}

package com.amg.fulfillment.cloud.logistics.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Tom
 * @date 2021-06-10-16:22
 */
@Slf4j
@Component
//@RocketMQMessageListener(topic = "${mq.message.message-1688channelOrderPartSendGoods-purchase.topic}",
//        selectorExpression = "*",
//        consumerGroup = "${mq.message.message-1688channelOrderPartSendGoods-purchase.topic}")
public class Purchase1688PartNoticeDepositoryConsumer /*implements RocketMQListener<String>*/ {

    @Autowired
    Purchase1688NoticeDepositoryConsumer purchase1688NoticeDepositoryConsumer;

    /**
     * 1688订单部分发货
     * {
     *   "orderId": 167539019420540000,
     *   "currentStatus": "waitbuyerreceive",
     *   "msgSendTime": "2018-05-30 19: 34: 27",
     *   "buyerMemberId": "b2b-665170100",
     *   "sellerMemberId": "b2b-1676547900b7bb3"
     * }
     */
/*    @Override
    public void onMessage(String message) {
        log.info("监听到1688部分发货消息：{}", message);
        JSONObject jsonObject = JSON.parseObject(message);
        jsonObject.put("currentStatus","waitbuyerreceive");
        purchase1688NoticeDepositoryConsumer.purchase(JSON.toJSONString(jsonObject));
    }*/
}

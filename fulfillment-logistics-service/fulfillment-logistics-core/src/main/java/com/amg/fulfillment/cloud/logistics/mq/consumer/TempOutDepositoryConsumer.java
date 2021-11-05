package com.amg.fulfillment.cloud.logistics.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.amg.framework.cloud.grpc.utils.GrpcJsonFormatUtils;
import com.amg.fulfillment.cloud.logistics.api.dto.depository.Size;
import com.amg.fulfillment.cloud.logistics.api.dto.msg.DepositoryProcessMsgDto;
import com.amg.fulfillment.cloud.logistics.api.proto.WarehouseOuterClass;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * 平台c出库通知
 */
@Slf4j
@Component
//@RocketMQMessageListener(topic = "EventWarehouseCheckout")
public class TempOutDepositoryConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private DepositoryDeliveryResultConsumer depositoryDeliveryResultConsumer;

    @Override
    public void onMessage(MessageExt message) {
        try {
            WarehouseOuterClass.EventWarehouseCheckout eventWarehouseCheckout = WarehouseOuterClass.EventWarehouseCheckout.parseFrom(message.getBody());
            log.info("监听到平台推送的货物出库消息：{}", GrpcJsonFormatUtils.toJsonString(eventWarehouseCheckout));
            process(eventWarehouseCheckout);
        } catch (InvalidProtocolBufferException e) {
            log.error("监听到平台推送的货物出库消息错误：{}", message);
        }
    }

    private void process(WarehouseOuterClass.EventWarehouseCheckout eventWarehouseCheckout) {
        DepositoryProcessMsgDto msgDto = new DepositoryProcessMsgDto();
        msgDto.setShipped(true);
        msgDto.setDispatchOrderId(eventWarehouseCheckout.getPackageKlId());
        msgDto.setMaterialId(eventWarehouseCheckout.getMaterialId());
        Size size = new Size();
        size.setLength(Double.valueOf(eventWarehouseCheckout.getLength()));
        size.setWidth(Double.valueOf(eventWarehouseCheckout.getWidth()));
        size.setHeight(Double.valueOf(eventWarehouseCheckout.getHeight()));
        msgDto.setSize(size);
        msgDto.setWeightInKg(String.valueOf(eventWarehouseCheckout.getWholePackageWeight()));
        if (StringUtils.isNotBlank(eventWarehouseCheckout.getExceptionType()) || StringUtils.isNotBlank(eventWarehouseCheckout.getExceptionDesc())) {
            msgDto.setFailureReason(eventWarehouseCheckout.getExceptionType()+eventWarehouseCheckout.getExceptionDesc());
            msgDto.setShipped(false);
        }
        msgDto.setItems(new ArrayList<>());
        String jsonString = JSON.toJSONString(msgDto);
        log.info("将平台接受的货物入仓转成JOSN字符串：{}", jsonString);
        depositoryDeliveryResultConsumer.onMessage(jsonString);
    }
}

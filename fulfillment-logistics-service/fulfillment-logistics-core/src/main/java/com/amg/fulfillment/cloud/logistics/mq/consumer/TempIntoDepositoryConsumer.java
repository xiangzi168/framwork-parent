package com.amg.fulfillment.cloud.logistics.mq.consumer;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.amg.framework.cloud.grpc.utils.GrpcJsonFormatUtils;
import com.amg.fulfillment.cloud.logistics.api.dto.msg.PurchaseIntoDepositoryMsgDto;
import com.amg.fulfillment.cloud.logistics.api.proto.WarehouseOuterClass;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 平台入库通知
 */
@Slf4j
@Component
//@RocketMQMessageListener(topic = "EventWarehouseCheckin")
public class TempIntoDepositoryConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private DepositoryIntoDepositoryMsgConsumer depositoryIntoDepositoryMsgConsumer;
    @Override
    public void onMessage(MessageExt message) {
        try {
            WarehouseOuterClass.EventWarehouseCheckin eventWarehouseCheckin = WarehouseOuterClass.EventWarehouseCheckin.parseFrom(message.getBody());
            log.info("监听到平台推送的货物入仓消息：{}", GrpcJsonFormatUtils.toJsonString(eventWarehouseCheckin));
            process(eventWarehouseCheckin);
            log.info("监听到平台推送的货物入仓消息处理完成：{}", DateUtil.now());
        } catch (Exception e) {
            log.error("监听到平台推送的货物入仓消息错误：{}",e);
        }
    }

    private void process( WarehouseOuterClass.EventWarehouseCheckin eventWarehouseCheckin) {
        String packageKlId = eventWarehouseCheckin.getPackageKlId();
        List<WarehouseOuterClass.CheckinItem> checkinItemsList = eventWarehouseCheckin.getCheckinItemsList();
        PurchaseIntoDepositoryMsgDto msgDto = new PurchaseIntoDepositoryMsgDto();
        msgDto.setPurchaseOrderId(packageKlId);
        List<PurchaseIntoDepositoryMsgDto.PurchaseOrderReceiveItemFeedback> purchaseOrderReceiveItemFeedbacks = checkinItemsList.stream().map(item -> {
            PurchaseIntoDepositoryMsgDto.PurchaseOrderReceiveItemFeedback itemFeedback = new PurchaseIntoDepositoryMsgDto.PurchaseOrderReceiveItemFeedback();
            itemFeedback.setId(item.getItemKlId());
            itemFeedback.setWeightInKg(Double.valueOf(item.getMeasuredWeight()*1.0 / 1000));
            itemFeedback.setIssueType(item.getExceptionType());
            itemFeedback.setIssueNote(item.getExceptionDesc());
            itemFeedback.setImageUrls(item.getPhotoUrlsList());
            itemFeedback.setReceived(StringUtils.isBlank(item.getExceptionType()) ? true : false);
            return itemFeedback;
        }).collect(Collectors.toList());
        msgDto.setItems(purchaseOrderReceiveItemFeedbacks);
        String jsonString = JSON.toJSONString(msgDto);
        log.info("将平台接受的货物入仓转成JOSN字符串：{}",jsonString );
        depositoryIntoDepositoryMsgConsumer.onMessage(jsonString);
    }
}

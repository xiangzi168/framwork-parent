package com.amg.fulfillment.cloud.logistics.util;

import com.alibaba.fastjson.JSON;
import com.amg.framework.cloud.rocketmq.utils.RocketmqUtils;
import com.amg.fulfillment.cloud.logistics.api.dto.msg.DepositoryPurchaseStatusMsgDto;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsPackageDO;
import com.amg.fulfillment.cloud.message.api.config.MqTopicConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static org.apache.rocketmq.client.producer.SendStatus.SEND_OK;

/**
 * Created by Seraph on 2021/5/27
 */

@Slf4j
@Component
public class SendMsgUtils {

    @Autowired
    private RocketmqUtils rocketmqUtils;
    @Autowired
    private MqTopicConstant mqTopicConstant;


    public Boolean sendUnfinishedPackageIdList(List<LogisticsPackageDO> logisticsPackageDOList)
    {
        SendResult sendResult = rocketmqUtils.syncSend(mqTopicConstant.getLogisticUnfinishedPackageTopic(), logisticsPackageDOList);
        log.info("接受仓库的入库发送MQ处理结果是【{}】,返回详情是：{}，发送到mq返回消息是,{}", sendResult.getSendStatus(), sendResult, logisticsPackageDOList);
        if (SEND_OK.equals(sendResult.getSendStatus())) {
            return true;
        }
        return false;
    }

    public void sendPurchaseIntoDepositoryMsg(List<DepositoryPurchaseStatusMsgDto> depositoryPurchaseStatusMsgDtoList, Integer status)
    {
        for (DepositoryPurchaseStatusMsgDto depositoryPurchaseStatusMsgDto : depositoryPurchaseStatusMsgDtoList)
        {
            depositoryPurchaseStatusMsgDto.setStatus(status);
            depositoryPurchaseStatusMsgDto.setNoticeTime(new Date());
        }
        this.doSendPurchaseIntoDepositoryMsg(depositoryPurchaseStatusMsgDtoList);
    }

    private Boolean doSendPurchaseIntoDepositoryMsg(List<DepositoryPurchaseStatusMsgDto> depositoryPurchaseStatusMsgDtoList)
    {
        String jsonString = JSON.toJSONString(depositoryPurchaseStatusMsgDtoList);

        SendResult sendResult = rocketmqUtils.syncSend(mqTopicConstant.getLogisticPurchaseStatusTopic(), depositoryPurchaseStatusMsgDtoList);
        log.info("接受仓库的入库发送MQ处理结果是【{}】,返回详情是：{}，发送到mq返回消息是,{}", sendResult.getSendStatus(), sendResult, jsonString);
        if (SEND_OK.equals(sendResult.getSendStatus())) {
            return true;
        }
        return false;
    }
}

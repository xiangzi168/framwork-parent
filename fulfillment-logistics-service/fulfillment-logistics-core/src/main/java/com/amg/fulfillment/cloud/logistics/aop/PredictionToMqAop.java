package com.amg.fulfillment.cloud.logistics.aop;

import com.amg.framework.cloud.rocketmq.utils.RocketmqUtils;
import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import com.amg.fulfillment.cloud.logistics.model.req.ManualPredictionReq;
import com.amg.fulfillment.cloud.logistics.model.req.PurchasePackagePredictionReq;
import com.amg.fulfillment.cloud.order.api.dto.AddPackageMqDTO;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 该切面为了提供添加采购物包裹时将渠道订单号给订单MQ
 */
@Aspect
@Component
@Slf4j
public class PredictionToMqAop {

    @Autowired
    private RocketmqUtils rocketmqUtils;

    @Pointcut(value = "execution(* com.amg.fulfillment.cloud.logistics.service.impl.PurchasePackageServiceImpl.manualPredictionPurchaseId(..)) && args(manualPredictionReq)")
    public void manualPredictionPoint(ManualPredictionReq manualPredictionReq) {

    }

    @After(value = "manualPredictionPoint(manualPredictionReq)")
    public void addLogisticOrderExecute(ManualPredictionReq manualPredictionReq) {
        log.info("start 接受到添加包裹的消息，将渠道订单号发送mq中，接受到的内容是：{}",manualPredictionReq);
        Integer packageSourceType = manualPredictionReq.getPackageSourceType();
        Set<String> channelOrderIdSet = manualPredictionReq.getPredictionDetails().stream().map(ManualPredictionReq.PredictionDetail::getChannelOrderId).collect(Collectors.toSet());
        if (channelOrderIdSet.isEmpty()) {
            return;
        }
        List<AddPackageMqDTO> list = channelOrderIdSet.stream().map(item -> {
            AddPackageMqDTO addPackageMqDTO = new AddPackageMqDTO();
            addPackageMqDTO.setChannelOrderNumber(item);
            addPackageMqDTO.setChannelType(packageSourceType);
            return addPackageMqDTO;
        }).collect(Collectors.toList());
        SendResult sendResult = rocketmqUtils.syncSend(Constant.LOGISTIC_PURCHASE_PREDICTION_WAYBILL_NOTICE, list);
        log.info("end 接受到添加包裹的消息，将渠道订单号发送mq中，发送的结果是：{}，发送到的内容是：{}",sendResult,manualPredictionReq);
    }
}

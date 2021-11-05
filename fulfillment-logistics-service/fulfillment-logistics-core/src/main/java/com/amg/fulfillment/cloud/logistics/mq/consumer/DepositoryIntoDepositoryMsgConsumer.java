package com.amg.fulfillment.cloud.logistics.mq.consumer;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.amg.framework.cloud.rocketmq.utils.RocketmqUtils;
import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import com.amg.fulfillment.cloud.logistics.api.dto.msg.DepositoryPurchaseStatusMsgDto;
import com.amg.fulfillment.cloud.logistics.api.dto.msg.PurchaseIntoDepositoryMsgDto;
import com.amg.fulfillment.cloud.logistics.api.enumeration.*;
import com.amg.fulfillment.cloud.logistics.entity.DepositorySaleOrderDetailDO;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsPurchasePackageDO;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsPurchasePackageErrorProductDO;
import com.amg.fulfillment.cloud.logistics.mapper.DepositorySaleOrderDetailMapper;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsPurchasePackageErrorProductMapper;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsPurchasePackageMapper;
import com.amg.fulfillment.cloud.message.api.config.MqTopicConstant;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Tom
 * @date 2021_04_27_20:31
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = "${mq.logistic.logistic-depository-third-notice.topic}"
        , selectorExpression = "${mq.logistic.logistic-depository-third-notice.tag2}"
        , consumerGroup = "${mq.logistic.logistic-depository-third-notice.tag2}")
public class DepositoryIntoDepositoryMsgConsumer implements RocketMQListener<String> {

    @Autowired
    private LogisticsPurchasePackageErrorProductMapper logisticsPurchasePackageErrorProductMapper;

    @Autowired
    private LogisticsPurchasePackageMapper logisticsPurchasePackageMapper;

    @Autowired
    private DepositorySaleOrderDetailMapper depositorySaleOrderDetailMapper;

    @Autowired
    private RocketmqUtils rocketmqUtils;

    @Autowired
    private MqTopicConstant mqTopicConstant;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Override
    public void onMessage(String message) {
        log.info("仓库--接受到仓库的采购订单快递入库消息：{}", message);
        //保留到数据库
        String isOk = transactionTemplate.execute(status -> this.processMsgIntoDatabase(message));
        // 消息通知
        if ("ok".equals(isOk)) {
            this.processDepoistoryMsgToMq(message);
        }
    }

    public void processMsgIntoDatabaseForTemp(String message){
        log.info("仓库--接受到仓库的采购订单快递入库消息：{}", message);
        //保留到数据库
        String isOk = transactionTemplate.execute(status -> this.processMsgIntoDatabase(message));
        // 消息通知
        if ("ok".equals(isOk)) {
            this.processDepoistoryMsgToMq(message);
        }
    }

    private String processMsgIntoDatabase(String message) {
        PurchaseIntoDepositoryMsgDto msg = JSON.parseObject(message, PurchaseIntoDepositoryMsgDto.class);
        log.info("仓库--接受到仓库的采购订单快递入库消息,转成对象是：{}", msg);
        ArrayList<PurchaseIntoDepositoryMsgDto.PurchaseOrderReceiveItemFeedback> purchaseOrderReceiveItemFeedbackAll = new ArrayList<>();
        // exsitException 是因为仓库返回的数据是以一个包裹给出的数据，所以可以全局确定包裹是否存在异常
        boolean exsitException = false;
        LambdaQueryWrapper<LogisticsPurchasePackageDO> queryWrapperForPackage = Wrappers.<LogisticsPurchasePackageDO>lambdaQuery()
                .eq(LogisticsPurchasePackageDO::getPackageNo, msg.getPurchaseOrderId());
        LogisticsPurchasePackageDO logisticsPurchasePackageDO = logisticsPurchasePackageMapper.selectOne(queryWrapperForPackage);
        if (Objects.isNull(logisticsPurchasePackageDO) || logisticsPurchasePackageDO.getId() == null) {
            log.error("仓库--接受到仓库的采购订单快递入库消息的包裹号在系统内查询不到，请核实！包裹号是：{}", msg.getPurchaseOrderId());
            return "fail";
        }
        if (logisticsPurchasePackageDO.getWarehousing() == PurchasePackageWarehousingTypeEnum.YES.getType()) {
            return "ok";
        }
        LambdaQueryWrapper<LogisticsPurchasePackageErrorProductDO> queryWrapperForErrorProduct = Wrappers.<LogisticsPurchasePackageErrorProductDO>lambdaQuery()
                .eq(LogisticsPurchasePackageErrorProductDO::getPurchasePackageId, logisticsPurchasePackageDO.getId());
        List<LogisticsPurchasePackageErrorProductDO> logisticsPurchasePackageErrorProductDOs = logisticsPurchasePackageErrorProductMapper.selectList(queryWrapperForErrorProduct);
        if (Objects.isNull(logisticsPurchasePackageErrorProductDOs) || logisticsPurchasePackageErrorProductDOs.isEmpty()) {
            log.error("仓库--接受到仓库的采购订单快递入库消息的包裹号存在但没有对应的包裹内容，请核实！包裹号是：{}", msg.getPurchaseOrderId());
        }
        Map<String, List<LogisticsPurchasePackageErrorProductDO>> errorProductMapByPurchaseId = logisticsPurchasePackageErrorProductDOs.stream()
                .filter(item -> StringUtils.isNotBlank(item.getPurchaseId()))
                .collect(Collectors.groupingBy(LogisticsPurchasePackageErrorProductDO::getPurchaseId));
        for (PurchaseIntoDepositoryMsgDto.PurchaseOrderReceiveItemFeedback item : msg.getItems()) {

            LogisticsPurchasePackageErrorProductDO logisticsPurchasePackageErrorProductDO = Optional.ofNullable(errorProductMapByPurchaseId.get(item.getId())).orElseGet(() -> Arrays.asList(new LogisticsPurchasePackageErrorProductDO())).get(0);
            if (StringUtils.isBlank(logisticsPurchasePackageErrorProductDO.getPurchaseId())) {
                continue;
            }
            LambdaUpdateWrapper<LogisticsPurchasePackageErrorProductDO> updateWrapper = Wrappers.<LogisticsPurchasePackageErrorProductDO>lambdaUpdate()
                    .set(LogisticsPurchasePackageErrorProductDO::getArriveTime, StrUtil.isNotBlank(msg.getReceiveOn()) ? DateUtil.parse(msg.getReceiveOn(), DatePattern.UTC_FORMAT).toJdkDate() : null)
                    .set(LogisticsPurchasePackageErrorProductDO::getErrorType, StringUtils.isBlank(item.getIssueType()) ? PurchasePackageProductErrorTypeEnum.NONE.getType() : item.getIssueType())
                    .set(LogisticsPurchasePackageErrorProductDO::getWeight, !Objects.isNull(item.getWeightInKg()) ? new BigDecimal(Double.valueOf(item.getWeightInKg() * 1000).toString()) : new BigDecimal("1000"))
                    .eq(LogisticsPurchasePackageErrorProductDO::getId, logisticsPurchasePackageErrorProductDO.getId());
            if ((PurchasePackageProductErrorTypeEnum.NONE.getType().equals(item.getIssueType())) || (StringUtils.isBlank(item.getIssueType())) && item.isReceived()) {
                updateWrapper.set(LogisticsPurchasePackageErrorProductDO::getStatus, DepositoryPurchaseStatusEnum.WAREHOUSING.getStatusCode());
            } else {
                exsitException = true;
                updateWrapper
                        .set(LogisticsPurchasePackageErrorProductDO::getErrorHandle, PurchasePackageProductErrorHandleTypeEnum.PENDING_ERROR.getType())
                        .set(LogisticsPurchasePackageErrorProductDO::getStatus, DepositoryPurchaseStatusEnum.HANLDINGEXCEPTION.getStatusCode())
                        .set(LogisticsPurchasePackageErrorProductDO::getErrorMessage, item.getIssueNote())
                        .set(LogisticsPurchasePackageErrorProductDO::getErrorImg, (!Objects.isNull(item.getImageUrls()) && !item.getImageUrls().isEmpty()) ? JSON.toJSONString(item.getImageUrls()) : null);
            }
            purchaseOrderReceiveItemFeedbackAll.add(item);
            logisticsPurchasePackageErrorProductMapper.update(null, updateWrapper);
        }
        // 更新包裹标签
        LambdaUpdateWrapper<LogisticsPurchasePackageDO> updateWrapperForLogisticsPurchase = Wrappers.<LogisticsPurchasePackageDO>lambdaUpdate()
                .set(LogisticsPurchasePackageDO::getLabel, exsitException ? PurchasePackageLabelTypeEnum.PENDING_ERROR.getType() : PurchasePackageLabelTypeEnum.NONE_ERROR.getType())
                .set(LogisticsPurchasePackageDO::getWarehousing, PurchasePackageWarehousingTypeEnum.YES.getType())
                .set(LogisticsPurchasePackageDO::getReceivingGoodTime, DateUtil.date().toJdkDate())
                .set(LogisticsPurchasePackageDO::getWarehousingFeedbackAction, msg.getFeedbackAction())
                .set(LogisticsPurchasePackageDO::getWarehousingPurchaseReason, msg.getPurchaseReason())
                .set(LogisticsPurchasePackageDO::getWarehousingExpressId, msg.getExpressId())
                .set(LogisticsPurchasePackageDO::getWarehousingExpressReceiveTime, StrUtil.isNotBlank(msg.getReceiveOn()) ? DateUtil.parse(msg.getReceiveOn(), DatePattern.UTC_FORMAT).toJdkDate() : null)
                .set(LogisticsPurchasePackageDO::getWarehousingExpressImage, Objects.isNull(msg.getExpressImageUrls()) ? null : msg.getExpressImageUrls().stream().collect(Collectors.joining(",")))
                .eq(LogisticsPurchasePackageDO::getId, logisticsPurchasePackageDO.getId());
        logisticsPurchasePackageMapper.update(null, updateWrapperForLogisticsPurchase);
        // 更新采购表数据
        if (!purchaseOrderReceiveItemFeedbackAll.isEmpty()) {
            cascadeUpdateSaleOrderDetailTable(purchaseOrderReceiveItemFeedbackAll);
        }
        return "ok";
    }

    private void cascadeUpdateSaleOrderDetailTable(List<PurchaseIntoDepositoryMsgDto.PurchaseOrderReceiveItemFeedback> receivePackageProductList) {
        List<String> purcahseIdList = receivePackageProductList.stream().map(PurchaseIntoDepositoryMsgDto.PurchaseOrderReceiveItemFeedback::getId).collect(Collectors.toList());
        LambdaQueryWrapper<LogisticsPurchasePackageErrorProductDO> queryWrapper = Wrappers.<LogisticsPurchasePackageErrorProductDO>lambdaQuery()
                .in(LogisticsPurchasePackageErrorProductDO::getPurchaseId, purcahseIdList);
        List<LogisticsPurchasePackageErrorProductDO> errorProductDOS_Primary = logisticsPurchasePackageErrorProductMapper.selectList(queryWrapper);
        List<LogisticsPurchasePackageErrorProductDO> errorProductDOS = errorProductDOS_Primary.stream().filter(item -> item.getStatus() > DepositoryPurchaseStatusEnum.WAITINGWAREHOUSING.getStatusCode()).collect(Collectors.toList());
        for (LogisticsPurchasePackageErrorProductDO errorProductDO : errorProductDOS) {
            LambdaUpdateWrapper<DepositorySaleOrderDetailDO> updateWrapper = Wrappers.<DepositorySaleOrderDetailDO>lambdaUpdate()
                    .set(DepositorySaleOrderDetailDO::getReceiveState, errorProductDO.getStatus())
                    .set(DepositorySaleOrderDetailDO::getReceivePurchaseId, errorProductDO.getPurchaseId())
                    .set(DepositorySaleOrderDetailDO::getPurchasePackageId, errorProductDO.getPurchasePackageId())
                    .set(DepositorySaleOrderDetailDO::getWeight, errorProductDO.getWeight())
                    .set(DepositorySaleOrderDetailDO::getReceiveTime, errorProductDO.getArriveTime())
                    .eq(DepositorySaleOrderDetailDO::getItemId, errorProductDO.getItemId())
                    .eq(DepositorySaleOrderDetailDO::getSaleOrder, errorProductDO.getSalesOrderId())
                    .lt(DepositorySaleOrderDetailDO::getReceiveState, DepositoryPurchaseStatusEnum.WAREHOUSING.getStatusCode()); // 已入库为终态，幂等
            depositorySaleOrderDetailMapper.update(null, updateWrapper);
        }
    }


    private void processDepoistoryMsgToMq(String message) {
        log.info("监听到平台推送的货物入仓消息处理完成发到mq中消息是：{}", message);
        PurchaseIntoDepositoryMsgDto msg = JSON.parseObject(message, PurchaseIntoDepositoryMsgDto.class);
        List<String> purchaseIds = msg.getItems().stream().map(PurchaseIntoDepositoryMsgDto.PurchaseOrderReceiveItemFeedback::getId).collect(Collectors.toList());
        if (purchaseIds.isEmpty()) {
            return;
        }
        LambdaQueryWrapper<LogisticsPurchasePackageErrorProductDO> queryWrapper = Wrappers.<LogisticsPurchasePackageErrorProductDO>lambdaQuery()
                .in(LogisticsPurchasePackageErrorProductDO::getPurchaseId, purchaseIds);
        List<LogisticsPurchasePackageErrorProductDO> errorProductDOS = logisticsPurchasePackageErrorProductMapper.selectList(queryWrapper);
        Map<String, List<LogisticsPurchasePackageErrorProductDO>> mapByPurchaseId = errorProductDOS.stream().collect(Collectors.groupingBy(LogisticsPurchasePackageErrorProductDO::getPurchaseId));
        List<DepositoryPurchaseStatusMsgDto> statusMsgDtos = msg.getItems().stream().map(item -> {
            DepositoryPurchaseStatusMsgDto statusMsgDto = new DepositoryPurchaseStatusMsgDto();
            statusMsgDto.setItemId(Optional.ofNullable(mapByPurchaseId.get(item.getId())).orElse(Arrays.asList(new LogisticsPurchasePackageErrorProductDO())).get(0).getItemId());
            statusMsgDto.setSalesOrderId(Optional.ofNullable(mapByPurchaseId.get(item.getId())).orElse(Arrays.asList(new LogisticsPurchasePackageErrorProductDO())).get(0).getSalesOrderId());
            statusMsgDto.setPurchaseId(item.getId());
            statusMsgDto.setStatus((PurchasePackageProductErrorTypeEnum.NONE.getType().equals(item.getIssueType()) || (StringUtils.isBlank(item.getIssueType()) && item.isReceived())) ? DepositoryPurchaseStatusEnum.WAREHOUSING.getStatusCode() : DepositoryPurchaseStatusEnum.HANLDINGEXCEPTION.getStatusCode());
            statusMsgDto.setWeight(item.getWeightInKg() == null ? 1000 : item.getWeightInKg() * 1000);
            statusMsgDto.setNoticeTime(DateUtil.date().toJdkDate());
            return statusMsgDto;
        }).collect(Collectors.toList());
        // todo 旧系统迁移完毕，将该代码删掉///////////////////////////////////
        List<DepositoryPurchaseStatusMsgDto> msgDtoList = statusMsgDtos.stream().filter(item -> StringUtils.isNotBlank(item.getItemId())).collect(Collectors.toList());
        if (msgDtoList.isEmpty()) {
            log.info("通知---仓库的采购订单入库消息到MQ，消息内容是null：可以直接返回：{}", msgDtoList);
            return;
        }
        /////////////////////////////////////////////////////////////////
        log.info("通知---仓库的采购订单入库消息到MQ，消息内容是：{}", JSON.toJSONString(msgDtoList));
        rocketmqUtils.asyncSend(mqTopicConstant.getLogisticPurchaseStatusTopic(), msgDtoList, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("通知---仓库的采购订单入库消息成功，消息内容是：{}", statusMsgDtos);
            }

            @Override
            public void onException(Throwable e) {
                log.error("通知---仓库的采购订单入库消息失败，消息内容是：{}", msg);
            }
        });
        ////////////// add: 由于采购服务需要，要对淘宝、拼多多独立一个topic,这里发送新的mq////////////////////////////////////////////////////////////////
        List<DepositoryPurchaseStatusMsgDto> pddAndTaoBaoListToMq = new ArrayList<>();
        List<Long> purchasePackageIds = errorProductDOS.stream().map(LogisticsPurchasePackageErrorProductDO::getPurchasePackageId).collect(Collectors.toList());
        LambdaQueryWrapper<LogisticsPurchasePackageDO> queryWrapperForPackage = Wrappers.<LogisticsPurchasePackageDO>lambdaQuery()
                .in(LogisticsPurchasePackageDO::getId, purchasePackageIds);
        List<LogisticsPurchasePackageDO> logisticsPurchasePackageDOS = logisticsPurchasePackageMapper.selectList(queryWrapperForPackage);
        List<LogisticsPurchasePackageDO> taoBaoAndPdds = logisticsPurchasePackageDOS.stream()
                .filter(item -> item.getPackageSourceType().equals(DeliveryPackagePurchaseChannelEnum.TAOBAO.getType()) || item.getPackageSourceType().equals(DeliveryPackagePurchaseChannelEnum.PDD.getType()))
                .collect(Collectors.toList());
        if (taoBaoAndPdds.isEmpty()) {
            return;
        }
        // 将全部消息分组
        Map<String, List<DepositoryPurchaseStatusMsgDto>> mapByPurchaseIdMsg = statusMsgDtos.stream().collect(Collectors.groupingBy(DepositoryPurchaseStatusMsgDto::getPurchaseId));
        Map<Long, List<LogisticsPurchasePackageErrorProductDO>> mapByPackageId = errorProductDOS.stream().collect(Collectors.groupingBy(LogisticsPurchasePackageErrorProductDO::getPurchasePackageId));
        taoBaoAndPdds.stream().forEach(item ->{
            List<LogisticsPurchasePackageErrorProductDO> logisticsPurchasePackageErrorProductDOS = Optional.ofNullable(mapByPackageId.get(item.getId())).orElseGet(() ->Collections.emptyList());
            logisticsPurchasePackageErrorProductDOS.stream().forEach(errorItem ->{
                // 找到pdd和淘宝的消息
                List<DepositoryPurchaseStatusMsgDto> depositoryPurchaseStatusMsgDtos = Optional.ofNullable(mapByPurchaseIdMsg.get(errorItem.getPurchaseId())).orElseGet(() ->Collections.emptyList());
                pddAndTaoBaoListToMq.addAll(depositoryPurchaseStatusMsgDtos);
            });
        });
        log.info("通知---仓库的采购订单入库消息到针对pdd和淘宝的topic中，消息内容是：{}", JSON.toJSONString(msgDtoList));
        rocketmqUtils.asyncSend(Constant.LOGISTIC_PURCHASE_STATUS_TAOBAO_PDD_NOTICE, pddAndTaoBaoListToMq, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("通知----仓库的采购订单入库消息到针对pdd和淘宝消息成功，消息内容是：{}", statusMsgDtos);
            }

            @Override
            public void onException(Throwable e) {
                log.error("通知----仓库的采购订单入库消息到针对pdd和淘宝消息失败，消息内容是：{}", msg);
            }
        });
    }
}

package com.amg.fulfillment.cloud.logistics.job;

import com.alibaba.fastjson.JSON;
import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb.PurchaseIntoDepositoryDetailForWanB;
import com.amg.fulfillment.cloud.logistics.api.dto.msg.PurchaseIntoDepositoryMsgDto;
import com.amg.fulfillment.cloud.logistics.api.enumeration.PurchasePackageProductErrorTypeEnum;
import com.amg.fulfillment.cloud.logistics.api.enumeration.PurchasePackageWarehousingTypeEnum;
import com.amg.fulfillment.cloud.logistics.api.util.WanBDepistoryUtil;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsPurchasePackageDO;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsPurchasePackageMapper;
import com.amg.fulfillment.cloud.logistics.mq.consumer.DepositoryIntoDepositoryMsgConsumer;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 拉取1688采购订单已入库未通知状态
 */
@Slf4j
@JobHandler(value = "purchase-into-depository-retry-job")
@Component
public class PurchaseIntoDepositoryRetryJob extends IJobHandler {
    @Autowired
    private WanBDepistoryUtil wanBDepistoryUtil;
    @Autowired
    private LogisticsPurchasePackageMapper logisticsPurchasePackageMapper;
    @Autowired
    private DepositoryIntoDepositoryMsgConsumer depositoryIntoDepositoryMsgConsumer;
    @Override
    public ReturnT<String> execute(String param) throws Exception {
        log.info("--------------------------------------开始执行purchase-into-depository-retry-job任务----------------------------------------------");
        log.info("手动入参是:{}",param);
        Long paramLong = 1L;
        try {
            if (StringUtils.isNotBlank(param)) {
                paramLong = Long.valueOf(param);
            }
        } catch (NumberFormatException e) {
            log.error("手动入参error :{},cause :{}", param, e);
        }
        LambdaQueryWrapper<LogisticsPurchasePackageDO> queryWrapper = Wrappers.<LogisticsPurchasePackageDO>lambdaQuery()
                .eq(LogisticsPurchasePackageDO::getWarehousing, PurchasePackageWarehousingTypeEnum.NO.getType())
                .eq(LogisticsPurchasePackageDO::getValid, Constant.YES)
                .between(LogisticsPurchasePackageDO::getCreateTime, LocalDateTime.now().minusMonths(1L), LocalDateTime.now().minusDays(Objects.isNull(paramLong) ? 1L : paramLong));
        List<LogisticsPurchasePackageDO> packageDOList = logisticsPurchasePackageMapper.selectList(queryWrapper);
        if (packageDOList.isEmpty()) {
            return ReturnT.SUCCESS;
        }
        packageDOList.stream().forEach(item ->{
            try {
                PurchaseIntoDepositoryDetailForWanB detailForWanB = wanBDepistoryUtil.queryPurchaseOrderDetail(item.getPackageNo());
                if (detailForWanB.isSucceeded()) {
                    process(item.getPackageNo(),detailForWanB.getData());
                } else {
                    log.error("purchase-into-depository-retry-job定时任务，查询该包裹号【{}】失败，失败原因：{}",item.getPackageNo(),detailForWanB.getError());
                }
            } catch (Exception e) {
                log.error("purchase-into-depository-retry-job定时任务，查询该包裹号【{}】失败，失败原因：{}", item.getPackageNo(), e);
            }
        });
        log.info("--------------------------------------结束purchase-into-depository-retry-job任务----------------------------------------------");
        return ReturnT.SUCCESS;
    }

    private void process(String packageNo, PurchaseIntoDepositoryDetailForWanB.PurchaseIntoDepositoryDetail detail) {
        long quantity = detail.getProducts().stream().filter(item -> !Objects.isNull(item.getReceivedQuantity())).count();
        if (quantity < 1) {
            return;
        }
        PurchaseIntoDepositoryMsgDto msgDto = new PurchaseIntoDepositoryMsgDto();
        msgDto.setFeedbackAction("【job查询】");
        msgDto.setPurchaseOrderId(detail.getRefId());
        msgDto.setPurchaseReason(detail.getPurchaseReason());
        msgDto.setExpressId(detail.getExpresses().get(0).getExpress().getExpressId());
        msgDto.setReceiveOn(detail.getArriveOn());
        msgDto.setExpressId(detail.getExpresses().get(0).getExpress().getExpressId());
        // 获取重量
        HashMap<String, Double> mapBySku = new HashMap<>();
        detail.getProducts().forEach(item -> {
            mapBySku.putIfAbsent(item.getSku(), StringUtils.isNotBlank(item.getActualWeightInKg()) ? Double.valueOf(item.getActualWeightInKg()) : Constant.DEFAULT_SALEORDER_PRODUCT_WEIGHT_KG);
        });
        List<PurchaseIntoDepositoryMsgDto.PurchaseOrderReceiveItemFeedback> feedbackList = detail.getItems().stream().map(item -> {
            PurchaseIntoDepositoryMsgDto.PurchaseOrderReceiveItemFeedback itemFeedback = new PurchaseIntoDepositoryMsgDto.PurchaseOrderReceiveItemFeedback();
            itemFeedback.setId(item.getId());
            itemFeedback.setSalesOrderId(item.getSalesOrderId());
            itemFeedback.setSku(item.getSku());
            itemFeedback.setIssueType(item.getIssueType());
            itemFeedback.setReceived(PurchasePackageProductErrorTypeEnum.QUANTITY.getType().equals(item.getIssueType()) ? false : true);
            itemFeedback.setWeightInKg(Optional.ofNullable(mapBySku.get(item.getSku())).orElseGet(() -> Constant.DEFAULT_SALEORDER_PRODUCT_WEIGHT_KG));
            return itemFeedback;
        }).collect(Collectors.toList());
        msgDto.setItems(feedbackList);
        String message = JSON.toJSONString(msgDto);
        log.info("purchase-into-depository-retry-job查询到采购入库消息未及时通知的订单：{}，内容是：{}",packageNo,message);
        depositoryIntoDepositoryMsgConsumer.processMsgIntoDatabaseForTemp(message);
    }
}

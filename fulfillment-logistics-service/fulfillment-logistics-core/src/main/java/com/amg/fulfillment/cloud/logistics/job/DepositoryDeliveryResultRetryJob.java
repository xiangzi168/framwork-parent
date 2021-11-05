package com.amg.fulfillment.cloud.logistics.job;

import com.alibaba.fastjson.JSON;
import com.amg.fulfillment.cloud.logistics.api.dto.depository.Size;
import com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb.DepositoryProcessDetailForWanB;
import com.amg.fulfillment.cloud.logistics.api.dto.msg.DepositoryProcessMsgDto;
import com.amg.fulfillment.cloud.logistics.api.enumeration.DeliveryPackageDeliveryStatusEnum;
import com.amg.fulfillment.cloud.logistics.api.util.WanBDepistoryUtil;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsPackageDO;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsPackageMapper;
import com.amg.fulfillment.cloud.logistics.mq.consumer.DepositoryDeliveryResultConsumer;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 仓库出库未及时通知查询
 */
@Slf4j
@JobHandler(value = "depository-delivery-retry-job")
@Component
public class DepositoryDeliveryResultRetryJob extends IJobHandler {

    @Autowired
    private LogisticsPackageMapper logisticsPackageMapper;
    @Autowired
    private WanBDepistoryUtil wanBDepistoryUtil;
    @Autowired
    private DepositoryDeliveryResultConsumer depositoryDeliveryResultConsumer;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        log.info("--------------------------------------开始执行depository-delivery-retry-job订单任务----------------------------------------------");
        LambdaQueryWrapper<LogisticsPackageDO> queryWrapperForPackage = Wrappers.<LogisticsPackageDO>lambdaQuery()
                .eq(LogisticsPackageDO::getDeliveryStatus, DeliveryPackageDeliveryStatusEnum.SENDING.getCode())
                .between(LogisticsPackageDO::getCreateTime, LocalDateTime.now().minusMonths(1L), LocalDateTime.now());
        List<LogisticsPackageDO> packageDOList = logisticsPackageMapper.selectList(queryWrapperForPackage);
        if (Objects.isNull(packageDOList) && packageDOList.size() < 1) {
            return ReturnT.SUCCESS;
        }
        packageDOList.stream().forEach(item -> {
            try {
                DepositoryProcessDetailForWanB detailForWanB = wanBDepistoryUtil.queryOutOrderDetail(item.getLogisticsOrderNo());
                if (detailForWanB.isSucceeded()) {
                    process(item.getLogisticsOrderNo(), detailForWanB.getData());
                } else {
                    log.error("depository-delivery-retry-job定时任务，查询该包裹号【{}】失败，失败原因：{}", item.getLogisticsOrderNo(), detailForWanB.getError());
                }
            } catch (Exception e) {
                log.error("depository-delivery-retry-job定时任务，查询该包裹号【{}】失败，失败原因：{}", item.getLogisticsOrderNo(), e);
            }
        });
        log.info("--------------------------------------结束depository-delivery-retry-job任务----------------------------------------------");
        return ReturnT.SUCCESS;
    }

    private void process(String logisticsOrderNo, DepositoryProcessDetailForWanB.OutOrderDetail detail) {
        if (!"Shipped".equals(detail.getStatus())) {
            return;
        }
        DepositoryProcessMsgDto depositoryProcessMsgDto = new DepositoryProcessMsgDto();
        depositoryProcessMsgDto.setDispatchOrderId(detail.getRefId());
        depositoryProcessMsgDto.setShipped(true);
        depositoryProcessMsgDto.setProcessOn(detail.getPackOn());
        depositoryProcessMsgDto.setWeightInKg(detail.getPacking().getWeightInKg());
        Size size = new Size();
        size.setLength(Double.valueOf(detail.getPacking().getSize().getLength()));
        size.setWidth(Double.valueOf(detail.getPacking().getSize().getWidth()));
        size.setHeight(Double.valueOf(detail.getPacking().getSize().getHeight()));
        depositoryProcessMsgDto.setSize(size);
        depositoryProcessMsgDto.setMaterialId(detail.getPacking().getMaterialId());
        if (StringUtils.isNotBlank(detail.getPackFailReason())) {
            depositoryProcessMsgDto.setFailureReason(detail.getPackFailReason());
            depositoryProcessMsgDto.setShipped(false);
        }
        List<DepositoryProcessMsgDto.DispatchOrderProcessItem> itemList = detail.getItems().stream().map(item -> {
            DepositoryProcessMsgDto.DispatchOrderProcessItem dispatchOrderPackItem = new DepositoryProcessMsgDto.DispatchOrderProcessItem();
            dispatchOrderPackItem.setGlobalStockItemId(item.getGlobalStockItemId());
            dispatchOrderPackItem.setSku(item.getSku());
            return dispatchOrderPackItem;
        }).collect(Collectors.toList());
        depositoryProcessMsgDto.setItems(itemList);
        String message = JSON.toJSONString(depositoryProcessMsgDto);
        log.info("depository-delivery-retry-job查询到出库消息未及时通知的订单：{}，内容是：{}",logisticsOrderNo,message);
        depositoryDeliveryResultConsumer.onMessage(message);
    }
}

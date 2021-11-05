package com.amg.fulfillment.cloud.logistics.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amg.framework.boot.base.enums.ResponseCodeEnum;
import com.amg.framework.boot.base.exception.GlobalException;
import com.amg.framework.boot.redisson.utils.RedissLockUtil;
import com.amg.framework.boot.utils.id.SnowflakeIdUtils;
import com.amg.framework.boot.utils.thread.ThreadPoolUtil;
import com.amg.framework.cloud.grpc.utils.GrpcJsonFormatUtils;
import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import com.amg.fulfillment.cloud.logistics.api.dto.msg.DepositoryProcessMsgDto;
import com.amg.fulfillment.cloud.logistics.api.dto.msg.DepositoryPurchaseStatusMsgDto;
import com.amg.fulfillment.cloud.logistics.api.enumeration.*;
import com.amg.fulfillment.cloud.logistics.api.util.LogisticsCommonUtils;
import com.amg.fulfillment.cloud.logistics.config.LogisticsPackageValidEvent;
import com.amg.fulfillment.cloud.logistics.config.WBDepositoryRegisterServiceConfig;
import com.amg.fulfillment.cloud.logistics.dto.depository.*;
import com.amg.fulfillment.cloud.logistics.dto.logistic.*;
import com.amg.fulfillment.cloud.logistics.entity.*;
import com.amg.fulfillment.cloud.logistics.enumeration.DepositoryTypeEnum;
import com.amg.fulfillment.cloud.logistics.enumeration.LogisticTypeEnum;
import com.amg.fulfillment.cloud.logistics.factory.DepositoryFactory;
import com.amg.fulfillment.cloud.logistics.factory.LogisticFactory;
import com.amg.fulfillment.cloud.logistics.manager.IDepositoryManager;
import com.amg.fulfillment.cloud.logistics.manager.ILogisticManager;
import com.amg.fulfillment.cloud.logistics.mapper.*;
import com.amg.fulfillment.cloud.logistics.module.rule.LogisticMatchRuleHandler;
import com.amg.fulfillment.cloud.logistics.service.IDeliveryProductService;
import com.amg.fulfillment.cloud.logistics.util.MetadataPlusUtils;
import com.amg.fulfillment.cloud.logistics.util.SendMsgUtils;
import com.amg.fulfillment.cloud.order.api.proto.MetadatapbMetadata;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


/**
 * Created by Seraph on 2021/5/20
 */

@Slf4j
@Service
public class DeliveryProductServiceImpl implements IDeliveryProductService {

    @Autowired
    private LogisticsCommonUtils logisticsCommonUtils;
    @Autowired
    private LogisticFactory logisticFactory;
    @Autowired
    private DepositoryFactory depositoryFactory;
    @Autowired
    private LogisticsPackageMapper logisticsPackageMapper;
    @Autowired
    private LogisticsProductMapper logisticsProductMapper;
    @Autowired
    private LogisticsPackageItemMapper logisticsPackageItemMapper;
    @Autowired
    private SendMsgUtils sendMsgUtils;
    @Autowired
    private LogisticsLabelMapper logisticsLabelMapper;
    @Autowired
    private LogisticsLabelProductMapper logisticsLabelProductMapper;
    @Autowired
    private LogisticsLabelCategoryMapper logisticsLabelCategoryMapper;
    @Autowired
    private LogisticsPackageAddressMapper logisticsPackageAddressMapper;
    @Autowired
    private MetadataPlusUtils metadataPlusUtils;
    @Autowired
    private LogisticMatchRuleHandler logisticMatchRuleHandler;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private DepositorySaleOrderDetailMapper depositorySaleOrderDetailMapper;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private RedissLockUtil redissLockUtil;

    private final ExecutorService executorService = ThreadPoolUtil.getNewInstance(2, "logistics-manual-out-depository");

    @Override
    public void autoOutDepository(List<LogisticOrderDto> logisticOrderDtoList) {
        logisticOrderDtoList = logisticOrderDtoList.stream().filter(item -> outDepositoryLimitTime(item)).collect(Collectors.toList());
        //初始化数据
        this.initLogisticsPackage(logisticOrderDtoList, Boolean.FALSE);

        //匹配物流规则
        Iterator<LogisticOrderDto> iterator = logisticOrderDtoList.iterator();
        while (iterator.hasNext()) {
            LogisticOrderDto logisticOrderDto = iterator.next();
            LogisticsChannelDO logisticsChannelDO = logisticMatchRuleHandler.getLogisticsChannel(logisticOrderDto);
            if (logisticsChannelDO == null) {
                iterator.remove();
            } else {
                logisticOrderDto.setLogisticCode(logisticsChannelDO.getLogisticsCode());
                logisticOrderDto.setChannel(logisticsChannelDO.getChannelCode());
            }
        }

        //如果都没有匹配到物流，则直接返回
        if (logisticOrderDtoList.size() == 0) {
            log.info("未匹配到对应的所有规则，不会在物流表记录，内容是：{}", JSON.toJSONString(logisticOrderDtoList));
            return;
        }

        //出库操作
        this.doOutDepository(logisticOrderDtoList);
    }

    @Override
    public List<LogisticDispatchResponseDto> manualOutDepository(List<LogisticOrderDto> logisticOrderDtoList) {
        // 异步请求方法
        Callable<List<LogisticDispatchResponseDto>> callable = () -> {
            long count = logisticOrderDtoList.stream().filter(item -> !outDepositoryLimitTime(item)).count();
            if (count > 0) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "推送的订单已经在推送路上了，请一分钟后再试！");
            }
            //初始化数据
            this.initLogisticsPackage(logisticOrderDtoList, Boolean.FALSE);
            return this.doOutDepository(logisticOrderDtoList);
        };
        Future<List<LogisticDispatchResponseDto>> future = executorService.submit(callable);
        try {
            long start = System.currentTimeMillis();
            log.info(" 人工出库 main线程开始等待时间是：{}", LocalDateTime.now());
            List<LogisticDispatchResponseDto> list = future.get(8, TimeUnit.SECONDS);
            log.info(" 人工出库 main线程开始结束时间： {}，花费时间是：{}，输出的对象是：{}", LocalDateTime.now(), System.currentTimeMillis() - start, list);
            return list;
        } catch (TimeoutException e) {
            log.error("手动推送订单发送错误，错误原因是：{}", e);
            // 超时异常，为了展示给用户友好提示，特殊处理该状态码，谨慎删除
            throw new GlobalException("100200200", "由于第三方服务不稳当，系统在努力请求中……,请稍后再试");
        } catch (InterruptedException e) {
            log.error("手动推送订单发送错误，错误原因是：{}", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, e.getMessage());
        } catch (ExecutionException e) {
            log.error("手动推送订单发送错误，错误原因是：{}", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, e.getMessage());
        } catch (Exception e) {
            log.error("手动推送订单发送错误，错误原因是：{}", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, e.getMessage());
        }
    }

    @Override
    public void initLogisticsPackage(List<LogisticOrderDto> logisticOrderDtoList) {
        this.initLogisticsPackage(logisticOrderDtoList, Boolean.TRUE);
    }

    private void initLogisticsPackage(List<LogisticOrderDto> logisticOrderDtoList, Boolean tempFlag) {
        //设置物流单号数据
        logisticOrderDtoList.forEach(logisticOrderDto -> {
            if (tempFlag)        //是否临时数据
            {
                logisticOrderDto.setLogisticOrderNo("temp:" + SnowflakeIdUtils.getId());
                return;     //返回
            }
            //设置物流单编号
            logisticOrderDto.setLogisticOrderNo(logisticsCommonUtils.generateDeliveryPackageNo());
        });
        //设置标签属性
        this.logisticsPackageLabel(logisticOrderDtoList);
    }

    @Transactional
    @Override
    public void depositoryDeliveryResult(DepositoryProcessMsgDto depositoryProcessMsgDto) {
        LambdaQueryWrapper<LogisticsPackageDO> logisticsPackageDOLambdaQueryWrapper = Wrappers.<LogisticsPackageDO>lambdaQuery()
                .eq(LogisticsPackageDO::getLogisticsOrderNo, depositoryProcessMsgDto.getDispatchOrderId());

        LogisticsPackageDO logisticsPackageDO = logisticsPackageMapper.selectOne(logisticsPackageDOLambdaQueryWrapper);
        Map<String, List<LogisticsPackageItemDO>> mapByPurchaseId = new HashMap<>(16);
        if (logisticsPackageDO != null) {
            Integer logisticsStatus = logisticsPackageDO.getLogisticsStatus();
            if (DeliveryPackageLogisticsStatusEnum.INIT.getCode().equals(logisticsStatus)        //初始状态
                    || DeliveryPackageLogisticsStatusEnum.CREATED.getCode().equals(logisticsStatus)     //已创建物流单
//                    || DeliveryPackageLogisticsStatusEnum.INDELIVERY.getCode().equals(logisticsStatus)
                    || DeliveryPackageLogisticsStatusEnum.ERROR.getCode().equals(logisticsStatus)) {  //创建物流单异常

                LambdaQueryWrapper<LogisticsPackageItemDO> logisticsPackageItemDOLambdaQueryWrapper = Wrappers.<LogisticsPackageItemDO>lambdaQuery()
                        .eq(LogisticsPackageItemDO::getPackageId, logisticsPackageDO.getId());
                List<LogisticsPackageItemDO> logisticsPackageItemDOList = logisticsPackageItemMapper.selectList(logisticsPackageItemDOLambdaQueryWrapper);
                Set<String> purchaseIdList = logisticsPackageItemDOList.stream().map(LogisticsPackageItemDO::getPurchaseId).collect(Collectors.toSet());
                mapByPurchaseId = logisticsPackageItemDOList.stream().collect(Collectors.groupingBy(LogisticsPackageItemDO::getPurchaseId));
                LogisticsPackageDO tempLogisticsPackageDO = new LogisticsPackageDO();
                tempLogisticsPackageDO.setId(logisticsPackageDO.getId());
                tempLogisticsPackageDO.setLogisticsStatus(logisticsStatus);
                tempLogisticsPackageDO.setPackageLength(depositoryProcessMsgDto.getSize().getLength());
                tempLogisticsPackageDO.setPackageWidth(depositoryProcessMsgDto.getSize().getWidth());
                tempLogisticsPackageDO.setPackageHeight(depositoryProcessMsgDto.getSize().getHeight());
                tempLogisticsPackageDO.setPackageUnit(depositoryProcessMsgDto.getSize().getUnit());
                DepositoryPurchaseStatusEnum depositoryPurchaseStatusEnum = DepositoryPurchaseStatusEnum.SENDED;
                if (depositoryProcessMsgDto.isShipped()) {  //如果是已出库，则设置为已出库状态
                    tempLogisticsPackageDO.setLogisticsStatus(DeliveryPackageLogisticsStatusEnum.DELIVERED.getCode());      //已出库状态
                    tempLogisticsPackageDO.setDeliveryStatus(DeliveryPackageDeliveryStatusEnum.SENDED.getCode());
//                    tempLogisticsPackageDO.setReceivingGoodTime(new Date());
                    tempLogisticsPackageDO.setDeliveryTime(new Date());
                } else {
                    if (StringUtils.isNoneBlank(depositoryProcessMsgDto.getFailureReason())) {
                        tempLogisticsPackageDO.setLogisticsStatus(DeliveryPackageLogisticsStatusEnum.ERROR.getCode());       //出库异常
                        tempLogisticsPackageDO.setErrorInfo(depositoryProcessMsgDto.getFailureReason());     //出库异常数据
//                        tempLogisticsPackageDO.setIsValid(Constant.NO_0);      //设置为失效
                        tempLogisticsPackageDO.setDeliveryStatus(DeliveryPackageDeliveryStatusEnum.FAILSENDED.getCode());
                        depositoryPurchaseStatusEnum = DepositoryPurchaseStatusEnum.FAILSENDED;     //发货失败
                    }
                }
                logisticsPackageMapper.updateById(tempLogisticsPackageDO);      //设置出库数据
                casecadeUpdateDepositorySaleOrder(purchaseIdList, mapByPurchaseId);  // 更新采购订单itemId对应的
                //出库结果通知
                List<DepositoryPurchaseStatusMsgDto> depositoryPurchaseStatusMsgDtoList = new ArrayList<>();
                for (String purchaseId : purchaseIdList) {
                    LogisticsPackageItemDO logisticsPackageItemDO = Optional.of(mapByPurchaseId.get(purchaseId)).orElse(Collections.singletonList(new LogisticsPackageItemDO())).get(0);
                    DepositoryPurchaseStatusMsgDto depositoryPurchaseStatusMsgDto = new DepositoryPurchaseStatusMsgDto();
                    depositoryPurchaseStatusMsgDto.setSalesOrderId(logisticsPackageDO.getSalesOrderId());
                    depositoryPurchaseStatusMsgDto.setPurchaseId(purchaseId);
                    depositoryPurchaseStatusMsgDto.setItemId(logisticsPackageItemDO.getItemId());
                    depositoryPurchaseStatusMsgDtoList.add(depositoryPurchaseStatusMsgDto);
                }
                sendMsgUtils.sendPurchaseIntoDepositoryMsg(depositoryPurchaseStatusMsgDtoList, depositoryPurchaseStatusEnum.getStatusCode());
            }
        }
    }


    private List<LogisticDispatchResponseDto> doOutDepository(List<LogisticOrderDto> logisticOrderDtoList) {
        long start_1 = System.currentTimeMillis();
        log.debug(" doOutDepository 开始时间： {}", LocalDateTime.now());
        //保留到数据库
        // 对地址 stree1 = stree1+stree2
        logisticOrderDtoList.stream().forEach(order ->{
            order.getReceiverAddress().setStreet1(StringUtils.substring(order.getReceiverAddress().getStreet1() + (StringUtils.isNotBlank(order.getReceiverAddress().getStreet2()) ? order.getReceiverAddress().getStreet2() : ""), 0, 190));
        });
        transactionTemplate.execute(status -> {
            return this.doSaveLogisticsPackageData(logisticOrderDtoList);
        });
        //创建物流单信息
        List<LogisticDispatchResponseDto> logisticDispatchResponseDtoList = new ArrayList<>();
        logisticOrderDtoList.forEach(logisticOrderDto -> {
            try {
                List<WaybillGoodDetailDto> waybillGoodDetailDtoList = logisticOrderDto.getWaybillGoodDetailDtos();
                Map<String, List<WaybillGoodDetailDto>> waybillGoodDetailDtoMap = waybillGoodDetailDtoList.stream().collect(Collectors.groupingBy(WaybillGoodDetailDto::getPurchaseId));
                //生成物流商驱动类、仓库驱动类
                ILogisticManager logisticManager = logisticFactory.getLogisticTypeFromCode(logisticOrderDto.getLogisticCode());
                //包装发货推送数据
                List<DepositoryPurchaseStatusMsgDto> depositoryPurchaseStatusMsgDtoList = new ArrayList<>();
                waybillGoodDetailDtoMap.forEach((purchaseId, tempWaybillGoodDetailDtoList) -> {
                    DepositoryPurchaseStatusMsgDto depositoryPurchaseStatusMsgDto = new DepositoryPurchaseStatusMsgDto();
                    depositoryPurchaseStatusMsgDto.setPurchaseId(purchaseId);
                    depositoryPurchaseStatusMsgDto.setItemId(tempWaybillGoodDetailDtoList.get(0).getItemId());
                    depositoryPurchaseStatusMsgDto.setSalesOrderId(logisticOrderDto.getSalesOrderId());
                    depositoryPurchaseStatusMsgDtoList.add(depositoryPurchaseStatusMsgDto);
                });

                //发货单数据修改操作
                LogisticsPackageDO logisticsPackageDO = new LogisticsPackageDO();
                logisticsPackageDO.setId(logisticOrderDto.getLogisticsPackageId());

                LogisticDispatchResponseDto logisticDispatchResponseDto = new LogisticDispatchResponseDto();
                logisticDispatchResponseDto.setIsSuccess(Boolean.FALSE);
                logisticDispatchResponseDto.setStatusCode(DepositoryPurchaseStatusEnum.PREPARATION.getStatusCode());
                logisticDispatchResponseDtoList.add(logisticDispatchResponseDto);
                log.debug(" this.createLogisticOrder 开始时间： {}", LocalDateTime.now());
                long start = System.currentTimeMillis();
                //step 1. 创建物流单
                LogisticOrderResponseDto logisticOrderResponseDto = this.createLogisticOrder(logisticManager, logisticOrderDto, logisticDispatchResponseDto, logisticsPackageDO, depositoryPurchaseStatusMsgDtoList);
                long end1 = System.currentTimeMillis();
                log.debug(" this.createLogisticOrder 结束时间： {}，花费时间： {}", LocalDateTime.now(), end1 - start);
                log.debug(" this.queryLogisticLabel 开始时间： {}", LocalDateTime.now());
                //step 2. 查询面单
                LogisticPrintLabelResponseDto logisticPrintLabelResponseDto = this.queryLogisticLabel(logisticManager, logisticOrderResponseDto, logisticDispatchResponseDto);
                long end2 = System.currentTimeMillis();
                log.debug(" this.queryLogisticLabel 结束时间： {}， 花费时间： {}", LocalDateTime.now(), end2 - end1);
                log.debug(" this.pushOutDepository 开始时间： {}", LocalDateTime.now());
                //step 3. 推送包裹单出库
                this.pushOutDepository(logisticOrderDto, logisticOrderResponseDto, logisticPrintLabelResponseDto, logisticsPackageDO, logisticDispatchResponseDto, depositoryPurchaseStatusMsgDtoList);
                long end3 = System.currentTimeMillis();
                log.debug(" this.pushOutDepository 结束时间： {} ,花费时间： {}", LocalDateTime.now(), end3 - end2);
                // 添加消息，设置以前的单子为无效
                LogisticsPackageValidEvent event = new LogisticsPackageValidEvent(logisticOrderDto);
                applicationContext.publishEvent(event);
            } finally {
                removeOutDepositoryLimit(logisticOrderDto);
            }
        });
        log.debug(" doOutDepository 结束时间： {}", LocalDateTime.now(), System.currentTimeMillis() - start_1);
        return logisticDispatchResponseDtoList;
    }


    @Override
    public List<LogisticDispatchResponseDto> voucherPrepared(List<LogisticOrderDto> logisticOrderDtoList) {
        //设置物流单号数据
        logisticOrderDtoList.forEach(logisticOrderDto -> {
            logisticOrderDto.setLogisticOrderNo(logisticsCommonUtils.generateDeliveryPackageNo());
            // 对地址 stree1 = stree1+stree2
            logisticOrderDto.getReceiverAddress().setStreet1(StringUtils.substring(logisticOrderDto.getReceiverAddress().getStreet1() + (StringUtils.isNotBlank(logisticOrderDto.getReceiverAddress().getStreet2()) ? logisticOrderDto.getReceiverAddress().getStreet2() : ""), 0, 190));
        });

        //保留到数据库
        this.doSaveLogisticsPackageData(logisticOrderDtoList);

        //创建物流单信息
        List<LogisticDispatchResponseDto> logisticDispatchResponseDtoList = new ArrayList<>();
        logisticOrderDtoList.forEach(logisticOrderDto -> {
            List<WaybillGoodDetailDto> waybillGoodDetailDtoList = logisticOrderDto.getWaybillGoodDetailDtos();
            Map<String, List<WaybillGoodDetailDto>> waybillGoodDetailDtoMap = waybillGoodDetailDtoList.stream().collect(Collectors.groupingBy(WaybillGoodDetailDto::getPurchaseId));

            //生成物流商驱动类、仓库驱动类
            ILogisticManager logisticManager = logisticFactory.getLogisticTypeFromCode(logisticOrderDto.getLogisticCode());

            //包装发货推送数据
            List<DepositoryPurchaseStatusMsgDto> depositoryPurchaseStatusMsgDtoList = new ArrayList<>();
            waybillGoodDetailDtoMap.forEach((purchaseId, tempWaybillGoodDetailDtoList) -> {
                DepositoryPurchaseStatusMsgDto depositoryPurchaseStatusMsgDto = new DepositoryPurchaseStatusMsgDto();
                depositoryPurchaseStatusMsgDto.setPurchaseId(purchaseId);
                depositoryPurchaseStatusMsgDto.setSalesOrderId(logisticOrderDto.getSalesOrderId());
                depositoryPurchaseStatusMsgDto.setItemId(tempWaybillGoodDetailDtoList.get(0).getItemId());
                depositoryPurchaseStatusMsgDtoList.add(depositoryPurchaseStatusMsgDto);
            });

            //发货单数据修改操作
            LogisticsPackageDO logisticsPackageDO = new LogisticsPackageDO();
            logisticsPackageDO.setId(logisticOrderDto.getLogisticsPackageId());

            LogisticDispatchResponseDto logisticDispatchResponseDto = new LogisticDispatchResponseDto();
            logisticDispatchResponseDto.setIsSuccess(Boolean.FALSE);
            logisticDispatchResponseDto.setStatusCode(DepositoryPurchaseStatusEnum.PREPARATION.getStatusCode());
            logisticDispatchResponseDtoList.add(logisticDispatchResponseDto);

            //step1. 创建物流单信息
            LogisticOrderResponseDto logisticOrderResponseDto = logisticManager.addLogisticOrder(logisticOrderDto);
            if (logisticOrderResponseDto.isSuccessSign())        //制单成功
            {
                //修改发货包裹单的状态为  已发货
                logisticsPackageDO.setLogisticsStatus(DeliveryPackageLogisticsStatusEnum.DELIVERED.getCode());
                logisticsPackageDO.setLogisticsTrackingCode(logisticOrderResponseDto.getTrackingNumber());      //包裹物流跟踪单号
                logisticsPackageDO.setLogisticsChannelTrackingCode(logisticOrderResponseDto.getChannelTrackingNum());      //包裹物流渠道跟踪单号
                logisticsPackageDO.setLogisticsProcessCode(logisticOrderResponseDto.getProcessCode());
                logisticsPackageDO.setLogisticsIdnexNumber(logisticOrderResponseDto.getIndexNumber());
                logisticsPackageDO.setLogisticsWayBillNo(logisticOrderResponseDto.getWaybillNo());
                logisticsPackageDO.setDeliveryStatus(DeliveryPackageDeliveryStatusEnum.SENDED.getCode());
//                logisticsPackageDO.setReceivingGoodTime(new Date());
                logisticsPackageDO.setDeliveryTime(new Date());
                logisticsPackageMapper.updateById(logisticsPackageDO);

                //已发货
                sendMsgUtils.sendPurchaseIntoDepositoryMsg(depositoryPurchaseStatusMsgDtoList, DepositoryPurchaseStatusEnum.SENDED.getStatusCode());       //发货中

                //返回数据
                logisticDispatchResponseDto.setLogisticsOrderNo(logisticOrderResponseDto.getLogisticsOrderNo());
                logisticDispatchResponseDto.setTrackingNumber(logisticOrderResponseDto.getTrackingNumber());
                logisticDispatchResponseDto.setWaybillNo(logisticOrderResponseDto.getWaybillNo());
                logisticDispatchResponseDto.setIsSuccess(Boolean.TRUE);
                logisticDispatchResponseDto.setStatusCode(DepositoryPurchaseStatusEnum.SENDED.getStatusCode());

            } else      //制单失败
            {
                logisticsPackageDO.setErrorInfo(logisticOrderResponseDto.getMessage() + StringUtils.substring(logisticOrderResponseDto.getError(), 0, 150));       //异常信息
                logisticsPackageDO.setLogisticsStatus(DeliveryPackageLogisticsStatusEnum.ERROR.getCode());      //设置为异常状态
                logisticsPackageDO.setDeliveryStatus(DeliveryPackageDeliveryStatusEnum.PREPARATIONFAIL.getCode());
//                logisticsPackageDO.setIsDeleted(Constant.YES);
//                logisticsPackageDO.setIsValid(Constant.NO_0);
                logisticsPackageMapper.updateById(logisticsPackageDO);      // 修改当前物流单

                logisticDispatchResponseDto.setStatusCode(DepositoryPurchaseStatusEnum.PREPARATIONFAIL.getStatusCode());
                logisticDispatchResponseDto.setErrorMsg(logisticOrderResponseDto.getMessage());
                sendMsgUtils.sendPurchaseIntoDepositoryMsg(depositoryPurchaseStatusMsgDtoList, DepositoryPurchaseStatusEnum.PREPARATIONFAIL.getStatusCode());       //制单失败
            }
        });
        return logisticDispatchResponseDtoList;
    }

    //创建物流单
    private LogisticOrderResponseDto createLogisticOrder(ILogisticManager logisticManager, LogisticOrderDto logisticOrderDto, LogisticDispatchResponseDto logisticDispatchResponseDto, LogisticsPackageDO logisticsPackageDO, List<DepositoryPurchaseStatusMsgDto> depositoryPurchaseStatusMsgDtoList) {
        LogisticOrderResponseDto logisticOrderResponseDto = null;
        try {
            logisticOrderResponseDto = logisticManager.addLogisticOrder(logisticOrderDto);
        } catch (Exception e) {
            log.error("创建物流单失败：失败原因是：{}", e);
            logisticOrderResponseDto = new LogisticOrderResponseDto();
            logisticOrderResponseDto.setSuccessSign(false);
            logisticOrderResponseDto.setMessage(StringUtils.substring(e.getMessage(), 0, 200));
        }
        if (!logisticOrderResponseDto.isSuccessSign())        //制单失败
        {
            logisticsPackageDO.setErrorInfo(logisticOrderResponseDto.getMessage() + (StringUtils.isBlank(logisticDispatchResponseDto.getErrorMsg()) ? "" : logisticDispatchResponseDto.getErrorMsg()));       //异常信息
            logisticsPackageDO.setLogisticsStatus(DeliveryPackageLogisticsStatusEnum.ERROR.getCode());      //设置为异常状态
            logisticsPackageDO.setDeliveryStatus(DeliveryPackageDeliveryStatusEnum.PREPARATIONFAIL.getCode());
//            logisticsPackageDO.setIsDeleted(Constant.YES);
//            logisticsPackageDO.setIsValid(Constant.NO_0);
            logisticsPackageMapper.updateById(logisticsPackageDO);      // 修改当前物流单

            logisticDispatchResponseDto.setStatusCode(DepositoryPurchaseStatusEnum.PREPARATIONFAIL.getStatusCode());
            logisticDispatchResponseDto.setErrorMsg(logisticOrderResponseDto.getMessage());
            sendMsgUtils.sendPurchaseIntoDepositoryMsg(depositoryPurchaseStatusMsgDtoList, DepositoryPurchaseStatusEnum.PREPARATIONFAIL.getStatusCode());       //制单失败
            return null;
        }

        //修改发货包裹单的状态为  已创建物流单
        logisticsPackageDO.setLogisticsStatus(DeliveryPackageLogisticsStatusEnum.CREATED.getCode());        //包裹状态
        logisticsPackageDO.setLogisticsTrackingCode(logisticOrderResponseDto.getTrackingNumber());      //包裹物流跟踪单号
        logisticsPackageDO.setLogisticsChannelTrackingCode(logisticOrderResponseDto.getChannelTrackingNum());      //包裹物流渠道跟踪单号
        logisticsPackageDO.setLogisticsProcessCode(logisticOrderResponseDto.getProcessCode());
        logisticsPackageDO.setLogisticsIdnexNumber(logisticOrderResponseDto.getIndexNumber());
        logisticsPackageDO.setLogisticsWayBillNo(logisticOrderResponseDto.getWaybillNo());
        logisticsPackageDO.setDeliveryStatus(DeliveryPackageDeliveryStatusEnum.PREPARATION.getCode());
        logisticsPackageMapper.updateById(logisticsPackageDO);

        //已制单
        sendMsgUtils.sendPurchaseIntoDepositoryMsg(depositoryPurchaseStatusMsgDtoList, DepositoryPurchaseStatusEnum.PREPARATION.getStatusCode());       //已制单
        return logisticOrderResponseDto;
    }

    //查询物流面单
    private LogisticPrintLabelResponseDto queryLogisticLabel(ILogisticManager logisticManager, LogisticOrderResponseDto logisticOrderResponseDto, LogisticDispatchResponseDto logisticDispatchResponseDto) {
        if (logisticOrderResponseDto == null) return null;        //制单失败

        LogisticOrderSearchDto logisticOrderSearchDto = new LogisticOrderSearchDto();
        logisticOrderSearchDto.setLogisticsOrderNo(logisticOrderResponseDto.getLogisticsOrderNo());
        logisticOrderSearchDto.setOrderNo(logisticOrderResponseDto.getProcessCode());
        logisticOrderSearchDto.setWayBillNo(logisticOrderResponseDto.getWaybillNo());
        logisticOrderSearchDto.setTrackingNumber(logisticOrderResponseDto.getTrackingNumber());
        LogisticPrintLabelResponseDto logisticPrintLabelResponseDto = null;
        try {
            logisticPrintLabelResponseDto = logisticManager.getLogisticPrintLabel(logisticOrderSearchDto);
        } catch (Exception e) {
            log.error("获取面单失败：失败原因是：{}", e);
            logisticPrintLabelResponseDto = new LogisticPrintLabelResponseDto();
            logisticPrintLabelResponseDto.setSuccessSign(false);
            logisticPrintLabelResponseDto.setMessage(StringUtils.substring(e.getMessage(), 0, 200));
        }
        if (!logisticPrintLabelResponseDto.isSuccessSign()) {
            logisticDispatchResponseDto.setErrorMsg(logisticPrintLabelResponseDto.getMessage());
            // 插入错误原因
            LambdaUpdateWrapper<LogisticsPackageDO> updateWrapper = Wrappers.<LogisticsPackageDO>lambdaUpdate()
                    .set(LogisticsPackageDO::getDeliveryStatus, DeliveryPackageDeliveryStatusEnum.PREPARATIONFAIL.getCode())
                    .set(LogisticsPackageDO::getErrorInfo, logisticPrintLabelResponseDto.getMessage())
                    .eq(LogisticsPackageDO::getLogisticsOrderNo, logisticDispatchResponseDto.getLogisticsOrderNo());
            logisticsPackageMapper.update(null, updateWrapper);
            return null;
        }

        return logisticPrintLabelResponseDto;
    }

    //推送出库操作
    private void pushOutDepository(LogisticOrderDto logisticOrderDto, LogisticOrderResponseDto logisticOrderResponseDto,
                                   LogisticPrintLabelResponseDto logisticPrintLabelResponseDto, LogisticsPackageDO logisticsPackageDO,
                                   LogisticDispatchResponseDto logisticDispatchResponseDto, List<DepositoryPurchaseStatusMsgDto> depositoryPurchaseStatusMsgDtoList) {
        if (logisticPrintLabelResponseDto == null) return;     //查询面单失败
        IDepositoryManager depositoryManager = null;
        if (logisticOrderDto.getStorageCode() != null && logisticOrderDto.getStorageCode() == DepositoryTypeEnum.TEST.getId()) {
            log.info("测试出库进入一条数据", JSONObject.toJSONString(logisticOrderDto));
            depositoryManager = depositoryFactory.createDepositoryManager(DepositoryTypeEnum.TEST.getCode());
        } else {
            depositoryManager = depositoryFactory.createDepositoryManager(DepositoryTypeEnum.WANB.getCode());
        }
        OutDepositoryOrderDto outDepositoryOrderDto = this.getOutDepositoryOrderDto(logisticOrderDto, logisticOrderResponseDto, logisticPrintLabelResponseDto);
        OutDepositoryResultDto outDepositoryResultDto = null;
        try {
            outDepositoryResultDto = depositoryManager.addOutDepositoryOrder(outDepositoryOrderDto);
        } catch (Exception e) {
            log.error("推送出库失败：失败原因是：{}", e);
            outDepositoryResultDto = new OutDepositoryResultDto();
            outDepositoryResultDto.setSuccessSign(false);
            outDepositoryResultDto.setErrorMsg(StringUtils.substring(e.getMessage(), 0, 200));
        }
        // 修改跟踪号，因为各个物流公司的跟踪号不一样4px（查询面单返回的），万邦（跟踪号），燕文、云图（快递单号）
//        logisticsPackageDO.setLogisticsTrackingCode(outDepositoryOrderDto.getExpress().getTrackingNumber());
        logisticsPackageDO.setLogisticsLabelUrl(logisticPrintLabelResponseDto.getLogisticsLabel());
        if (!outDepositoryResultDto.isSuccessSign()) {
            logisticsPackageDO.setErrorInfo(outDepositoryResultDto.getErrorMsg());       //异常信息
            logisticsPackageDO.setDeliveryStatus(DeliveryPackageDeliveryStatusEnum.FAILPUSH.getCode());
//            logisticsPackageDO.setIsValid(Constant.NO_0);
            logisticsPackageMapper.updateById(logisticsPackageDO);      // 修改当前物流单
            logisticDispatchResponseDto.setStatusCode(DepositoryPurchaseStatusEnum.FAILPUSH.getStatusCode());
            logisticDispatchResponseDto.setErrorMsg(outDepositoryResultDto.getErrorMsg());
            sendMsgUtils.sendPurchaseIntoDepositoryMsg(depositoryPurchaseStatusMsgDtoList, DepositoryPurchaseStatusEnum.FAILPUSH.getStatusCode());       //发货失败
            return;
        }

        //修改发货包裹单的状态为   发货中
//        logisticsPackageDO.setLogisticsStatus(DeliveryPackageLogisticsStatusEnum.INDELIVERY.getCode());
        logisticsPackageDO.setLogisticsStatus(DeliveryPackageLogisticsStatusEnum.CREATED.getCode());
        logisticsPackageDO.setDeliveryStatus(DeliveryPackageDeliveryStatusEnum.SENDING.getCode());
        logisticsPackageDO.setErrorInfo(StringUtils.isNotBlank(logisticsPackageDO.getErrorInfo()) ? "【解决】" + logisticsPackageDO.getErrorInfo() : null);
        logisticsPackageMapper.updateById(logisticsPackageDO);

        //发货中
        sendMsgUtils.sendPurchaseIntoDepositoryMsg(depositoryPurchaseStatusMsgDtoList, DepositoryPurchaseStatusEnum.SENDING.getStatusCode());       //发货中

        //返回数据
        logisticDispatchResponseDto.setDispatchOrderId(outDepositoryResultDto.getRefId());
        logisticDispatchResponseDto.setLogisticsOrderNo(logisticOrderResponseDto.getLogisticsOrderNo());
        logisticDispatchResponseDto.setTrackingNumber(logisticOrderResponseDto.getTrackingNumber());
        logisticDispatchResponseDto.setWaybillNo(logisticOrderResponseDto.getWaybillNo());
        logisticDispatchResponseDto.setIsSuccess(Boolean.TRUE);
        logisticDispatchResponseDto.setStatusCode(DepositoryPurchaseStatusEnum.SENDING.getStatusCode());
    }


    private void logisticsPackageLabel(List<LogisticOrderDto> logisticOrderDtoList) {
        //step 1. 查询参数物流包裹单商品 类目的父级类目
        //step 2. 查询参数物流包裹单商品所关联的 标签数据
        //stpe 3. 查询参数物流包裹单商品类目（包含父级类目）所关联的 标签数据
        //step 4. 组装商品所关联的 labelId、商品类目（包含父级类目）所关联的 labelId、商品类目（包含父级类目）categoryCode
        //step 5. 设置带电属性

        //step 1
        Set<String> categoryCodeSet = new HashSet<>();
        Set<String> skuSet = new HashSet<>();
        Map<String, List<String>> categoryMetaIdMap = new HashMap<>();        //当前物流单下所有的类目(父级类目)
        logisticOrderDtoList.forEach(logisticOrderDto -> {
            //把当前物流单的商品 类目全部都组合起来
            List<WaybillGoodDetailDto> waybillGoodDetailDtoList = logisticOrderDto.getWaybillGoodDetailDtos();
            if (waybillGoodDetailDtoList == null || waybillGoodDetailDtoList.size() == 0) {
                throw new GlobalException("100400", "订单请求内没有商品信息");
            }
            Set<String> tempCategoryCodeSet = waybillGoodDetailDtoList.stream().map(WaybillGoodDetailDto::getCategoryCode).collect(Collectors.toSet());
            Set<String> tempSkuSet = waybillGoodDetailDtoList.stream().map(WaybillGoodDetailDto::getGoodsId).collect(Collectors.toSet());
            if (tempCategoryCodeSet.size() != 0) {
                log.info("查询当前物流单商品类目的父节点（包含当前节点） 参数：{}", JSON.toJSONString(tempCategoryCodeSet));
                //查询当前物流单商品类目的父节点（包含当前节点）
                List<MetadatapbMetadata.CategoryMeta> categoryMetaList = metadataPlusUtils.getCategoryParentList(new ArrayList<>(tempCategoryCodeSet));
                String s = GrpcJsonFormatUtils.toJsonString(categoryMetaList);
                String categoryCodeParent = null;
                for (WaybillGoodDetailDto waybillGoodDetailDto : waybillGoodDetailDtoList) {
                    categoryCodeParent = waybillGoodDetailDto.getCategoryCode();
                    Set<String> strings = new HashSet<>();
                    for (MetadatapbMetadata.CategoryMeta categoryMeta : categoryMetaList) {
                        String id = categoryMeta.getId();
                        if (categoryCodeParent != null && categoryCodeParent.equals(id)) {
                            strings.add(id);
                            if (StringUtils.isNotEmpty(categoryMeta.getParentId())) {
                                strings.add(categoryMeta.getParentId());
                                categoryCodeParent = categoryMeta.getParentId();
                            }
                        }
                    }
                    log.info("匹配的商品对应标签信息结果参数为：{}", JSON.toJSONString(strings));
                    List<Map<String, Object>> maps = logisticsLabelCategoryMapper.queryLogisticsLabelCategoryList(new ArrayList<>(strings));
                    log.info("匹配的商品对应标签信息结果结果为：{}", JSON.toJSONString(maps));
                    List<Long> labelId = maps.stream().map(logisticsLabelProductItem -> Long.valueOf(logisticsLabelProductItem.get("labelId").toString())).collect(Collectors.toList());
                    waybillGoodDetailDto.setRelevanceLabelIdList(labelId);

                }


                log.info("查询当前物流单商品类目的父节点（包含当前节点）：{}", s);
                List<String> categoryMetaIdList = categoryMetaList.stream().map(MetadatapbMetadata.CategoryMeta::getId).collect(Collectors.toList());

                //以物流单号分组
                categoryMetaIdMap.put(logisticOrderDto.getLogisticOrderNo(), categoryMetaIdList);
                categoryCodeSet.addAll(categoryMetaIdList);     //把所有的父级类目也设置到一起
            }

            //把所有的类目、sku 聚合到一起，下一步去查询数据库，判断是否已经配置类目、sku
            categoryCodeSet.addAll(tempCategoryCodeSet);
            skuSet.addAll(tempSkuSet);
        });


        //商品关联标签数据
        List<Map<String, Object>> logisticsLabelProductList = new ArrayList<>();
        Map<String, List<Map<String, Object>>> logisticsLabelProductMap = new HashMap<>();

        //step 2
        //标签关联类目数据
        List<Map<String, Object>> logisticsLabelCategoryList = new ArrayList<>();
        if (skuSet.size() != 0) {
            logisticsLabelProductList.addAll(logisticsLabelProductMapper.queryLogisticsLabelProductListBySkuList(new ArrayList<>(skuSet)));
            logisticsLabelProductMap.putAll(logisticsLabelProductList.stream().collect(Collectors.groupingBy(map -> map.get("sku").toString())));
        }

        //step 3
        if (categoryCodeSet.size() != 0) {
            logisticsLabelCategoryList.addAll(logisticsLabelCategoryMapper.queryLogisticsLabelCategoryList(new ArrayList<>(categoryCodeSet)));
        }

        logisticOrderDtoList.forEach(logisticOrderDto -> {
            AtomicReference<Boolean> atomicReferenceBatteryFlag = new AtomicReference<>(true);
            List<WaybillGoodDetailDto> waybillGoodDetailDtoList = logisticOrderDto.getWaybillGoodDetailDtos();
            waybillGoodDetailDtoList.forEach(waybillGoodDetailDto -> {
                String sku = waybillGoodDetailDto.getGoodsId();
                Set<Long> labelIdSet = new HashSet<>(Optional.ofNullable(waybillGoodDetailDto.getLabelIdList()).orElse(new ArrayList<>()));
                Set<String> labelNameSet = new HashSet<>();
                List<Map<String, Object>> productList = Optional.ofNullable(logisticsLabelProductMap.get(sku)).orElse(new ArrayList<>());       //商品数据
                List<String> categoryMetaIdList = Optional.ofNullable(categoryMetaIdMap.get(logisticOrderDto.getLogisticOrderNo())).orElse(new ArrayList<>());        //当前物流单所包含的类目，包含父级类目
                //step 4
                //过滤 labelId 数据
                List<Long> productLabelIdList = productList.stream().map(map -> Long.valueOf(map.get("labelId").toString())).collect(Collectors.toList());
                List<Long> categoryLabelIdList = logisticsLabelCategoryList.stream().filter(logisticsLabelCategory -> categoryMetaIdList.contains(logisticsLabelCategory.get("categoryCode").toString())).map(logisticsLabelCategory -> Long.valueOf(logisticsLabelCategory.get("labelId").toString())).collect(Collectors.toList());
                labelIdSet.addAll(productLabelIdList);     //商品关联的 标签 id
                labelIdSet.addAll(categoryLabelIdList);    //类目关联的 标签 id

                //设置该商品下所有的 类目id、标签 id
                waybillGoodDetailDto.setCategoryCodeList(new ArrayList<>(categoryMetaIdList));
                waybillGoodDetailDto.setLabelIdList(new ArrayList<>(labelIdSet));

                //过滤 labelName 数据
                List<String> productLabelNameList = logisticsLabelProductList.stream().filter(logisticsLabelProductItem -> labelIdSet.contains(Long.valueOf(logisticsLabelProductItem.get("labelId").toString()))).map(logisticsLabelProductItem -> logisticsLabelProductItem.get("labelName").toString()).collect(Collectors.toList());
                List<String> categoryLabelNameList = logisticsLabelCategoryList.stream().filter(logisticsLabelProductItem -> labelIdSet.contains(Long.valueOf(logisticsLabelProductItem.get("labelId").toString()))).map(logisticsLabelProductItem -> logisticsLabelProductItem.get("labelName").toString()).collect(Collectors.toList());
                labelNameSet.addAll(productLabelNameList);
                labelNameSet.addAll(categoryLabelNameList);

                //step 5
                //设置带电数据
                if (atomicReferenceBatteryFlag.get()) {
                    Boolean tempBatteryFlag = this.batteryProperty(logisticOrderDto, labelNameSet);
                    if (tempBatteryFlag) atomicReferenceBatteryFlag.set(false);        //不再设置带电属性
                }
            });
        });
    }

    private Boolean doSaveLogisticsPackageData(List<LogisticOrderDto> logisticOrderDtoList) {
        List<LogisticsPackageDO> logisticsPackageDOList = new ArrayList<>();
        List<LogisticsProductDO> logisticsProductDOList = new ArrayList<>();
        List<LogisticsPackageAddressDO> logisticsPackageAddressDOList = new ArrayList<>();
        List<LogisticsPackageItemDO> logisticsPackageItemDOList = new ArrayList<>();
        logisticOrderDtoList.forEach(logisticOrderDto -> {
            logisticOrderDto.setLogisticsPackageId(SnowflakeIdUtils.getId());
            LogisticTypeEnum logisticTypeEnum = LogisticTypeEnum.getLogisticTypeEnumByCode(logisticOrderDto.getLogisticCode());
            LogisticsPackageDO logisticsPackageDO = this.getLogisticsPackage(logisticOrderDto, logisticTypeEnum);

            //组装数据，以 sku 分组，把 itemId 和 purchaseId 组合到一起
            List<WaybillGoodDetailDto> waybillGoodDetailDtoList = logisticOrderDto.getWaybillGoodDetailDtos();
            Map<String, Map> logisticsProductItemMap = this.getLogisticsProductItemMap(waybillGoodDetailDtoList);
            Set<String> skuSet = new HashSet<>();
            for (WaybillGoodDetailDto waybillGoodDetailDto : waybillGoodDetailDtoList) {
                if (skuSet.contains(waybillGoodDetailDto.getGoodsId())) {      //以 sku 分组，如果重复，则直接忽略
                    continue;
                }
                skuSet.add(waybillGoodDetailDto.getGoodsId());
                //组装发货包裹商品 itemId 数据
                logisticsPackageItemDOList.addAll(this.getLogisticsPackageItemList(logisticsProductItemMap, waybillGoodDetailDto, logisticsPackageDO.getId(), logisticsPackageDO.getCreateBy()));

                //组装发货包裹商品数据
                logisticsProductDOList.add(this.getLogisticsProduct(logisticsProductItemMap, logisticsPackageDO, waybillGoodDetailDto));
            }

            //组装发货地址数据
            logisticsPackageAddressDOList.add(this.getLogisticsPackageAddress(logisticsPackageDO.getId(), logisticOrderDto.getReceiverAddress()));

            //组装发货包裹数据
            logisticsPackageDOList.add(logisticsPackageDO);
        });

        //添加发货单数据数据库
        logisticsPackageMapper.insertLogisticsPackageBatch(logisticsPackageDOList);     //添加发货包裹数据
        logisticsProductMapper.insertLogisticsProductBatch(logisticsProductDOList);     //添加发货包裹商品数据
        logisticsPackageItemMapper.insertLogisticsPackageItemBatch(logisticsPackageItemDOList);     //添加发货包裹关联数据
        logisticsPackageAddressMapper.insertLogisticsPackageAddressBatch(logisticsPackageAddressDOList);        //添加发货包裹单地址数据
        return true;
    }

    private LogisticsPackageAddressDO getLogisticsPackageAddress(Long packageId, AddressDto addressDto) {
        LogisticsPackageAddressDO logisticsPackageAddressDO = new LogisticsPackageAddressDO();
        logisticsPackageAddressDO.setPackageId(packageId);
        logisticsPackageAddressDO.setFirstName(addressDto.getFirstName());
        logisticsPackageAddressDO.setLastName(addressDto.getLastName());
        logisticsPackageAddressDO.setCompany(addressDto.getCompany());
        logisticsPackageAddressDO.setCountryCode(addressDto.getCountryCode());
        logisticsPackageAddressDO.setProvince(addressDto.getProvince());
        logisticsPackageAddressDO.setCity(addressDto.getCity());
        logisticsPackageAddressDO.setStreet1(addressDto.getStreet1());
        logisticsPackageAddressDO.setStreet2(addressDto.getStreet2());
        logisticsPackageAddressDO.setPostCode(addressDto.getPostCode());
        logisticsPackageAddressDO.setTel(addressDto.getTel());
        logisticsPackageAddressDO.setEmail(addressDto.getEmail());
        logisticsPackageAddressDO.setTaxNumber(addressDto.getTaxNumber());

        return logisticsPackageAddressDO;
    }


    private LogisticsPackageDO getLogisticsPackage(LogisticOrderDto logisticOrderDto, LogisticTypeEnum
            logisticTypeEnum) {
        AddressDto addressDto = logisticOrderDto.getReceiverAddress();
        List<WaybillGoodDetailDto> waybillGoodDetailDtoList = logisticOrderDto.getWaybillGoodDetailDtos();

        //组装发货包裹单数据
        LogisticsPackageDO logisticsPackageDO = new LogisticsPackageDO();
        logisticsPackageDO.setId(logisticOrderDto.getLogisticsPackageId());
        logisticsPackageDO.setType(DeliveryPackageTypeEnum.ABROAD_DELIVERY_PACKAGE.getType());
        logisticsPackageDO.setPurchaseChannel(DeliveryPackagePurchaseChannelEnum.ALIBABA.getType());
        logisticsPackageDO.setSalesOrderId(logisticOrderDto.getSalesOrderId());
        logisticsPackageDO.setChannelOrderId(StringUtils.join(logisticOrderDto.getChannelOrderIdList(), ","));
        logisticsPackageDO.setDeliveryPackageType(logisticOrderDto.getPackageType());
        logisticsPackageDO.setLogisticsName(logisticTypeEnum.getName());
        logisticsPackageDO.setLogisticsCode(logisticOrderDto.getLogisticCode());
        logisticsPackageDO.setLogisticsChannel(logisticOrderDto.getChannel());
        logisticsPackageDO.setLogisticsOrderNo(logisticOrderDto.getLogisticOrderNo());
        logisticsPackageDO.setLogisticsStatus(DeliveryPackageLogisticsStatusEnum.INIT.getCode());       //包裹初始状态
        logisticsPackageDO.setLogisticsArea(addressDto.getCountryCode());
        logisticsPackageDO.setLogisticsWeight(waybillGoodDetailDtoList.stream().map(WaybillGoodDetailDto::getWeight).reduce(BigDecimal.ZERO, BigDecimal::add));
        logisticsPackageDO.setLogisticsRemark(logisticOrderDto.getRemark());
        logisticsPackageDO.setBattery(logisticOrderDto.getBattery());
        logisticsPackageDO.setBatteryType(logisticOrderDto.getBatteryType());
        logisticsPackageDO.setCreateBy(StringUtils.isBlank(logisticOrderDto.getOperationer()) ? Constant.SYSTEM_ROOT : logisticOrderDto.getOperationer());
        return logisticsPackageDO;
    }

    private Map<String, Map> getLogisticsProductItemMap(List<WaybillGoodDetailDto> waybillGoodDetailDtoList) {
        //组装数据，以 sku 分组，把 itemId 和 purchaseId 组合到一起
        Map<String, Map> map = new HashMap<>();
        waybillGoodDetailDtoList.forEach(waybillGoodDetailDto -> {
            Map tempMap = Optional.ofNullable(map.get(waybillGoodDetailDto.getGoodsId())).orElse(new HashMap<>());
            Integer nums = (Integer) Optional.ofNullable(tempMap.get("nums")).orElse(0);
            List<String> itemAndPurchaseIdList = (List<String>) Optional.ofNullable(tempMap.get("itemAndPurchaseIdList")).orElse(new ArrayList<>());

            itemAndPurchaseIdList.add(waybillGoodDetailDto.getItemId() + "," + waybillGoodDetailDto.getPurchaseId());

            tempMap.put("nums", nums + 1);
            tempMap.put("itemAndPurchaseIdList", itemAndPurchaseIdList);

            map.put(waybillGoodDetailDto.getGoodsId(), tempMap);
        });
        return map;
    }

    private List<LogisticsPackageItemDO> getLogisticsPackageItemList
            (Map<String, Map> logisticsProductItemMap, WaybillGoodDetailDto waybillGoodDetailDto, Long packageId, String
                    createBy) {
        List<LogisticsPackageItemDO> logisticsPackageItemDOList = new ArrayList<>();
        Map tempMap = logisticsProductItemMap.get(waybillGoodDetailDto.getGoodsId());
        List<String> itemAndPurchaseIdList = (List<String>) Optional.ofNullable(tempMap.get("itemAndPurchaseIdList")).orElse(new ArrayList<>());     //item id, 采购 id
        itemAndPurchaseIdList.forEach(itemAndPurchaseId -> {
            String[] itemAndPurchaseIdArr = itemAndPurchaseId.split(",");
            LogisticsPackageItemDO logisticsPackageItemDO = new LogisticsPackageItemDO();
            logisticsPackageItemDO.setPackageId(packageId);
            logisticsPackageItemDO.setSku(waybillGoodDetailDto.getGoodsId());
            logisticsPackageItemDO.setItemId(itemAndPurchaseIdArr[0]);
            logisticsPackageItemDO.setPurchaseId(itemAndPurchaseIdArr[1]);
            logisticsPackageItemDO.setCreateBy(createBy);
            logisticsPackageItemDOList.add(logisticsPackageItemDO);
        });
        return logisticsPackageItemDOList;
    }

    private LogisticsProductDO getLogisticsProduct(Map<String, Map> logisticsProductItemMap, LogisticsPackageDO
            logisticsPackageDO, WaybillGoodDetailDto waybillGoodDetailDto) {
        Map tempMap = logisticsProductItemMap.get(waybillGoodDetailDto.getGoodsId());
        Integer nums = (Integer) Optional.ofNullable(tempMap.get("nums")).orElse(0);

        //组装发货包裹商品数据
        LogisticsProductDO logisticsProductDO = new LogisticsProductDO();
        logisticsProductDO.setId(SnowflakeIdUtils.getId());
        logisticsProductDO.setType(PackageProductTypeEnum.DELIVERY_PACKAGE.getType());
        logisticsProductDO.setRelationId(logisticsPackageDO.getId());
        logisticsProductDO.setSku(waybillGoodDetailDto.getGoodsId());
        logisticsProductDO.setCategoryCode(waybillGoodDetailDto.getCategoryCode());
        logisticsProductDO.setProductName(waybillGoodDetailDto.getGoodsTitle());
        logisticsProductDO.setProductImg(waybillGoodDetailDto.getImg());
        logisticsProductDO.setProductAttribute(waybillGoodDetailDto.getAttribute());
        logisticsProductDO.setProductCount(nums);
        logisticsProductDO.setProductWeight(waybillGoodDetailDto.getWeight().toEngineeringString());
        logisticsProductDO.setProductDeclaredNameEn(waybillGoodDetailDto.getDeclaredNameEn());
        logisticsProductDO.setProductDeclaredNameCn(waybillGoodDetailDto.getDeclaredNameCn());
        logisticsProductDO.setProductDeclaredPrice(waybillGoodDetailDto.getDeclaredValue());
        logisticsProductDO.setProductSalePriceCny(waybillGoodDetailDto.getSalePriceCny());
        return logisticsProductDO;
    }

    private OutDepositoryOrderDto getOutDepositoryOrderDto(LogisticOrderDto
                                                                   logisticOrderDto, LogisticOrderResponseDto logisticOrderResponseDto, LogisticPrintLabelResponseDto
                                                                   logisticPrintLabelResponseDto) {
        OutDepositoryOrderDto outDepositoryOrderDto = new OutDepositoryOrderDto();
        outDepositoryOrderDto.setDispatchOrderId(logisticOrderResponseDto.getLogisticsOrderNo());
        outDepositoryOrderDto.setSalesOrderId(logisticOrderDto.getSalesOrderId());
        outDepositoryOrderDto.setAddress(logisticOrderDto.getReceiverAddress());
        outDepositoryOrderDto.setExpress(this.getOutDepositoryOrderExpressDto(logisticOrderDto, logisticOrderResponseDto, logisticPrintLabelResponseDto));
        outDepositoryOrderDto.setRemark(logisticOrderDto.getRemark());
        outDepositoryOrderDto.setOrderItems(this.getOutDepositoryOrderItemDto(logisticOrderDto));
        outDepositoryOrderDto.setEstimatedWeight(this.getOutDepositoryOrderWeight(logisticOrderDto).divide(new BigDecimal("1000")));
        return outDepositoryOrderDto;
    }

    private ExpressDto getOutDepositoryOrderExpressDto(LogisticOrderDto logisticOrderDto, LogisticOrderResponseDto
            logisticOrderResponseDto, LogisticPrintLabelResponseDto logisticPrintLabelResponseDto) {
        ExpressDto expressDto = new ExpressDto();
        if (LogisticTypeEnum.PX4.getCode().equals(logisticOrderDto.getLogisticCode())) {
            expressDto.setTrackingNumber(logisticPrintLabelResponseDto.getLabelBarcode());     //专门用于发货的
        } else if (LogisticTypeEnum.WANB.getCode().equals(logisticOrderDto.getLogisticCode())) {
            expressDto.setTrackingNumber(logisticOrderResponseDto.getWaybillNo());
        } else if ((LogisticTypeEnum.YUNTU.getCode().equals(logisticOrderDto.getLogisticCode()))) {
            expressDto.setTrackingNumber(logisticOrderResponseDto.getWaybillNo());
        } else {
            expressDto.setTrackingNumber(logisticOrderResponseDto.getTrackingNumber());
        }
        expressDto.setLabelUrl(logisticPrintLabelResponseDto.getLogisticsLabel());
        expressDto.setServiceId(WBDepositoryRegisterServiceConfig.getServicerIdFromCode(logisticOrderDto.getLogisticCode(), logisticOrderDto.getChannel()));
        return expressDto;
    }

    private List<OutDepositoryOrderItemDto> getOutDepositoryOrderItemDto(LogisticOrderDto logisticOrderDto) {
        Map<String, Integer> map = new HashMap<>();
        List<WaybillGoodDetailDto> waybillGoodDetailDtoList = logisticOrderDto.getWaybillGoodDetailDtos();
        waybillGoodDetailDtoList.forEach(waybillGoodDetailDto -> {
            Integer nums = Optional.ofNullable(map.get(waybillGoodDetailDto.getGoodsId())).orElse(0);
            map.put(waybillGoodDetailDto.getGoodsId(), nums + 1);
        });

        List<OutDepositoryOrderItemDto> outDepositoryOrderItemDtoList = new ArrayList<>();
        Set<Map.Entry<String, Integer>> set = map.entrySet();
        set.forEach(mapEntry -> {
            OutDepositoryOrderItemDto outDepositoryOrderItemDto = new OutDepositoryOrderItemDto();
            outDepositoryOrderItemDto.setSku(mapEntry.getKey());
            outDepositoryOrderItemDto.setQuantity(mapEntry.getValue());
            outDepositoryOrderItemDtoList.add(outDepositoryOrderItemDto);
        });
        return outDepositoryOrderItemDtoList;
    }

    private BigDecimal getOutDepositoryOrderWeight(LogisticOrderDto logisticOrderDto) {
        List<WaybillGoodDetailDto> waybillGoodDetailDtoList = logisticOrderDto.getWaybillGoodDetailDtos();
        return waybillGoodDetailDtoList.stream().map(WaybillGoodDetailDto::getWeight).reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    private Boolean batteryProperty(LogisticOrderDto logisticOrderDto, Set<String> labelNameSet) {
        if (labelNameSet.contains("带电")) {
            logisticOrderDto.setBattery(Constant.YES);
            logisticOrderDto.setBatteryType(2);
            return true;
        } else if (labelNameSet.contains("纯电池")) {
            logisticOrderDto.setBattery(Constant.YES);
            logisticOrderDto.setBatteryType(3);
            return true;
        }
        return false;
    }


    private void casecadeUpdateDepositorySaleOrder
            (Set<String> purchaseIdList, Map<String, List<LogisticsPackageItemDO>> mapByPurchaseId) {
        for (String purchaseId : purchaseIdList) {
            LogisticsPackageItemDO logisticsPackageItemDO = Optional.of(mapByPurchaseId.get(purchaseId)).orElse(Collections.singletonList(new LogisticsPackageItemDO())).get(0);
            if (logisticsPackageItemDO.getItemId() != null) {
                LambdaUpdateWrapper<DepositorySaleOrderDetailDO> updateWrapper = Wrappers.<DepositorySaleOrderDetailDO>lambdaUpdate()
                        .set(DepositorySaleOrderDetailDO::getLogisticsPackageId, logisticsPackageItemDO.getPackageId())
                        .eq(DepositorySaleOrderDetailDO::getItemId, logisticsPackageItemDO.getItemId());
//                        .eq(DepositorySaleOrderDetailDO::getPurchaseId, purchaseId);  目前不在提供PurchaseId 在销售订单中
                depositorySaleOrderDetailMapper.update(null, updateWrapper);
            }

        }
    }

    private boolean outDepositoryLimitTime(LogisticOrderDto logisticOrderDto) {
        String redisKey = logisticOrderDto.getWaybillGoodDetailDtos().stream().map(item -> item.getItemId()).collect(Collectors.joining(","));
        String hashCode = Objects.hashCode(redisKey) + "";
        log.info("防止短时间内多次人工出库,请求订单是：{}，加密值是：{}，Hash值是：{}", logisticOrderDto.getSalesOrderId(), redisKey, hashCode);
        return redissLockUtil.tryLock(hashCode, TimeUnit.SECONDS, -1, 180);
    }

    private void removeOutDepositoryLimit(LogisticOrderDto logisticOrderDto) {
        String redisKey = logisticOrderDto.getWaybillGoodDetailDtos().stream().map(item -> item.getItemId()).collect(Collectors.joining(","));
        String hashCode = Objects.hashCode(redisKey) + "";
        log.info("移除防止短时间内多次人工出库redis的key,请求订单是：{}，加密值是：{}，Hash值是：{}", logisticOrderDto.getSalesOrderId(), redisKey, hashCode);
        redissLockUtil.unlock(hashCode);
    }
}

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
        //???????????????
        this.initLogisticsPackage(logisticOrderDtoList, Boolean.FALSE);

        //??????????????????
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

        //????????????????????????????????????????????????
        if (logisticOrderDtoList.size() == 0) {
            log.info("???????????????????????????????????????????????????????????????????????????{}", JSON.toJSONString(logisticOrderDtoList));
            return;
        }

        //????????????
        this.doOutDepository(logisticOrderDtoList);
    }

    @Override
    public List<LogisticDispatchResponseDto> manualOutDepository(List<LogisticOrderDto> logisticOrderDtoList) {
        // ??????????????????
        Callable<List<LogisticDispatchResponseDto>> callable = () -> {
            long count = logisticOrderDtoList.stream().filter(item -> !outDepositoryLimitTime(item)).count();
            if (count > 0) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "??????????????????????????????????????????????????????????????????");
            }
            //???????????????
            this.initLogisticsPackage(logisticOrderDtoList, Boolean.FALSE);
            return this.doOutDepository(logisticOrderDtoList);
        };
        Future<List<LogisticDispatchResponseDto>> future = executorService.submit(callable);
        try {
            long start = System.currentTimeMillis();
            log.info(" ???????????? main??????????????????????????????{}", LocalDateTime.now());
            List<LogisticDispatchResponseDto> list = future.get(8, TimeUnit.SECONDS);
            log.info(" ???????????? main??????????????????????????? {}?????????????????????{}????????????????????????{}", LocalDateTime.now(), System.currentTimeMillis() - start, list);
            return list;
        } catch (TimeoutException e) {
            log.error("???????????????????????????????????????????????????{}", e);
            // ??????????????????????????????????????????????????????????????????????????????????????????
            throw new GlobalException("100200200", "???????????????????????????????????????????????????????????????,???????????????");
        } catch (InterruptedException e) {
            log.error("???????????????????????????????????????????????????{}", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, e.getMessage());
        } catch (ExecutionException e) {
            log.error("???????????????????????????????????????????????????{}", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, e.getMessage());
        } catch (Exception e) {
            log.error("???????????????????????????????????????????????????{}", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, e.getMessage());
        }
    }

    @Override
    public void initLogisticsPackage(List<LogisticOrderDto> logisticOrderDtoList) {
        this.initLogisticsPackage(logisticOrderDtoList, Boolean.TRUE);
    }

    private void initLogisticsPackage(List<LogisticOrderDto> logisticOrderDtoList, Boolean tempFlag) {
        //????????????????????????
        logisticOrderDtoList.forEach(logisticOrderDto -> {
            if (tempFlag)        //??????????????????
            {
                logisticOrderDto.setLogisticOrderNo("temp:" + SnowflakeIdUtils.getId());
                return;     //??????
            }
            //?????????????????????
            logisticOrderDto.setLogisticOrderNo(logisticsCommonUtils.generateDeliveryPackageNo());
        });
        //??????????????????
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
            if (DeliveryPackageLogisticsStatusEnum.INIT.getCode().equals(logisticsStatus)        //????????????
                    || DeliveryPackageLogisticsStatusEnum.CREATED.getCode().equals(logisticsStatus)     //??????????????????
//                    || DeliveryPackageLogisticsStatusEnum.INDELIVERY.getCode().equals(logisticsStatus)
                    || DeliveryPackageLogisticsStatusEnum.ERROR.getCode().equals(logisticsStatus)) {  //?????????????????????

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
                if (depositoryProcessMsgDto.isShipped()) {  //????????????????????????????????????????????????
                    tempLogisticsPackageDO.setLogisticsStatus(DeliveryPackageLogisticsStatusEnum.DELIVERED.getCode());      //???????????????
                    tempLogisticsPackageDO.setDeliveryStatus(DeliveryPackageDeliveryStatusEnum.SENDED.getCode());
//                    tempLogisticsPackageDO.setReceivingGoodTime(new Date());
                    tempLogisticsPackageDO.setDeliveryTime(new Date());
                } else {
                    if (StringUtils.isNoneBlank(depositoryProcessMsgDto.getFailureReason())) {
                        tempLogisticsPackageDO.setLogisticsStatus(DeliveryPackageLogisticsStatusEnum.ERROR.getCode());       //????????????
                        tempLogisticsPackageDO.setErrorInfo(depositoryProcessMsgDto.getFailureReason());     //??????????????????
//                        tempLogisticsPackageDO.setIsValid(Constant.NO_0);      //???????????????
                        tempLogisticsPackageDO.setDeliveryStatus(DeliveryPackageDeliveryStatusEnum.FAILSENDED.getCode());
                        depositoryPurchaseStatusEnum = DepositoryPurchaseStatusEnum.FAILSENDED;     //????????????
                    }
                }
                logisticsPackageMapper.updateById(tempLogisticsPackageDO);      //??????????????????
                casecadeUpdateDepositorySaleOrder(purchaseIdList, mapByPurchaseId);  // ??????????????????itemId?????????
                //??????????????????
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
        log.debug(" doOutDepository ??????????????? {}", LocalDateTime.now());
        //??????????????????
        // ????????? stree1 = stree1+stree2
        logisticOrderDtoList.stream().forEach(order ->{
            order.getReceiverAddress().setStreet1(StringUtils.substring(order.getReceiverAddress().getStreet1() + (StringUtils.isNotBlank(order.getReceiverAddress().getStreet2()) ? order.getReceiverAddress().getStreet2() : ""), 0, 190));
        });
        transactionTemplate.execute(status -> {
            return this.doSaveLogisticsPackageData(logisticOrderDtoList);
        });
        //?????????????????????
        List<LogisticDispatchResponseDto> logisticDispatchResponseDtoList = new ArrayList<>();
        logisticOrderDtoList.forEach(logisticOrderDto -> {
            try {
                List<WaybillGoodDetailDto> waybillGoodDetailDtoList = logisticOrderDto.getWaybillGoodDetailDtos();
                Map<String, List<WaybillGoodDetailDto>> waybillGoodDetailDtoMap = waybillGoodDetailDtoList.stream().collect(Collectors.groupingBy(WaybillGoodDetailDto::getPurchaseId));
                //??????????????????????????????????????????
                ILogisticManager logisticManager = logisticFactory.getLogisticTypeFromCode(logisticOrderDto.getLogisticCode());
                //????????????????????????
                List<DepositoryPurchaseStatusMsgDto> depositoryPurchaseStatusMsgDtoList = new ArrayList<>();
                waybillGoodDetailDtoMap.forEach((purchaseId, tempWaybillGoodDetailDtoList) -> {
                    DepositoryPurchaseStatusMsgDto depositoryPurchaseStatusMsgDto = new DepositoryPurchaseStatusMsgDto();
                    depositoryPurchaseStatusMsgDto.setPurchaseId(purchaseId);
                    depositoryPurchaseStatusMsgDto.setItemId(tempWaybillGoodDetailDtoList.get(0).getItemId());
                    depositoryPurchaseStatusMsgDto.setSalesOrderId(logisticOrderDto.getSalesOrderId());
                    depositoryPurchaseStatusMsgDtoList.add(depositoryPurchaseStatusMsgDto);
                });

                //???????????????????????????
                LogisticsPackageDO logisticsPackageDO = new LogisticsPackageDO();
                logisticsPackageDO.setId(logisticOrderDto.getLogisticsPackageId());

                LogisticDispatchResponseDto logisticDispatchResponseDto = new LogisticDispatchResponseDto();
                logisticDispatchResponseDto.setIsSuccess(Boolean.FALSE);
                logisticDispatchResponseDto.setStatusCode(DepositoryPurchaseStatusEnum.PREPARATION.getStatusCode());
                logisticDispatchResponseDtoList.add(logisticDispatchResponseDto);
                log.debug(" this.createLogisticOrder ??????????????? {}", LocalDateTime.now());
                long start = System.currentTimeMillis();
                //step 1. ???????????????
                LogisticOrderResponseDto logisticOrderResponseDto = this.createLogisticOrder(logisticManager, logisticOrderDto, logisticDispatchResponseDto, logisticsPackageDO, depositoryPurchaseStatusMsgDtoList);
                long end1 = System.currentTimeMillis();
                log.debug(" this.createLogisticOrder ??????????????? {}?????????????????? {}", LocalDateTime.now(), end1 - start);
                log.debug(" this.queryLogisticLabel ??????????????? {}", LocalDateTime.now());
                //step 2. ????????????
                LogisticPrintLabelResponseDto logisticPrintLabelResponseDto = this.queryLogisticLabel(logisticManager, logisticOrderResponseDto, logisticDispatchResponseDto);
                long end2 = System.currentTimeMillis();
                log.debug(" this.queryLogisticLabel ??????????????? {}??? ??????????????? {}", LocalDateTime.now(), end2 - end1);
                log.debug(" this.pushOutDepository ??????????????? {}", LocalDateTime.now());
                //step 3. ?????????????????????
                this.pushOutDepository(logisticOrderDto, logisticOrderResponseDto, logisticPrintLabelResponseDto, logisticsPackageDO, logisticDispatchResponseDto, depositoryPurchaseStatusMsgDtoList);
                long end3 = System.currentTimeMillis();
                log.debug(" this.pushOutDepository ??????????????? {} ,??????????????? {}", LocalDateTime.now(), end3 - end2);
                // ?????????????????????????????????????????????
                LogisticsPackageValidEvent event = new LogisticsPackageValidEvent(logisticOrderDto);
                applicationContext.publishEvent(event);
            } finally {
                removeOutDepositoryLimit(logisticOrderDto);
            }
        });
        log.debug(" doOutDepository ??????????????? {}", LocalDateTime.now(), System.currentTimeMillis() - start_1);
        return logisticDispatchResponseDtoList;
    }


    @Override
    public List<LogisticDispatchResponseDto> voucherPrepared(List<LogisticOrderDto> logisticOrderDtoList) {
        //????????????????????????
        logisticOrderDtoList.forEach(logisticOrderDto -> {
            logisticOrderDto.setLogisticOrderNo(logisticsCommonUtils.generateDeliveryPackageNo());
            // ????????? stree1 = stree1+stree2
            logisticOrderDto.getReceiverAddress().setStreet1(StringUtils.substring(logisticOrderDto.getReceiverAddress().getStreet1() + (StringUtils.isNotBlank(logisticOrderDto.getReceiverAddress().getStreet2()) ? logisticOrderDto.getReceiverAddress().getStreet2() : ""), 0, 190));
        });

        //??????????????????
        this.doSaveLogisticsPackageData(logisticOrderDtoList);

        //?????????????????????
        List<LogisticDispatchResponseDto> logisticDispatchResponseDtoList = new ArrayList<>();
        logisticOrderDtoList.forEach(logisticOrderDto -> {
            List<WaybillGoodDetailDto> waybillGoodDetailDtoList = logisticOrderDto.getWaybillGoodDetailDtos();
            Map<String, List<WaybillGoodDetailDto>> waybillGoodDetailDtoMap = waybillGoodDetailDtoList.stream().collect(Collectors.groupingBy(WaybillGoodDetailDto::getPurchaseId));

            //??????????????????????????????????????????
            ILogisticManager logisticManager = logisticFactory.getLogisticTypeFromCode(logisticOrderDto.getLogisticCode());

            //????????????????????????
            List<DepositoryPurchaseStatusMsgDto> depositoryPurchaseStatusMsgDtoList = new ArrayList<>();
            waybillGoodDetailDtoMap.forEach((purchaseId, tempWaybillGoodDetailDtoList) -> {
                DepositoryPurchaseStatusMsgDto depositoryPurchaseStatusMsgDto = new DepositoryPurchaseStatusMsgDto();
                depositoryPurchaseStatusMsgDto.setPurchaseId(purchaseId);
                depositoryPurchaseStatusMsgDto.setSalesOrderId(logisticOrderDto.getSalesOrderId());
                depositoryPurchaseStatusMsgDto.setItemId(tempWaybillGoodDetailDtoList.get(0).getItemId());
                depositoryPurchaseStatusMsgDtoList.add(depositoryPurchaseStatusMsgDto);
            });

            //???????????????????????????
            LogisticsPackageDO logisticsPackageDO = new LogisticsPackageDO();
            logisticsPackageDO.setId(logisticOrderDto.getLogisticsPackageId());

            LogisticDispatchResponseDto logisticDispatchResponseDto = new LogisticDispatchResponseDto();
            logisticDispatchResponseDto.setIsSuccess(Boolean.FALSE);
            logisticDispatchResponseDto.setStatusCode(DepositoryPurchaseStatusEnum.PREPARATION.getStatusCode());
            logisticDispatchResponseDtoList.add(logisticDispatchResponseDto);

            //step1. ?????????????????????
            LogisticOrderResponseDto logisticOrderResponseDto = logisticManager.addLogisticOrder(logisticOrderDto);
            if (logisticOrderResponseDto.isSuccessSign())        //????????????
            {
                //?????????????????????????????????  ?????????
                logisticsPackageDO.setLogisticsStatus(DeliveryPackageLogisticsStatusEnum.DELIVERED.getCode());
                logisticsPackageDO.setLogisticsTrackingCode(logisticOrderResponseDto.getTrackingNumber());      //????????????????????????
                logisticsPackageDO.setLogisticsChannelTrackingCode(logisticOrderResponseDto.getChannelTrackingNum());      //??????????????????????????????
                logisticsPackageDO.setLogisticsProcessCode(logisticOrderResponseDto.getProcessCode());
                logisticsPackageDO.setLogisticsIdnexNumber(logisticOrderResponseDto.getIndexNumber());
                logisticsPackageDO.setLogisticsWayBillNo(logisticOrderResponseDto.getWaybillNo());
                logisticsPackageDO.setDeliveryStatus(DeliveryPackageDeliveryStatusEnum.SENDED.getCode());
//                logisticsPackageDO.setReceivingGoodTime(new Date());
                logisticsPackageDO.setDeliveryTime(new Date());
                logisticsPackageMapper.updateById(logisticsPackageDO);

                //?????????
                sendMsgUtils.sendPurchaseIntoDepositoryMsg(depositoryPurchaseStatusMsgDtoList, DepositoryPurchaseStatusEnum.SENDED.getStatusCode());       //?????????

                //????????????
                logisticDispatchResponseDto.setLogisticsOrderNo(logisticOrderResponseDto.getLogisticsOrderNo());
                logisticDispatchResponseDto.setTrackingNumber(logisticOrderResponseDto.getTrackingNumber());
                logisticDispatchResponseDto.setWaybillNo(logisticOrderResponseDto.getWaybillNo());
                logisticDispatchResponseDto.setIsSuccess(Boolean.TRUE);
                logisticDispatchResponseDto.setStatusCode(DepositoryPurchaseStatusEnum.SENDED.getStatusCode());

            } else      //????????????
            {
                logisticsPackageDO.setErrorInfo(logisticOrderResponseDto.getMessage() + StringUtils.substring(logisticOrderResponseDto.getError(), 0, 150));       //????????????
                logisticsPackageDO.setLogisticsStatus(DeliveryPackageLogisticsStatusEnum.ERROR.getCode());      //?????????????????????
                logisticsPackageDO.setDeliveryStatus(DeliveryPackageDeliveryStatusEnum.PREPARATIONFAIL.getCode());
//                logisticsPackageDO.setIsDeleted(Constant.YES);
//                logisticsPackageDO.setIsValid(Constant.NO_0);
                logisticsPackageMapper.updateById(logisticsPackageDO);      // ?????????????????????

                logisticDispatchResponseDto.setStatusCode(DepositoryPurchaseStatusEnum.PREPARATIONFAIL.getStatusCode());
                logisticDispatchResponseDto.setErrorMsg(logisticOrderResponseDto.getMessage());
                sendMsgUtils.sendPurchaseIntoDepositoryMsg(depositoryPurchaseStatusMsgDtoList, DepositoryPurchaseStatusEnum.PREPARATIONFAIL.getStatusCode());       //????????????
            }
        });
        return logisticDispatchResponseDtoList;
    }

    //???????????????
    private LogisticOrderResponseDto createLogisticOrder(ILogisticManager logisticManager, LogisticOrderDto logisticOrderDto, LogisticDispatchResponseDto logisticDispatchResponseDto, LogisticsPackageDO logisticsPackageDO, List<DepositoryPurchaseStatusMsgDto> depositoryPurchaseStatusMsgDtoList) {
        LogisticOrderResponseDto logisticOrderResponseDto = null;
        try {
            logisticOrderResponseDto = logisticManager.addLogisticOrder(logisticOrderDto);
        } catch (Exception e) {
            log.error("??????????????????????????????????????????{}", e);
            logisticOrderResponseDto = new LogisticOrderResponseDto();
            logisticOrderResponseDto.setSuccessSign(false);
            logisticOrderResponseDto.setMessage(StringUtils.substring(e.getMessage(), 0, 200));
        }
        if (!logisticOrderResponseDto.isSuccessSign())        //????????????
        {
            logisticsPackageDO.setErrorInfo(logisticOrderResponseDto.getMessage() + (StringUtils.isBlank(logisticDispatchResponseDto.getErrorMsg()) ? "" : logisticDispatchResponseDto.getErrorMsg()));       //????????????
            logisticsPackageDO.setLogisticsStatus(DeliveryPackageLogisticsStatusEnum.ERROR.getCode());      //?????????????????????
            logisticsPackageDO.setDeliveryStatus(DeliveryPackageDeliveryStatusEnum.PREPARATIONFAIL.getCode());
//            logisticsPackageDO.setIsDeleted(Constant.YES);
//            logisticsPackageDO.setIsValid(Constant.NO_0);
            logisticsPackageMapper.updateById(logisticsPackageDO);      // ?????????????????????

            logisticDispatchResponseDto.setStatusCode(DepositoryPurchaseStatusEnum.PREPARATIONFAIL.getStatusCode());
            logisticDispatchResponseDto.setErrorMsg(logisticOrderResponseDto.getMessage());
            sendMsgUtils.sendPurchaseIntoDepositoryMsg(depositoryPurchaseStatusMsgDtoList, DepositoryPurchaseStatusEnum.PREPARATIONFAIL.getStatusCode());       //????????????
            return null;
        }

        //?????????????????????????????????  ??????????????????
        logisticsPackageDO.setLogisticsStatus(DeliveryPackageLogisticsStatusEnum.CREATED.getCode());        //????????????
        logisticsPackageDO.setLogisticsTrackingCode(logisticOrderResponseDto.getTrackingNumber());      //????????????????????????
        logisticsPackageDO.setLogisticsChannelTrackingCode(logisticOrderResponseDto.getChannelTrackingNum());      //??????????????????????????????
        logisticsPackageDO.setLogisticsProcessCode(logisticOrderResponseDto.getProcessCode());
        logisticsPackageDO.setLogisticsIdnexNumber(logisticOrderResponseDto.getIndexNumber());
        logisticsPackageDO.setLogisticsWayBillNo(logisticOrderResponseDto.getWaybillNo());
        logisticsPackageDO.setDeliveryStatus(DeliveryPackageDeliveryStatusEnum.PREPARATION.getCode());
        logisticsPackageMapper.updateById(logisticsPackageDO);

        //?????????
        sendMsgUtils.sendPurchaseIntoDepositoryMsg(depositoryPurchaseStatusMsgDtoList, DepositoryPurchaseStatusEnum.PREPARATION.getStatusCode());       //?????????
        return logisticOrderResponseDto;
    }

    //??????????????????
    private LogisticPrintLabelResponseDto queryLogisticLabel(ILogisticManager logisticManager, LogisticOrderResponseDto logisticOrderResponseDto, LogisticDispatchResponseDto logisticDispatchResponseDto) {
        if (logisticOrderResponseDto == null) return null;        //????????????

        LogisticOrderSearchDto logisticOrderSearchDto = new LogisticOrderSearchDto();
        logisticOrderSearchDto.setLogisticsOrderNo(logisticOrderResponseDto.getLogisticsOrderNo());
        logisticOrderSearchDto.setOrderNo(logisticOrderResponseDto.getProcessCode());
        logisticOrderSearchDto.setWayBillNo(logisticOrderResponseDto.getWaybillNo());
        logisticOrderSearchDto.setTrackingNumber(logisticOrderResponseDto.getTrackingNumber());
        LogisticPrintLabelResponseDto logisticPrintLabelResponseDto = null;
        try {
            logisticPrintLabelResponseDto = logisticManager.getLogisticPrintLabel(logisticOrderSearchDto);
        } catch (Exception e) {
            log.error("???????????????????????????????????????{}", e);
            logisticPrintLabelResponseDto = new LogisticPrintLabelResponseDto();
            logisticPrintLabelResponseDto.setSuccessSign(false);
            logisticPrintLabelResponseDto.setMessage(StringUtils.substring(e.getMessage(), 0, 200));
        }
        if (!logisticPrintLabelResponseDto.isSuccessSign()) {
            logisticDispatchResponseDto.setErrorMsg(logisticPrintLabelResponseDto.getMessage());
            // ??????????????????
            LambdaUpdateWrapper<LogisticsPackageDO> updateWrapper = Wrappers.<LogisticsPackageDO>lambdaUpdate()
                    .set(LogisticsPackageDO::getDeliveryStatus, DeliveryPackageDeliveryStatusEnum.PREPARATIONFAIL.getCode())
                    .set(LogisticsPackageDO::getErrorInfo, logisticPrintLabelResponseDto.getMessage())
                    .eq(LogisticsPackageDO::getLogisticsOrderNo, logisticDispatchResponseDto.getLogisticsOrderNo());
            logisticsPackageMapper.update(null, updateWrapper);
            return null;
        }

        return logisticPrintLabelResponseDto;
    }

    //??????????????????
    private void pushOutDepository(LogisticOrderDto logisticOrderDto, LogisticOrderResponseDto logisticOrderResponseDto,
                                   LogisticPrintLabelResponseDto logisticPrintLabelResponseDto, LogisticsPackageDO logisticsPackageDO,
                                   LogisticDispatchResponseDto logisticDispatchResponseDto, List<DepositoryPurchaseStatusMsgDto> depositoryPurchaseStatusMsgDtoList) {
        if (logisticPrintLabelResponseDto == null) return;     //??????????????????
        IDepositoryManager depositoryManager = null;
        if (logisticOrderDto.getStorageCode() != null && logisticOrderDto.getStorageCode() == DepositoryTypeEnum.TEST.getId()) {
            log.info("??????????????????????????????", JSONObject.toJSONString(logisticOrderDto));
            depositoryManager = depositoryFactory.createDepositoryManager(DepositoryTypeEnum.TEST.getCode());
        } else {
            depositoryManager = depositoryFactory.createDepositoryManager(DepositoryTypeEnum.WANB.getCode());
        }
        OutDepositoryOrderDto outDepositoryOrderDto = this.getOutDepositoryOrderDto(logisticOrderDto, logisticOrderResponseDto, logisticPrintLabelResponseDto);
        OutDepositoryResultDto outDepositoryResultDto = null;
        try {
            outDepositoryResultDto = depositoryManager.addOutDepositoryOrder(outDepositoryOrderDto);
        } catch (Exception e) {
            log.error("???????????????????????????????????????{}", e);
            outDepositoryResultDto = new OutDepositoryResultDto();
            outDepositoryResultDto.setSuccessSign(false);
            outDepositoryResultDto.setErrorMsg(StringUtils.substring(e.getMessage(), 0, 200));
        }
        // ???????????????????????????????????????????????????????????????4px???????????????????????????????????????????????????????????????????????????????????????
//        logisticsPackageDO.setLogisticsTrackingCode(outDepositoryOrderDto.getExpress().getTrackingNumber());
        logisticsPackageDO.setLogisticsLabelUrl(logisticPrintLabelResponseDto.getLogisticsLabel());
        if (!outDepositoryResultDto.isSuccessSign()) {
            logisticsPackageDO.setErrorInfo(outDepositoryResultDto.getErrorMsg());       //????????????
            logisticsPackageDO.setDeliveryStatus(DeliveryPackageDeliveryStatusEnum.FAILPUSH.getCode());
//            logisticsPackageDO.setIsValid(Constant.NO_0);
            logisticsPackageMapper.updateById(logisticsPackageDO);      // ?????????????????????
            logisticDispatchResponseDto.setStatusCode(DepositoryPurchaseStatusEnum.FAILPUSH.getStatusCode());
            logisticDispatchResponseDto.setErrorMsg(outDepositoryResultDto.getErrorMsg());
            sendMsgUtils.sendPurchaseIntoDepositoryMsg(depositoryPurchaseStatusMsgDtoList, DepositoryPurchaseStatusEnum.FAILPUSH.getStatusCode());       //????????????
            return;
        }

        //?????????????????????????????????   ?????????
//        logisticsPackageDO.setLogisticsStatus(DeliveryPackageLogisticsStatusEnum.INDELIVERY.getCode());
        logisticsPackageDO.setLogisticsStatus(DeliveryPackageLogisticsStatusEnum.CREATED.getCode());
        logisticsPackageDO.setDeliveryStatus(DeliveryPackageDeliveryStatusEnum.SENDING.getCode());
        logisticsPackageDO.setErrorInfo(StringUtils.isNotBlank(logisticsPackageDO.getErrorInfo()) ? "????????????" + logisticsPackageDO.getErrorInfo() : null);
        logisticsPackageMapper.updateById(logisticsPackageDO);

        //?????????
        sendMsgUtils.sendPurchaseIntoDepositoryMsg(depositoryPurchaseStatusMsgDtoList, DepositoryPurchaseStatusEnum.SENDING.getStatusCode());       //?????????

        //????????????
        logisticDispatchResponseDto.setDispatchOrderId(outDepositoryResultDto.getRefId());
        logisticDispatchResponseDto.setLogisticsOrderNo(logisticOrderResponseDto.getLogisticsOrderNo());
        logisticDispatchResponseDto.setTrackingNumber(logisticOrderResponseDto.getTrackingNumber());
        logisticDispatchResponseDto.setWaybillNo(logisticOrderResponseDto.getWaybillNo());
        logisticDispatchResponseDto.setIsSuccess(Boolean.TRUE);
        logisticDispatchResponseDto.setStatusCode(DepositoryPurchaseStatusEnum.SENDING.getStatusCode());
    }


    private void logisticsPackageLabel(List<LogisticOrderDto> logisticOrderDtoList) {
        //step 1. ????????????????????????????????? ?????????????????????
        //step 2. ????????????????????????????????????????????? ????????????
        //stpe 3. ??????????????????????????????????????????????????????????????????????????? ????????????
        //step 4. ???????????????????????? labelId??????????????????????????????????????????????????? labelId???????????????????????????????????????categoryCode
        //step 5. ??????????????????

        //step 1
        Set<String> categoryCodeSet = new HashSet<>();
        Set<String> skuSet = new HashSet<>();
        Map<String, List<String>> categoryMetaIdMap = new HashMap<>();        //?????????????????????????????????(????????????)
        logisticOrderDtoList.forEach(logisticOrderDto -> {
            //??????????????????????????? ???????????????????????????
            List<WaybillGoodDetailDto> waybillGoodDetailDtoList = logisticOrderDto.getWaybillGoodDetailDtos();
            if (waybillGoodDetailDtoList == null || waybillGoodDetailDtoList.size() == 0) {
                throw new GlobalException("100400", "?????????????????????????????????");
            }
            Set<String> tempCategoryCodeSet = waybillGoodDetailDtoList.stream().map(WaybillGoodDetailDto::getCategoryCode).collect(Collectors.toSet());
            Set<String> tempSkuSet = waybillGoodDetailDtoList.stream().map(WaybillGoodDetailDto::getGoodsId).collect(Collectors.toSet());
            if (tempCategoryCodeSet.size() != 0) {
                log.info("????????????????????????????????????????????????????????????????????? ?????????{}", JSON.toJSONString(tempCategoryCodeSet));
                //?????????????????????????????????????????????????????????????????????
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
                    log.info("???????????????????????????????????????????????????{}", JSON.toJSONString(strings));
                    List<Map<String, Object>> maps = logisticsLabelCategoryMapper.queryLogisticsLabelCategoryList(new ArrayList<>(strings));
                    log.info("???????????????????????????????????????????????????{}", JSON.toJSONString(maps));
                    List<Long> labelId = maps.stream().map(logisticsLabelProductItem -> Long.valueOf(logisticsLabelProductItem.get("labelId").toString())).collect(Collectors.toList());
                    waybillGoodDetailDto.setRelevanceLabelIdList(labelId);

                }


                log.info("????????????????????????????????????????????????????????????????????????{}", s);
                List<String> categoryMetaIdList = categoryMetaList.stream().map(MetadatapbMetadata.CategoryMeta::getId).collect(Collectors.toList());

                //?????????????????????
                categoryMetaIdMap.put(logisticOrderDto.getLogisticOrderNo(), categoryMetaIdList);
                categoryCodeSet.addAll(categoryMetaIdList);     //??????????????????????????????????????????
            }

            //?????????????????????sku ?????????????????????????????????????????????????????????????????????????????????sku
            categoryCodeSet.addAll(tempCategoryCodeSet);
            skuSet.addAll(tempSkuSet);
        });


        //????????????????????????
        List<Map<String, Object>> logisticsLabelProductList = new ArrayList<>();
        Map<String, List<Map<String, Object>>> logisticsLabelProductMap = new HashMap<>();

        //step 2
        //????????????????????????
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
                List<Map<String, Object>> productList = Optional.ofNullable(logisticsLabelProductMap.get(sku)).orElse(new ArrayList<>());       //????????????
                List<String> categoryMetaIdList = Optional.ofNullable(categoryMetaIdMap.get(logisticOrderDto.getLogisticOrderNo())).orElse(new ArrayList<>());        //??????????????????????????????????????????????????????
                //step 4
                //?????? labelId ??????
                List<Long> productLabelIdList = productList.stream().map(map -> Long.valueOf(map.get("labelId").toString())).collect(Collectors.toList());
                List<Long> categoryLabelIdList = logisticsLabelCategoryList.stream().filter(logisticsLabelCategory -> categoryMetaIdList.contains(logisticsLabelCategory.get("categoryCode").toString())).map(logisticsLabelCategory -> Long.valueOf(logisticsLabelCategory.get("labelId").toString())).collect(Collectors.toList());
                labelIdSet.addAll(productLabelIdList);     //??????????????? ?????? id
                labelIdSet.addAll(categoryLabelIdList);    //??????????????? ?????? id

                //??????????????????????????? ??????id????????? id
                waybillGoodDetailDto.setCategoryCodeList(new ArrayList<>(categoryMetaIdList));
                waybillGoodDetailDto.setLabelIdList(new ArrayList<>(labelIdSet));

                //?????? labelName ??????
                List<String> productLabelNameList = logisticsLabelProductList.stream().filter(logisticsLabelProductItem -> labelIdSet.contains(Long.valueOf(logisticsLabelProductItem.get("labelId").toString()))).map(logisticsLabelProductItem -> logisticsLabelProductItem.get("labelName").toString()).collect(Collectors.toList());
                List<String> categoryLabelNameList = logisticsLabelCategoryList.stream().filter(logisticsLabelProductItem -> labelIdSet.contains(Long.valueOf(logisticsLabelProductItem.get("labelId").toString()))).map(logisticsLabelProductItem -> logisticsLabelProductItem.get("labelName").toString()).collect(Collectors.toList());
                labelNameSet.addAll(productLabelNameList);
                labelNameSet.addAll(categoryLabelNameList);

                //step 5
                //??????????????????
                if (atomicReferenceBatteryFlag.get()) {
                    Boolean tempBatteryFlag = this.batteryProperty(logisticOrderDto, labelNameSet);
                    if (tempBatteryFlag) atomicReferenceBatteryFlag.set(false);        //????????????????????????
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

            //?????????????????? sku ???????????? itemId ??? purchaseId ???????????????
            List<WaybillGoodDetailDto> waybillGoodDetailDtoList = logisticOrderDto.getWaybillGoodDetailDtos();
            Map<String, Map> logisticsProductItemMap = this.getLogisticsProductItemMap(waybillGoodDetailDtoList);
            Set<String> skuSet = new HashSet<>();
            for (WaybillGoodDetailDto waybillGoodDetailDto : waybillGoodDetailDtoList) {
                if (skuSet.contains(waybillGoodDetailDto.getGoodsId())) {      //??? sku ???????????????????????????????????????
                    continue;
                }
                skuSet.add(waybillGoodDetailDto.getGoodsId());
                //???????????????????????? itemId ??????
                logisticsPackageItemDOList.addAll(this.getLogisticsPackageItemList(logisticsProductItemMap, waybillGoodDetailDto, logisticsPackageDO.getId(), logisticsPackageDO.getCreateBy()));

                //??????????????????????????????
                logisticsProductDOList.add(this.getLogisticsProduct(logisticsProductItemMap, logisticsPackageDO, waybillGoodDetailDto));
            }

            //????????????????????????
            logisticsPackageAddressDOList.add(this.getLogisticsPackageAddress(logisticsPackageDO.getId(), logisticOrderDto.getReceiverAddress()));

            //????????????????????????
            logisticsPackageDOList.add(logisticsPackageDO);
        });

        //??????????????????????????????
        logisticsPackageMapper.insertLogisticsPackageBatch(logisticsPackageDOList);     //????????????????????????
        logisticsProductMapper.insertLogisticsProductBatch(logisticsProductDOList);     //??????????????????????????????
        logisticsPackageItemMapper.insertLogisticsPackageItemBatch(logisticsPackageItemDOList);     //??????????????????????????????
        logisticsPackageAddressMapper.insertLogisticsPackageAddressBatch(logisticsPackageAddressDOList);        //?????????????????????????????????
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

        //???????????????????????????
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
        logisticsPackageDO.setLogisticsStatus(DeliveryPackageLogisticsStatusEnum.INIT.getCode());       //??????????????????
        logisticsPackageDO.setLogisticsArea(addressDto.getCountryCode());
        logisticsPackageDO.setLogisticsWeight(waybillGoodDetailDtoList.stream().map(WaybillGoodDetailDto::getWeight).reduce(BigDecimal.ZERO, BigDecimal::add));
        logisticsPackageDO.setLogisticsRemark(logisticOrderDto.getRemark());
        logisticsPackageDO.setBattery(logisticOrderDto.getBattery());
        logisticsPackageDO.setBatteryType(logisticOrderDto.getBatteryType());
        logisticsPackageDO.setCreateBy(StringUtils.isBlank(logisticOrderDto.getOperationer()) ? Constant.SYSTEM_ROOT : logisticOrderDto.getOperationer());
        return logisticsPackageDO;
    }

    private Map<String, Map> getLogisticsProductItemMap(List<WaybillGoodDetailDto> waybillGoodDetailDtoList) {
        //?????????????????? sku ???????????? itemId ??? purchaseId ???????????????
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
        List<String> itemAndPurchaseIdList = (List<String>) Optional.ofNullable(tempMap.get("itemAndPurchaseIdList")).orElse(new ArrayList<>());     //item id, ?????? id
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

        //??????????????????????????????
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
            expressDto.setTrackingNumber(logisticPrintLabelResponseDto.getLabelBarcode());     //?????????????????????
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
        if (labelNameSet.contains("??????")) {
            logisticOrderDto.setBattery(Constant.YES);
            logisticOrderDto.setBatteryType(2);
            return true;
        } else if (labelNameSet.contains("?????????")) {
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
//                        .eq(DepositorySaleOrderDetailDO::getPurchaseId, purchaseId);  ??????????????????PurchaseId ??????????????????
                depositorySaleOrderDetailMapper.update(null, updateWrapper);
            }

        }
    }

    private boolean outDepositoryLimitTime(LogisticOrderDto logisticOrderDto) {
        String redisKey = logisticOrderDto.getWaybillGoodDetailDtos().stream().map(item -> item.getItemId()).collect(Collectors.joining(","));
        String hashCode = Objects.hashCode(redisKey) + "";
        log.info("????????????????????????????????????,??????????????????{}??????????????????{}???Hash?????????{}", logisticOrderDto.getSalesOrderId(), redisKey, hashCode);
        return redissLockUtil.tryLock(hashCode, TimeUnit.SECONDS, -1, 180);
    }

    private void removeOutDepositoryLimit(LogisticOrderDto logisticOrderDto) {
        String redisKey = logisticOrderDto.getWaybillGoodDetailDtos().stream().map(item -> item.getItemId()).collect(Collectors.joining(","));
        String hashCode = Objects.hashCode(redisKey) + "";
        log.info("??????????????????????????????????????????redis???key,??????????????????{}??????????????????{}???Hash?????????{}", logisticOrderDto.getSalesOrderId(), redisKey, hashCode);
        redissLockUtil.unlock(hashCode);
    }
}

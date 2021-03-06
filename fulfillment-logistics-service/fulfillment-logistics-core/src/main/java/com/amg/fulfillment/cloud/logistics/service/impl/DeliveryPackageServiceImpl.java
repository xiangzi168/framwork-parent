package com.amg.fulfillment.cloud.logistics.service.impl;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.amg.framework.boot.base.enums.ResponseCodeEnum;
import com.amg.framework.boot.base.exception.GlobalException;
import com.amg.framework.boot.redisson.utils.RedissLockUtil;
import com.amg.framework.boot.utils.excel.ExcelUtil;
import com.amg.framework.boot.utils.id.SnowflakeIdUtils;
import com.amg.framework.cloud.grpc.context.UserContext;
import com.amg.framework.cloud.rocketmq.utils.RocketmqUtils;
import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import com.amg.fulfillment.cloud.logistics.api.dto.msg.DepositoryPurchaseStatusMsgDto;
import com.amg.fulfillment.cloud.logistics.api.dto.msg.LogisticsChangeMsgDto;
import com.amg.fulfillment.cloud.logistics.api.enumeration.*;
import com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderLogisticsGTO;
import com.amg.fulfillment.cloud.logistics.api.proto.SalesOrderLogisticsGTO;
import com.amg.fulfillment.cloud.logistics.api.util.BeanConvertUtils;
import com.amg.fulfillment.cloud.logistics.api.util.LogisticsCommonUtils;
import com.amg.fulfillment.cloud.logistics.config.LogisticPackageAgainPushEvent;
import com.amg.fulfillment.cloud.logistics.config.WBDepositoryRegisterServiceConfig;
import com.amg.fulfillment.cloud.logistics.controller.TestController;
import com.amg.fulfillment.cloud.logistics.dto.depository.*;
import com.amg.fulfillment.cloud.logistics.dto.logistic.*;
import com.amg.fulfillment.cloud.logistics.entity.*;
import com.amg.fulfillment.cloud.logistics.enumeration.BaseLogisticsResponseCodeEnum;
import com.amg.fulfillment.cloud.logistics.enumeration.DepositoryTypeEnum;
import com.amg.fulfillment.cloud.logistics.enumeration.ExceptionEnum;
import com.amg.fulfillment.cloud.logistics.enumeration.LogisticNodeEnum;
import com.amg.fulfillment.cloud.logistics.factory.DepositoryFactory;
import com.amg.fulfillment.cloud.logistics.factory.LogisticFactory;
import com.amg.fulfillment.cloud.logistics.manager.IDepositoryManager;
import com.amg.fulfillment.cloud.logistics.manager.ILogisticManager;
import com.amg.fulfillment.cloud.logistics.mapper.*;
import com.amg.fulfillment.cloud.logistics.model.excel.Delivery1688PackageExcel;
import com.amg.fulfillment.cloud.logistics.model.excel.DeliveryAePackageExcel;
import com.amg.fulfillment.cloud.logistics.model.req.*;
import com.amg.fulfillment.cloud.logistics.model.vo.*;
import com.amg.fulfillment.cloud.logistics.service.IDeliveryPackageService;
import com.amg.fulfillment.cloud.logistics.service.IDeliveryProductService;
import com.amg.fulfillment.cloud.logistics.service.ILogisticsTrackNodeService;
import com.amg.fulfillment.cloud.logistics.util.*;
import com.amg.fulfillment.cloud.order.api.client.CjChannelOrderClient;
import com.amg.fulfillment.cloud.order.api.client.OrderServiceClient;
import com.amg.fulfillment.cloud.order.api.dto.AeTrackinginfoDTO;
import com.amg.fulfillment.cloud.order.api.dto.CjBaseResultDTO;
import com.amg.fulfillment.cloud.order.api.dto.CjTrackNumberFindDTO;
import com.amg.fulfillment.cloud.order.api.dto.OpenPlatformDTO;
import com.amg.fulfillment.cloud.order.api.enums.CjOrderStatusEnum;
import com.amg.fulfillment.cloud.order.api.proto.CjChannelOrderGTO;
import com.amg.fulfillment.cloud.order.api.proto.OrderUserInfoGTO;
import com.amg.fulfillment.cloud.order.api.util.AeOpenUtils;
import com.amg.fulfillment.cloud.order.api.util.CjHttpUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.taobao.api.request.AliexpressLogisticsDsTrackinginfoQueryRequest;
import com.taobao.api.response.AliexpressLogisticsDsTrackinginfoQueryResponse;
import com.taobao.api.response.AliexpressTradeDsOrderGetResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotBlank;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.apache.rocketmq.client.producer.SendStatus.SEND_OK;

/**
 * Created by Seraph on 2021/5/13
 */
@Slf4j
@Service
public class DeliveryPackageServiceImpl implements IDeliveryPackageService {

    @Autowired
    private LogisticsCommonUtils logisticsCommonUtils;
    @Autowired
    private LogisticsPackageMapper logisticsPackageMapper;
    @Autowired
    private LogisticsProductMapper logisticsProductMapper;
    @Autowired
    private LogisticsPackageTrackMapper logisticsPackageTrackMapper;
    @Autowired
    private LogisticsPurchasePackageMapper logisticsPurchasePackageMapper;
    @Autowired
    private LogisticsPackageItemMapper logisticsPackageItemMapper;
    @Autowired
    private LogisticsPackageAddressMapper logisticsPackageAddressMapper;
    @Autowired
    private LogisticsTrackNodeMapper logisticsTrackNodeMapper;
    @Autowired
    private LogisticsChannelMapper logisticsChannelMapper;
    @Autowired
    private DepositoryFactory depositoryFactory;
    @Autowired
    private LogisticFactory logisticFactory;
    @Autowired
    private SendMsgUtils sendMsgUtils;
    @Autowired
    private AeOpenUtils aeOpenUtils;
    @Autowired
    private IDeliveryProductService deliveryProductService;
    @Autowired
    private ILogisticsTrackNodeService logisticsTrackNodeService;
    @Autowired
    private RocketmqUtils rocketmqUtils;
    @Autowired
    private RedissLockUtil redissLockUtil;
    @Autowired
    private CjHttpUtils cjHttpUtils;
    @Autowired
    private CjChannelOrderClient cjChannelOrderClient;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private OrderServiceClient orderServiceClient;
    @Autowired
    private TransactionTemplate transactionTemplate;

    private static final SimpleDateFormat simpleDateFormate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    @Async
    public void syncUnfinishedPackageList() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -90);
        //?????????30?????????????????????
        LambdaQueryWrapper<LogisticsPackageDO> logisticsPackageDOLambdaQueryWrapper = Wrappers.<LogisticsPackageDO>lambdaQuery()
                .ge(LogisticsPackageDO::getCreateTime, calendar.getTime())     //90 ??????????????????
                .between(LogisticsPackageDO::getLogisticsStatus, DeliveryPackageLogisticsStatusEnum.CREATED.getCode(), DeliveryPackageLogisticsStatusEnum.DELIVERED.getCode())
                .eq(LogisticsPackageDO::getIsValid, Constant.YES);      //????????????
        long page = 1;
        long rows = 200;
        while (true) {
            Page<LogisticsPackageDO> selectPage = logisticsPackageMapper.selectPage(new Page(page, rows), logisticsPackageDOLambdaQueryWrapper);
            List<LogisticsPackageDO> logisticsPackageDOList = selectPage.getRecords();
            sendMsgUtils.sendUnfinishedPackageIdList(logisticsPackageDOList);
            if (!selectPage.hasNext()) {
                break;
            }
            page++;
        }
    }

    @Override
    public Page<DeliveryPackageVO> list(DeliveryPackageReq deliveryPackageReq) {
        IPage page = new Page(deliveryPackageReq.getPage(), deliveryPackageReq.getRow());
        // ?????????????????? ??????????????? ????????????logistics_received???????????????????????????
        if (LogisticNodeEnum.DEFAULT.getNodeEn().equals(deliveryPackageReq.getLogisticsLastNode())) {
            deliveryPackageReq.setLogisticsLastNode(null);
            deliveryPackageReq.setLogisticsReceived(Constant.NO_0);
        }
        List<LogisticsPackageDO> logisticsPackageDOList = logisticsPackageMapper.queryLogisticsPackageList(page, deliveryPackageReq);
        List<DeliveryPackageVO> deliveryPackageVOList = BeanConvertUtils.copyProperties(logisticsPackageDOList, DeliveryPackageVO.class);

        Page<DeliveryPackageVO> resultPage = BeanConvertUtils.copyProperties(page, Page.class);
        resultPage.setRecords(deliveryPackageVOList);
        return resultPage;
    }

    @Override
    public DeliveryPackageVO detail(Integer type, Long packageId) {
        //???????????????????????????
        LambdaQueryWrapper<LogisticsPackageDO> logisticsPackageDOLambdaQueryWrapper = Wrappers.<LogisticsPackageDO>lambdaQuery()
                .eq(LogisticsPackageDO::getId, packageId)
                .eq(LogisticsPackageDO::getType, type);

        List<DeliveryPackageVO> deliveryPackageVOList = this.getLogisticsPackageList(logisticsPackageDOLambdaQueryWrapper);
        return deliveryPackageVOList.get(0);
    }

    @Override
    public List<DeliveryPackageVO> salesOrderPackage(String salesOrderId) {
        LambdaQueryWrapper<LogisticsPackageDO> logisticsPackageDOLambdaQueryWrapper = Wrappers.<LogisticsPackageDO>lambdaQuery()
                .eq(LogisticsPackageDO::getIsValid, Constant.YES)
                .in(LogisticsPackageDO::getDeliveryStatus, DeliveryPackageDeliveryStatusEnum.SENDING.getCode(), DeliveryPackageDeliveryStatusEnum.SENDED.getCode())
                .eq(LogisticsPackageDO::getSalesOrderId, salesOrderId);
        List<DeliveryPackageVO> logisticsPackageList = this.getLogisticsPackageList(logisticsPackageDOLambdaQueryWrapper);
        if (Objects.isNull(logisticsPackageList) || logisticsPackageList.isEmpty()) {
            return Collections.emptyList();
        }
        List<DeliveryPackageVO> returnList = logisticsPackageList.stream().filter(item -> !Objects.isNull(item.getId())).collect(Collectors.toList());
        if (Objects.isNull(returnList) || returnList.isEmpty()) {
            return Collections.emptyList();
        }
        return returnList;
    }

    @Override
    public Boolean pushWarehouse(DeliveryPackageOperationReq deliveryPackageOperationReq) {
        if (!outDepositoryLimitTime(deliveryPackageOperationReq.getLogisticsWayBillNo())) {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "??????????????????????????????????????????");
        }
        deliveryPackageOperationReq.setLogisticsWayBillNos(Collections.singletonList(deliveryPackageOperationReq.getLogisticsWayBillNo()));
        return this.pushWarehouseBatch(deliveryPackageOperationReq);
    }

    @Override
    public Boolean pushWarehouseBatch(DeliveryPackageOperationReq deliveryPackageOperationReq) {
        List<String> wayBillNoList = deliveryPackageOperationReq.getLogisticsWayBillNos();
        List<String> requiretList = wayBillNoList.stream().filter(item -> outDepositoryLimitTime(item)).collect(Collectors.toList());
        deliveryPackageOperationReq.setLogisticsWayBillNos(requiretList);
        if (wayBillNoList.isEmpty() || wayBillNoList.stream().filter(item -> StringUtils.isNotBlank(item)).count() < 1) {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "????????????????????????????????????null");
        }
        try {
            //???????????????
            LambdaQueryWrapper<LogisticsPackageDO> logisticsPackageDOLambdaQueryWrapper = Wrappers.<LogisticsPackageDO>lambdaQuery()
                    .in(LogisticsPackageDO::getLogisticsWayBillNo, wayBillNoList);
            List<LogisticsPackageDO> logisticsPackageDOList = logisticsPackageMapper.selectList(logisticsPackageDOLambdaQueryWrapper);
            List<Long> idList = logisticsPackageDOList.stream().map(LogisticsPackageDO::getId).collect(Collectors.toList());
            //????????????
            LambdaQueryWrapper<LogisticsProductDO> logisticsProductDOLambdaQueryWrapper = Wrappers.<LogisticsProductDO>lambdaQuery()
                    .in(LogisticsProductDO::getRelationId, idList)
                    .eq(LogisticsProductDO::getType, PackageProductTypeEnum.DELIVERY_PACKAGE.getType());
            List<LogisticsProductDO> logisticsProductDOList = logisticsProductMapper.selectList(logisticsProductDOLambdaQueryWrapper);
            Map<Long, List<LogisticsProductDO>> logisticsProductDOMap = logisticsProductDOList.stream().collect(Collectors.groupingBy(LogisticsProductDO::getRelationId));
            //????????? itemId ??????
            LambdaQueryWrapper<LogisticsPackageItemDO> logisticsPackageItemDOLambdaQueryWrapper = Wrappers.<LogisticsPackageItemDO>lambdaQuery()
                    .in(LogisticsPackageItemDO::getPackageId, idList);
            List<LogisticsPackageItemDO> logisticsPackageItemDOList = logisticsPackageItemMapper.selectList(logisticsPackageItemDOLambdaQueryWrapper);
            Map<Long, List<LogisticsPackageItemDO>> logisticsPackageItemDOMap = logisticsPackageItemDOList.stream().collect(Collectors.groupingBy(LogisticsPackageItemDO::getPackageId));

            //????????????
            LambdaQueryWrapper<LogisticsPackageAddressDO> logisticsPackageAddressDOLambdaQueryWrapper = Wrappers.<LogisticsPackageAddressDO>lambdaQuery()
                    .in(LogisticsPackageAddressDO::getPackageId, idList);
            List<LogisticsPackageAddressDO> logisticsPackageAddressDOList = logisticsPackageAddressMapper.selectList(logisticsPackageAddressDOLambdaQueryWrapper);
            Map<Long, LogisticsPackageAddressDO> logisticsPackageAddressDOMap = logisticsPackageAddressDOList.stream().collect(Collectors.toMap(LogisticsPackageAddressDO::getPackageId, logisticsPackageAddressDO -> logisticsPackageAddressDO));

            //???????????????????????????
            IDepositoryManager depositoryManager = depositoryFactory.createDepositoryManager(DepositoryTypeEnum.WANB.getCode());

            AtomicReference<Boolean> atomicReferenceFlag = new AtomicReference<>(false);
            logisticsPackageDOList.forEach(logisticsPackageDO -> {
                if (logisticsPackageDO.getType().equals(DeliveryPackageTypeEnum.AE_DELIVERY_PACKAGE.getType()))      //??????????????????????????????????????????
                {
                    return;
                }
                // ???????????????
                logisticsPackageDO.setCreateBy(UserContext.getCurrentUserName());
                List<DepositoryPurchaseStatusMsgDto> depositoryPurchaseStatusMsgDtoList = new ArrayList<>();
                List<LogisticsProductDO> tempLogisticsProductDOList = logisticsProductDOMap.get(logisticsPackageDO.getId());
                List<LogisticsPackageItemDO> tempLogisticsPackageItemDOList = logisticsPackageItemDOMap.get(logisticsPackageDO.getId());
                LogisticsPackageAddressDO logisticsPackageAddressDO = logisticsPackageAddressDOMap.get(logisticsPackageDO.getId());

                tempLogisticsPackageItemDOList.forEach(logisticsPackageItemDO -> {
                    DepositoryPurchaseStatusMsgDto depositoryPurchaseStatusMsgDto = new DepositoryPurchaseStatusMsgDto();
                    depositoryPurchaseStatusMsgDto.setSalesOrderId(logisticsPackageDO.getSalesOrderId());
                    depositoryPurchaseStatusMsgDto.setPurchaseId(logisticsPackageItemDO.getPurchaseId());
                    depositoryPurchaseStatusMsgDtoList.add(depositoryPurchaseStatusMsgDto);
                });

                OutDepositoryOrderDto outDepositoryOrderDto = getOutDepositoryOrderDto(logisticsPackageDO, tempLogisticsProductDOList, logisticsPackageAddressDO);
                // fix:?????????????????????????????????????????????????????????????????????
                outDepositoryOrderDto.getExpress().setTrackingNumber(logisticsPackageDO.getLogisticsWayBillNo());
    //            outDepositoryOrderDto.getExpress().setLabelUrl(logisticsPackageDO.getLogisticsLabelUrl());
    //            outDepositoryOrderDto.getExpress().setServiceId(WBDepositoryRegisterServiceConfig.getServicerIdFromCode(logisticsPackageDO.getLogisticsCode(), logisticsPackageDO.getLogisticsChannel()));
                OutDepositoryResultDto outDepositoryResultDto = depositoryManager.addOutDepositoryOrder(outDepositoryOrderDto);
                if (outDepositoryResultDto.isSuccessSign()) {
                    //?????????????????????????????????   ?????????
    //                logisticsPackageDO.setLogisticsStatus(DeliveryPackageLogisticsStatusEnum.INDELIVERY.getCode());     //?????????
                    logisticsPackageDO.setLogisticsStatus(DeliveryPackageLogisticsStatusEnum.CREATED.getCode());     //?????????
                    logisticsPackageDO.setDeliveryStatus(DeliveryPackageDeliveryStatusEnum.SENDING.getCode());
                    logisticsPackageDO.setLogisticsTrackingCode(outDepositoryOrderDto.getExpress().getTrackingNumber());
                    logisticsPackageDO.setLogisticsLabelUrl(outDepositoryOrderDto.getExpress().getLabelUrl());
                    logisticsPackageMapper.updateById(logisticsPackageDO);

                    //?????????
                    sendMsgUtils.sendPurchaseIntoDepositoryMsg(depositoryPurchaseStatusMsgDtoList, DepositoryPurchaseStatusEnum.SENDING.getStatusCode());       //?????????

                    atomicReferenceFlag.set(true);
                } else      //??????????????????
                {
                    logisticsPackageDO.setErrorInfo(outDepositoryResultDto.getErrorMsg());       //????????????
                    logisticsPackageDO.setDeliveryStatus(DeliveryPackageDeliveryStatusEnum.FAILPUSH.getCode());
                    logisticsPackageMapper.updateById(logisticsPackageDO);      // ?????????????????????

                    sendMsgUtils.sendPurchaseIntoDepositoryMsg(depositoryPurchaseStatusMsgDtoList, DepositoryPurchaseStatusEnum.FAILPUSH.getStatusCode());       //????????????
                }
            });
            return atomicReferenceFlag.get();
        } finally {
            requiretList.stream().forEach(item ->removeOutDepositoryLimit(item));
        }

    }

    @Override
    public DeliveryPackageCannelVO cancel(DeliveryPackageOperationReq deliveryPackageOperationReq) {
        deliveryPackageOperationReq.setLogisticsWayBillNos(Collections.singletonList(deliveryPackageOperationReq.getLogisticsWayBillNo()));
        List<DeliveryPackageCannelVO> deliveryPackageCannelVOList = this.cancelBatch(deliveryPackageOperationReq);
        if (deliveryPackageCannelVOList.size() == 0) {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "????????????");
        }
        return deliveryPackageCannelVOList.get(0);
    }

    @Override
    public List<DeliveryPackageCannelVO> cancelBatch(DeliveryPackageOperationReq deliveryPackageOperationReq) {
        List<String> wayBillNoList = deliveryPackageOperationReq.getLogisticsWayBillNos();
        List<Long> idLists = deliveryPackageOperationReq.getIdList();

        if ((Objects.isNull(wayBillNoList) || wayBillNoList.isEmpty()) && (Objects.isNull(idLists) || idLists.isEmpty())) {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "????????????????????????null");
        }
        String operationRemark = deliveryPackageOperationReq.getOperationRemark();
        LambdaQueryWrapper<LogisticsPackageDO> logisticsPackageDOLambdaQueryWrapper = Wrappers.<LogisticsPackageDO>lambdaQuery()
                .in(!Objects.isNull(wayBillNoList) && !wayBillNoList.isEmpty(), LogisticsPackageDO::getLogisticsWayBillNo, wayBillNoList)
                .in(!Objects.isNull(idLists) && !idLists.isEmpty(), LogisticsPackageDO::getId, idLists);
        List<LogisticsPackageDO> logisticsPackageDOList = logisticsPackageMapper.selectList(logisticsPackageDOLambdaQueryWrapper);
        if (logisticsPackageDOList.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> idList = logisticsPackageDOList.stream().map(LogisticsPackageDO::getId).collect(Collectors.toList());
        LambdaQueryWrapper<LogisticsPackageItemDO> logisticsPackageItemDOLambdaQueryWrapper = Wrappers.<LogisticsPackageItemDO>lambdaQuery()
                .in(LogisticsPackageItemDO::getPackageId, idList);
        List<LogisticsPackageItemDO> logisticsPackageItemDOList = logisticsPackageItemMapper.selectList(logisticsPackageItemDOLambdaQueryWrapper);
        Map<Long, List<LogisticsPackageItemDO>> logisticsPackageItemDOMap = logisticsPackageItemDOList.stream().collect(Collectors.groupingBy(LogisticsPackageItemDO::getPackageId));

        //????????????
        List<DeliveryPackageCannelVO> deliveryPackageCannelVOList = new ArrayList<>();

        //???????????????????????????
        IDepositoryManager depositoryManager = depositoryFactory.createDepositoryManager(DepositoryTypeEnum.WANB.getCode());

        ArrayList<LogisticsPackageDO> requiredAgainQueryDepositoryList = new ArrayList<>();
        //????????????
        logisticsPackageDOList.forEach(logisticsPackageDO -> {
            //??????????????????????????????????????????
            if (logisticsPackageDO.getType().equals(DeliveryPackageTypeEnum.AE_DELIVERY_PACKAGE.getType())) {
                return;
            }
            logisticsPackageDO.setUpdateBy(UserContext.getCurrentUserName());
            DeliveryPackageCannelVO deliveryPackageCannelVO = new DeliveryPackageCannelVO();
            deliveryPackageCannelVO.setId(logisticsPackageDO.getId());
            deliveryPackageCannelVOList.add(deliveryPackageCannelVO);

            //??????????????????????????????????????????????????????
//            if (logisticsPackageDO.getLogisticsStatus().equals(DeliveryPackageLogisticsStatusEnum.INDELIVERY.getCode())) {//?????????
            if (logisticsPackageDO.getLogisticsStatus().equals(DeliveryPackageLogisticsStatusEnum.CREATED.getCode())) {
                CancelDepositoryOrderResponse cancelDepositoryOrderResponse = depositoryManager.cancelOutDepositoryOrder(logisticsPackageDO.getLogisticsOrderNo());
                if (cancelDepositoryOrderResponse.getSuccessSign()) {
                    //??????????????????
                    deliveryPackageCannelVO.setStatus(cancelDepositoryOrderResponse.getStatus());

                    //?????????????????????
                    LogisticsPackageDO tempLogisticsPackageDO = new LogisticsPackageDO();
                    tempLogisticsPackageDO.setUpdateBy(UserContext.getCurrentUserName());
                    tempLogisticsPackageDO.setId(logisticsPackageDO.getId());
                    tempLogisticsPackageDO.setCancelRemark(StringUtils.isNotBlank(operationRemark) ? operationRemark : "??????????????????");        //??????????????????

                    //??????????????????
                    if (LogisticItemEnum.LogisticItemEnumWanB.DepositoryChannelOrderStatusEnum.ACCEPTED.getStatus().equals(cancelDepositoryOrderResponse.getStatus())) {
                        Integer logisticsStatus = DeliveryPackageLogisticsStatusEnum.CANCEL.getCode();       //?????????
                        tempLogisticsPackageDO.setLogisticsStatus(logisticsStatus);     //???????????????
                        tempLogisticsPackageDO.setDeliveryStatus(DeliveryPackageDeliveryStatusEnum.CANCELSEND.getCode());
//                        tempLogisticsPackageDO.setIsValid(Constant.NO_0);       //???????????????
                        tempLogisticsPackageDO.setCancelRemark("????????????????????????" + tempLogisticsPackageDO.getCancelRemark());        //??????????????????
                        cancalSendMsgToMq(logisticsPackageItemDOMap, logisticsPackageDO);
                    } else if (LogisticItemEnum.LogisticItemEnumWanB.DepositoryChannelOrderStatusEnum.REQUESTED.getStatus().equals(cancelDepositoryOrderResponse.getStatus())) {
                        requiredAgainQueryDepositoryList.add(logisticsPackageDO);
                    } else if (LogisticItemEnum.LogisticItemEnumWanB.DepositoryChannelOrderStatusEnum.REJECTED.getStatus().equals(cancelDepositoryOrderResponse.getStatus())) {
                        tempLogisticsPackageDO.setCancelRemark("????????????????????????" + tempLogisticsPackageDO.getCancelRemark());
                        deliveryPackageCannelVO.setErrorMsg(cancelDepositoryOrderResponse.getErrorMsg());
                    }
                    //?????????????????????????????????
                    logisticsPackageMapper.updateById(tempLogisticsPackageDO);
                } else {
                    deliveryPackageCannelVO.setErrorMsg(cancelDepositoryOrderResponse.getErrorMsg());
                }
            } else {
                deliveryPackageCannelVO.setErrorMsg("???????????????????????????????????????");
            }
        });
        if (!requiredAgainQueryDepositoryList.isEmpty()) {
            Runnable runnable = () -> {
                for (LogisticsPackageDO logisticsPackageDO : requiredAgainQueryDepositoryList) {
                    for (int i = 0; i < 3; i++) {
                        String status = queryCancelStatus(depositoryManager, logisticsPackageDO.getLogisticsOrderNo());
                        if (LogisticItemEnum.LogisticItemEnumWanB.DepositoryChannelOrderStatusEnum.ACCEPTED.getStatus().equals(status)) {
                            Integer logisticsStatus = DeliveryPackageLogisticsStatusEnum.CANCEL.getCode();       //?????????
                            LogisticsPackageDO tempLogisticsPackageDO = new LogisticsPackageDO();
                            tempLogisticsPackageDO.setId(logisticsPackageDO.getId());
                            tempLogisticsPackageDO.setLogisticsStatus(logisticsStatus);     //???????????????
                            tempLogisticsPackageDO.setDeliveryStatus(DeliveryPackageDeliveryStatusEnum.CANCELSEND.getCode());
//                            tempLogisticsPackageDO.setIsValid(Constant.NO_0);       //???????????????
                            tempLogisticsPackageDO.setCancelRemark("????????????????????????" + tempLogisticsPackageDO.getCancelRemark());        //??????????????????
                            //?????????????????????????????????
                            logisticsPackageMapper.updateById(tempLogisticsPackageDO);
                            //????????????????????????
                            cancalSendMsgToMq(logisticsPackageItemDOMap, logisticsPackageDO);
                            break;
                        } else if (LogisticItemEnum.LogisticItemEnumWanB.DepositoryChannelOrderStatusEnum.REJECTED.getStatus().equals(status)) {
                            LogisticsPackageDO tempLogisticsPackageDO = new LogisticsPackageDO();
                            tempLogisticsPackageDO.setId(logisticsPackageDO.getId());
                            tempLogisticsPackageDO.setCancelRemark("????????????????????????" + tempLogisticsPackageDO.getCancelRemark());        //??????????????????
                            //?????????????????????????????????
                            logisticsPackageMapper.updateById(tempLogisticsPackageDO);
                            break;
                        }
                        try {
                            TimeUnit.SECONDS.sleep(3);
                        } catch (InterruptedException e) {
                            log.error("error sleep 3 seconds ");
                        }
                    }
                }
            };
            new Thread(runnable).start();
        }
        // ??????????????????????????????
        if (deliveryPackageCannelVOList.stream().filter(item -> StringUtils.isNotBlank(item.getErrorMsg())).count() > 0) {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, deliveryPackageCannelVOList.stream().map(DeliveryPackageCannelVO::getErrorMsg).collect(Collectors.joining(",")));
        }
        return deliveryPackageCannelVOList;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean deliveredCreatePackage(DeliveryPackageDeliveredCreateReq deliveryPackageDeliveredCreateReq) {
        String logisticsCode = deliveryPackageDeliveredCreateReq.getLogisticsCode();
        String channelCode = deliveryPackageDeliveredCreateReq.getChannelCode();
        String salesOrderId = deliveryPackageDeliveredCreateReq.getSalesOrderId();
        List<DeliveryPackageDeliveredCreateReq.ItemAndBillNoReq> itemIdAndBillList = deliveryPackageDeliveredCreateReq.getItemIdList();
        List<String> billNos = itemIdAndBillList.stream().map(DeliveryPackageDeliveredCreateReq.ItemAndBillNoReq::getLogisticsWayBillNo).collect(Collectors.toList());
        List<String> itemIdList = itemIdAndBillList.stream().map(DeliveryPackageDeliveredCreateReq.ItemAndBillNoReq::getItemId).collect(Collectors.toList());
        //???????????????
        LambdaQueryWrapper<LogisticsPackageDO> logisticsPackageDOLambdaQueryWrapper = Wrappers.<LogisticsPackageDO>lambdaQuery()
                .in(LogisticsPackageDO::getLogisticsWayBillNo, billNos)
                .eq(LogisticsPackageDO::getSalesOrderId, salesOrderId);
        List<LogisticsPackageDO> logisticsPackageDOList = logisticsPackageMapper.selectList(logisticsPackageDOLambdaQueryWrapper);
        List<Long> packageIdList = logisticsPackageDOList.stream().map(LogisticsPackageDO::getId).collect(Collectors.toList());

        //????????? item ??????
        LambdaQueryWrapper<LogisticsPackageItemDO> logisticsPackageItemDOLambdaQueryWrapper = Wrappers.<LogisticsPackageItemDO>lambdaQuery()
//                .in(LogisticsPackageItemDO::getItemId, itemIdList)
                .in(LogisticsPackageItemDO::getPackageId, packageIdList);
        List<LogisticsPackageItemDO> logisticsPackageItemDOList_all = logisticsPackageItemMapper.selectList(logisticsPackageItemDOLambdaQueryWrapper);
        // ?????????????????????itemId
        List<LogisticsPackageItemDO> logisticsPackageItemDOList = logisticsPackageItemDOList_all.stream().filter(item -> itemIdList.contains(item.getItemId())).collect(Collectors.toList());
        Map<String, List<LogisticsPackageItemDO>> logisticsPackageItemDOMap = logisticsPackageItemDOList.stream().collect(Collectors.groupingBy(LogisticsPackageItemDO::getSku));

        //?????????????????????
        LambdaQueryWrapper<LogisticsProductDO> logisticsProductDOLambdaQueryWrapper = Wrappers.<LogisticsProductDO>lambdaQuery()
                .in(LogisticsProductDO::getRelationId, packageIdList)
                .eq(LogisticsProductDO::getType, PackageProductTypeEnum.DELIVERY_PACKAGE.getType());
        List<LogisticsProductDO> logisticsProductDOList = logisticsProductMapper.selectList(logisticsProductDOLambdaQueryWrapper);

      /*  //?????????????????????
        LambdaQueryWrapper<LogisticsPackageAddressDO> logisticsPackageAddressDOLambdaQueryWrapper = Wrappers.<LogisticsPackageAddressDO>lambdaQuery()
                .in(LogisticsPackageAddressDO::getPackageId, packageIdList);
        List<LogisticsPackageAddressDO> logisticsPackageAddressDOList = logisticsPackageAddressMapper.selectList(logisticsPackageAddressDOLambdaQueryWrapper);*/

        for (LogisticsPackageDO logisticsPackageDO : logisticsPackageDOList) {
            if (!logisticsPackageDO.getLogisticsStatus().equals(DeliveryPackageLogisticsStatusEnum.DELIVERED.getCode())) {
                throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "????????????????????????????????????????????????????????????????????????????????????");
            }
        }
      /*  if (logisticsPackageAddressDOList.size() == 0) {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "?????????????????????");
        }*/

        AtomicReference<Integer> atomicReferenceBattery = new AtomicReference(Constant.NO);
        AtomicReference<Integer> atomicReferenceBatteryType = new AtomicReference<>(1);
        List<String> channelOrderIdList = new ArrayList<>();
        logisticsPackageDOList.forEach(logisticsPackageDO -> {
            if (logisticsPackageDO.getBattery() == Constant.YES) {
                atomicReferenceBattery.set(Constant.YES);
                atomicReferenceBatteryType.set(logisticsPackageDO.getBatteryType());
            }

            String channelOrderId = logisticsPackageDO.getChannelOrderId();
            if (StringUtils.isNoneBlank(channelOrderId)) {
                channelOrderIdList.addAll(Arrays.asList(StringUtils.split(channelOrderId, ",")));
            }
        });


        AddressDto addressDto = getAddressDtoFromRemoteOrderService(salesOrderId);

        List<WaybillGoodDetailDto> waybillGoodDetailDtoList = new ArrayList<>();
        logisticsProductDOList.forEach(logisticsProductDO -> {
            List<LogisticsPackageItemDO> tempLogisticsPackageItemDOList = Optional.ofNullable(logisticsPackageItemDOMap.get(logisticsProductDO.getSku())).orElse(new ArrayList<>());
            tempLogisticsPackageItemDOList.forEach(logisticsPackageItemDO -> {
                WaybillGoodDetailDto waybillGoodDetailDto = new WaybillGoodDetailDto();
                waybillGoodDetailDto.setItemId(logisticsPackageItemDO.getItemId());
                waybillGoodDetailDto.setGoodsId(logisticsProductDO.getSku());
                waybillGoodDetailDto.setGoodsTitle(logisticsProductDO.getProductName());
                waybillGoodDetailDto.setDeclaredNameEn(logisticsProductDO.getProductDeclaredNameEn());
                waybillGoodDetailDto.setDeclaredNameCn(logisticsProductDO.getProductDeclaredNameCn());
                waybillGoodDetailDto.setDeclaredValue(logisticsProductDO.getProductDeclaredPrice());
                waybillGoodDetailDto.setWeight(new BigDecimal(logisticsProductDO.getProductWeight()));
                waybillGoodDetailDto.setImg(logisticsProductDO.getProductImg());
                waybillGoodDetailDto.setAttribute(logisticsProductDO.getProductAttribute());
                waybillGoodDetailDto.setPurchaseId(logisticsPackageItemDO.getPurchaseId());
                waybillGoodDetailDto.setCategoryCode(logisticsProductDO.getCategoryCode());

                waybillGoodDetailDtoList.add(waybillGoodDetailDto);
            });
        });

        LogisticOrderDto logisticOrderDto = new LogisticOrderDto();
        logisticOrderDto.setLogisticOrderNo(logisticsCommonUtils.generateDeliveryPackageNo());
        logisticOrderDto.setSalesOrderId(salesOrderId);
        logisticOrderDto.setChannelOrderIdList(channelOrderIdList);
        logisticOrderDto.setReceiverAddress(addressDto);
        logisticOrderDto.setRemark(logisticsPackageDOList.get(0).getLogisticsRemark());
        logisticOrderDto.setPackageType(logisticsPackageDOList.get(0).getDeliveryPackageType());
        logisticOrderDto.setWaybillGoodDetailDtos(waybillGoodDetailDtoList);
        logisticOrderDto.setBattery(atomicReferenceBattery.get());
        logisticOrderDto.setBatteryType(atomicReferenceBatteryType.get());
        logisticOrderDto.setLogisticCode(logisticsCode);
        logisticOrderDto.setChannel(channelCode);
        logisticOrderDto.setCreatePackageFlag(Boolean.TRUE);
        logisticOrderDto.setOperationer(UserContext.getCurrentUserName());
        List<LogisticDispatchResponseDto> logisticDispatchResponseDtoList = deliveryProductService.voucherPrepared(Collections.singletonList(logisticOrderDto));
        for (LogisticDispatchResponseDto logisticDispatchResponseDto : logisticDispatchResponseDtoList) {
            if (!logisticDispatchResponseDto.getIsSuccess()) {
                return false;
            }
        }
        // ?????????????????????????????????????????????????????????
        Map<Long, List<LogisticsPackageItemDO>> mapByPackageId = logisticsPackageItemDOList_all.stream().collect(Collectors.groupingBy(LogisticsPackageItemDO::getPackageId));
        Map<Long, List<LogisticsPackageItemDO>> mapByPackageToUpdate = logisticsPackageItemDOList.stream().collect(Collectors.groupingBy(LogisticsPackageItemDO::getPackageId));
        mapByPackageToUpdate.entrySet().forEach(item -> {
            if (mapByPackageId.get(item.getKey()).size() == item.getValue().size()) {
                // ?????? ??????
                LambdaUpdateWrapper<LogisticsPackageDO> logisticsPackageDOLambdaUpdateWrapper = Wrappers.<LogisticsPackageDO>lambdaUpdate()
                        .set(LogisticsPackageDO::getIsValid, Constant.NO_0)
                        .set(LogisticsPackageDO::getUpdateBy, UserContext.getCurrentUserName())
                        .eq(LogisticsPackageDO::getId, item.getKey());
                logisticsPackageMapper.update(null, logisticsPackageDOLambdaUpdateWrapper);
            }
            // ??????itemId
            List<Long> itemIds = item.getValue().stream().map(LogisticsPackageItemDO::getId).collect(Collectors.toList());
            if (!itemIds.isEmpty()) {
                LambdaUpdateWrapper<LogisticsPackageItemDO> logisticsPackageItemUpdate = Wrappers.<LogisticsPackageItemDO>lambdaUpdate()
                        .set(LogisticsPackageItemDO::getIsDeleted, Constant.YES)
                        .set(LogisticsPackageItemDO::getUpdateBy, UserContext.getCurrentUserName())
                        .in(LogisticsPackageItemDO::getId, itemIds);
                logisticsPackageItemMapper.update(null, logisticsPackageItemUpdate);
            }
        });

        return true;
    }


    @Override
    public Boolean refresh(DeliveryPackageOperationReq deliveryPackageOperationReq) {
        List<Long> idList = deliveryPackageOperationReq.getIdList();
        if (idList.size() == 0) {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "????????????????????????????????????");
        }

        LambdaQueryWrapper<LogisticsPackageDO> logisticsPackageDOLambdaQueryWrapper = Wrappers.<LogisticsPackageDO>lambdaQuery()
                .in(LogisticsPackageDO::getId, idList);
        List<LogisticsPackageDO> logisticsPackageDOList = logisticsPackageMapper.selectList(logisticsPackageDOLambdaQueryWrapper);
        // ?????????????????????????????????????????????????????????????????????
        List<Long> packageIds = logisticsPackageDOList.stream().map(LogisticsPackageDO::getId).collect(Collectors.toList());
        if (packageIds.isEmpty()) {
            return true;
        }
        packageIds.stream().parallel().forEach(item -> logisticsPackageTrackMapper.deleteLogisticsPackageTrackByPackageId(item));
        LambdaUpdateWrapper<LogisticsPackageDO> updateWrapper = Wrappers.<LogisticsPackageDO>lambdaUpdate()
                .set(LogisticsPackageDO::getLogisticsStatus, DeliveryPackageLogisticsStatusEnum.DELIVERED.getCode())
                .set(LogisticsPackageDO::getLogisticsNode, null)
                .set(LogisticsPackageDO::getLogisticsNodeEn, null)
                .set(LogisticsPackageDO::getReceivingGoodTime,null)
                .in(LogisticsPackageDO::getId, packageIds);
        logisticsPackageMapper.update(null,updateWrapper);
//        this.doRefresh1688DeliveryPackage(logisticsPackageDOList);
        this.refreshDeliveryPackage(logisticsPackageDOList);
        return true;
    }

    @Override
    public Boolean refreshDeliveryPackage(List<LogisticsPackageDO> list) {
        Map<Integer, List<LogisticsPackageDO>> logisticsPackageDOMap = list.stream().collect(Collectors.groupingBy(LogisticsPackageDO::getType));
        List<LogisticsPackageDO> abroadLogisticsPackageDOList = Optional.ofNullable(logisticsPackageDOMap.get(DeliveryPackageTypeEnum.ABROAD_DELIVERY_PACKAGE.getType())).orElse(new ArrayList<>());
        List<LogisticsPackageDO> aeLogisticsPackageDOList = Optional.ofNullable(logisticsPackageDOMap.get(DeliveryPackageTypeEnum.AE_DELIVERY_PACKAGE.getType())).orElse(new ArrayList<>());
        List<LogisticsPackageDO> cjlogisticsPackageDOS = Optional.ofNullable(logisticsPackageDOMap.get(DeliveryPackageTypeEnum.CJ_DELIVERY_PACKAGE.getType())).orElse(new ArrayList<>());
        if (abroadLogisticsPackageDOList.size() != 0) this.doRefresh1688DeliveryPackage(list);        //1688 ??????????????????
        if (aeLogisticsPackageDOList.size() != 0)
            this.doRefreshAeDeliveryPackage(aeLogisticsPackageDOList);      //AE ??????????????????
        if (cjlogisticsPackageDOS.size() != 0)
            this.doSegmentRefreshCjDeliveryPackage(cjlogisticsPackageDOS);      //CJ ??????????????????
        return Boolean.TRUE;
    }

    @Override
    public Boolean disable(DeliveryPackageOperationReq deliveryPackageOperationReq) {
        List<Long> idListReq = deliveryPackageOperationReq.getIdList();
        LambdaQueryWrapper<LogisticsPackageDO> logisticsPackageDOLambdaQueryWrapper = Wrappers.<LogisticsPackageDO>lambdaQuery()
                .in(LogisticsPackageDO::getId, idListReq);
        List<LogisticsPackageDO> logisticsPackageDOList = logisticsPackageMapper.selectList(logisticsPackageDOLambdaQueryWrapper);
        List<Long> idList = logisticsPackageDOList.stream().map(LogisticsPackageDO::getId).collect(Collectors.toList());
        Map<Long, LogisticsPackageDO> longLogisticsPackageDOMap = logisticsPackageDOList.stream().collect(Collectors.toMap(LogisticsPackageDO::getId, logisticsPackageDO -> logisticsPackageDO));
        for (Long id : idList) {
            LogisticsPackageDO logisticsPackageDO = longLogisticsPackageDOMap.get(id);
            if (logisticsPackageDO == null) {
                throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "????????????");
            }
            if (logisticsPackageDO.getIsValid() == Constant.NO_0) {
                throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "???????????????????????????????????????????????????");
            }
        }

        LogisticsPackageDO tempLogisticsPackageDO = new LogisticsPackageDO();
        tempLogisticsPackageDO.setIsValid(Constant.NO_0);
        tempLogisticsPackageDO.setUpdateBy(UserContext.getCurrentUserName());
        logisticsPackageMapper.update(tempLogisticsPackageDO, logisticsPackageDOLambdaQueryWrapper);
        return true;
    }

    public List<DeliveryPackageVO> getLogisticsPackageList(LambdaQueryWrapper<LogisticsPackageDO> logisticsPackageDOLambdaQueryWrapper) {
        List<LogisticsPackageDO> logisticsPackageDOListBase = logisticsPackageMapper.selectList(logisticsPackageDOLambdaQueryWrapper);
        List<LogisticsPackageDO> logisticsPackageDOList = logisticsPackageDOListBase.stream().filter(item -> item.getLogisticsStatus() != DeliveryPackageLogisticsStatusEnum.ERROR.getCode()).collect(Collectors.toList());
        List<Long> logisticsPackageIdList = logisticsPackageDOList.stream().map(LogisticsPackageDO::getId).collect(Collectors.toList());
        List<String> logisticsCodeList = logisticsPackageDOList.stream().map(LogisticsPackageDO::getLogisticsCode).collect(Collectors.toList());
        Map<Long, List<LogisticsPackageTrackDO>> logisticsPackageTrackDOMap = new HashMap<>();
        Map<Long, List<LogisticsProductDO>> logisticsProductDOMap = new HashMap<>();
        Map<String, List<LogisticsTrackNodeDO>> logisticsTrackNodeDOMap = new HashMap<>();
        Map<String, LogisticsChannelDO> logisticsChannelDOMap = new HashMap<>();
        Map<Long, List<LogisticsPackageItemDO>> logisticsPackageItemDOMap = new HashMap<>();
        if (logisticsPackageIdList.isEmpty()) {
            return Collections.singletonList(new DeliveryPackageVO());
        } else {
            List<LogisticsTrackNodeDO> logisticsTrackNodeDOList = logisticsTrackNodeMapper.selectList(Wrappers.emptyWrapper());
            logisticsTrackNodeDOMap.putAll(logisticsTrackNodeDOList.stream().collect(Collectors.groupingBy(logisticsTrackNodeDO -> logisticsTrackNodeDO.getType() + (StringUtils.isBlank(logisticsTrackNodeDO.getLogisticsCode()) ? "" : logisticsTrackNodeDO.getLogisticsCode()))));

            //??????????????????
            LambdaQueryWrapper<LogisticsPackageTrackDO> logisticsPackageTrackDOLambdaQueryWrapper = Wrappers.<LogisticsPackageTrackDO>lambdaQuery()
                    .in(LogisticsPackageTrackDO::getPackageId, logisticsPackageIdList);
            List<LogisticsPackageTrackDO> logisticsPackageTrackDOList = logisticsPackageTrackMapper.selectList(logisticsPackageTrackDOLambdaQueryWrapper);
            logisticsPackageTrackDOMap.putAll(logisticsPackageTrackDOList.stream().collect(Collectors.groupingBy(LogisticsPackageTrackDO::getPackageId)));
            //?????????????????????????????????
            LambdaQueryWrapper<LogisticsProductDO> logisticsProductDOLambdaQueryWrapper = Wrappers.<LogisticsProductDO>lambdaQuery()
                    .eq(LogisticsProductDO::getType, PackageProductTypeEnum.DELIVERY_PACKAGE.getType())
                    .in(LogisticsProductDO::getRelationId, logisticsPackageIdList);
            List<LogisticsProductDO> logisticsProductDOList = logisticsProductMapper.selectList(logisticsProductDOLambdaQueryWrapper);
            logisticsProductDOMap.putAll(logisticsProductDOList.stream().collect(Collectors.groupingBy(LogisticsProductDO::getRelationId)));
            //??????????????????????????? item ??????
            LambdaQueryWrapper<LogisticsPackageItemDO> logisticsPackageItemDOLambdaQueryWrapper = Wrappers.<LogisticsPackageItemDO>lambdaQuery()
                    .in(LogisticsPackageItemDO::getPackageId, logisticsPackageIdList);
            List<LogisticsPackageItemDO> logisticsPackageItemDOList = logisticsPackageItemMapper.selectList(logisticsPackageItemDOLambdaQueryWrapper);
            logisticsPackageItemDOMap.putAll(logisticsPackageItemDOList.stream().collect(Collectors.groupingBy(LogisticsPackageItemDO::getPackageId)));

            //??????????????????
            LambdaQueryWrapper<LogisticsChannelDO> logisticsChannelDOLambdaQueryWrapper = Wrappers.<LogisticsChannelDO>lambdaQuery()
                    .in(LogisticsChannelDO::getLogisticsCode, logisticsCodeList);
            List<LogisticsChannelDO> logisticsChannelDOList = logisticsChannelMapper.selectList(logisticsChannelDOLambdaQueryWrapper);
            logisticsChannelDOMap.putAll(logisticsChannelDOList.stream().collect(Collectors.toMap(logisticsChannelDO -> (logisticsChannelDO.getLogisticsCode() + logisticsChannelDO.getChannelCode()), logisticsChannelDO -> logisticsChannelDO)));

        }
        //?????????????????????????????????
        List<DeliveryPackageVO> deliveryPackageVOList = new ArrayList<>();
        logisticsPackageDOList.forEach(logisticsPackageDO -> {
            DeliveryPackageVO deliveryPackageVO = BeanConvertUtils.copyProperties(logisticsPackageDO, DeliveryPackageVO.class);

            List<LogisticsPackageTrackDO> tempLogisticsPackageTrackDOList = Optional.ofNullable(logisticsPackageTrackDOMap.get(logisticsPackageDO.getId())).orElse(new ArrayList<>());
            List<LogisticsProductDO> tempLogisticsProductDOList = Optional.ofNullable(logisticsProductDOMap.get(logisticsPackageDO.getId())).orElse(new ArrayList<>());
            LogisticsChannelDO logisticsChannelDO = Optional.ofNullable(logisticsChannelDOMap.get(logisticsPackageDO.getLogisticsCode() + logisticsPackageDO.getLogisticsChannel())).orElse(new LogisticsChannelDO());
            List<LogisticsPackageTrackVO> logisticsPackageTrackVOList = new ArrayList<>();
            if (logisticsPackageDO.getType().equals(DeliveryPackageTypeEnum.ABROAD_DELIVERY_PACKAGE.getType())) {
                List<LogisticsTrackNodeDO> logisticsTrackNodeDOList = Optional.ofNullable(logisticsTrackNodeDOMap.get(logisticsPackageDO.getType() + (StringUtils.isBlank(logisticsPackageDO.getLogisticsCode()) ? "" : logisticsPackageDO.getLogisticsCode()))).orElse(new ArrayList<>());
                Map<String, LogisticsTrackNodeDO> tempLogisticsTrackNodeDOMap = logisticsTrackNodeDOList.stream().collect(Collectors.toMap(LogisticsTrackNodeDO::getNode, logisticsTrackNodeDO -> logisticsTrackNodeDO));
                logisticsPackageTrackVOList.addAll(this.get1688LogisticsPackageTrack(tempLogisticsPackageTrackDOList, tempLogisticsTrackNodeDOMap));
            } else if (logisticsPackageDO.getType().equals(DeliveryPackageTypeEnum.AE_DELIVERY_PACKAGE.getType())) {
                List<LogisticsTrackNodeDO> logisticsTrackNodeDOList = Optional.ofNullable(logisticsTrackNodeDOMap.get(String.valueOf(logisticsPackageDO.getType()))).orElse(new ArrayList<>());
                logisticsPackageTrackVOList.addAll(this.getAeLogisticsPackageTrack(tempLogisticsPackageTrackDOList, logisticsTrackNodeDOList));
            } else if (logisticsPackageDO.getType().equals(DeliveryPackageTypeEnum.CJ_DELIVERY_PACKAGE.getType())) {
                List<LogisticsTrackNodeDO> logisticsTrackNodeDOList = Optional.ofNullable(logisticsTrackNodeDOMap.get(String.valueOf(logisticsPackageDO.getType()))).orElse(new ArrayList<>());
                logisticsPackageTrackVOList.addAll(this.getCJLogisticsPackageTrack(tempLogisticsPackageTrackDOList, logisticsTrackNodeDOList));
            }
            List<LogisticsProductVO> logisticsProductVOList = new ArrayList<>();
            Map<String, LogisticsProductDO> tempLogisticsProductDOMap = tempLogisticsProductDOList.stream().collect(Collectors.toMap(LogisticsProductDO::getSku, logisticsProductDO -> logisticsProductDO));
            List<LogisticsPackageItemDO> logisticsPackageItemDOList = Optional.ofNullable(logisticsPackageItemDOMap.get(logisticsPackageDO.getId())).orElse(new ArrayList<>());
            Map<String, List<LogisticsPackageItemDO>> tempLogisticsPackageItemDOMap = logisticsPackageItemDOList.stream().collect(Collectors.groupingBy(LogisticsPackageItemDO::getSku));
            tempLogisticsPackageItemDOMap.forEach((key, values) -> {
                LogisticsProductDO logisticsProductDO = Optional.ofNullable(tempLogisticsProductDOMap.get(key)).orElse(new LogisticsProductDO());
                values.forEach(logisticsPackageItemDO -> {
                    LogisticsProductVO logisticsProductVO = BeanConvertUtils.copyProperties(logisticsProductDO, LogisticsProductVO.class);
                    logisticsProductVO.setItemId(logisticsPackageItemDO.getItemId());
                    logisticsProductVO.setPurchaseId(logisticsPackageItemDO.getPurchaseId());
                    logisticsProductVOList.add(logisticsProductVO);
                });
            });

            deliveryPackageVO.setChannelCode(logisticsPackageDO.getLogisticsChannel());     //????????????????????????
            deliveryPackageVO.setChannelName(logisticsChannelDO.getChannelName());      //????????????????????????
            // ??????
            Collections.sort(logisticsPackageTrackVOList, ((o1, o2) -> o2.getTrackTime().compareTo(o1.getTrackTime())));
            deliveryPackageVO.setTrackList(logisticsPackageTrackVOList);       //????????????
            deliveryPackageVO.setProductList(logisticsProductVOList);       //????????????
            deliveryPackageVOList.add(deliveryPackageVO);
        });
        return deliveryPackageVOList;
    }

    @Override
    public File exportDeliveryPackageExcel(ExportExcelReq<DeliveryPackageReq> exportExcelReq) {
        DeliveryPackageReq deliveryPackageReq = Optional.ofNullable(exportExcelReq.getData()).orElse(new DeliveryPackageReq());
        Integer type = deliveryPackageReq.getType();
        log.info("????????????deliveryPackageReq.getType()????????????{}", type);
        if (type.equals(DeliveryPackageTypeEnum.ABROAD_DELIVERY_PACKAGE.getType()))       //?????? 1688
        {
            List<Delivery1688PackageExcel> delivery1688PackageExcelList = this.list1688DeliveryPackage(exportExcelReq);
            return FilePlusUtils.exportExcel(exportExcelReq, delivery1688PackageExcelList, Delivery1688PackageExcel.class);
        } else if (type.equals(DeliveryPackageTypeEnum.AE_DELIVERY_PACKAGE.getType()))    //?????? AE
        {
            List<DeliveryAePackageExcel> deliveryAePackageExcelList = this.listAeDeliveryPackage(exportExcelReq);
            return FilePlusUtils.exportExcel(exportExcelReq, deliveryAePackageExcelList, DeliveryAePackageExcel.class);

        } else if (type.equals(DeliveryPackageTypeEnum.CJ_DELIVERY_PACKAGE.getType())) {     //?????? CJ
            List<DeliveryAePackageExcel> deliveryAePackageExcelList = this.listAeDeliveryPackage(exportExcelReq);
            return FilePlusUtils.exportExcel(exportExcelReq, deliveryAePackageExcelList, DeliveryAePackageExcel.class);
        }
        throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200101, "??????????????????????????????");
    }

    @Override
    public Boolean updateAeDeliveryPackage(DeliveryPackageDto deliveryPackageDto) {
        this.logisticsTrackinginfoQuery(deliveryPackageDto);

        List<DeliveryPackageDto.DeliveryPackageItemDto> deliveryPackageItemDtoList = deliveryPackageDto.getList();
        deliveryPackageItemDtoList.forEach(deliveryPackageItemDto -> {
            Boolean trackFlag = deliveryPackageItemDto.getTrackFlag();
            LambdaQueryWrapper<LogisticsPackageDO> logisticsPackageDOLambdaQueryWrapper = Wrappers.<LogisticsPackageDO>lambdaQuery()
                    .eq(LogisticsPackageDO::getType, deliveryPackageDto.getType())
                    .eq(LogisticsPackageDO::getLogisticsCode, deliveryPackageItemDto.getLogisticsCode())
                    .eq(LogisticsPackageDO::getChannelOrderId, deliveryPackageItemDto.getChannelOrderId())
                    .eq(LogisticsPackageDO::getLogisticsTrackingCode, deliveryPackageItemDto.getLogisticsTrackingCode());

            //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            LogisticsPackageDO logisticsPackageDO = Optional.ofNullable(logisticsPackageMapper.selectOne(logisticsPackageDOLambdaQueryWrapper)).orElse(new LogisticsPackageDO());
            Long id = logisticsPackageDO.getId();
            ///////////////////////////??????????????????????????????/////////////////////////////////
            List<DeliveryPackageDto.DeliveryPackageTrackDto> trackList = Optional.ofNullable(deliveryPackageItemDto.getTrackList()).orElseGet(() -> Collections.emptyList());
            DeliveryPackageDto.DeliveryPackageTrackDto trackDto = trackList.stream().sorted(Comparator.comparing(DeliveryPackageDto.DeliveryPackageTrackDto::getEventDate).reversed()).findFirst().orElseGet(() -> new DeliveryPackageDto.DeliveryPackageTrackDto());
            try {
                String lastDate = simpleDateFormate.format(trackDto.getEventDate());
                if (id != null && !checkRemoteLogisticsTrackIsUpdateByLastEventTime(logisticsPackageDO, lastDate)) {
                    return;
                }
            } catch (Exception e) {
                log.error("????????????--??????AE???????????????????????????{}", trackDto.getEventDate());
            }
            ///////////////////////////////////////////////////////////////////////////
            LogisticsPackageDO tempLogisticsPackageDO = new LogisticsPackageDO();
            tempLogisticsPackageDO.setLogisticsChannelStatus(deliveryPackageItemDto.getLogisticsChannelStatus());
            tempLogisticsPackageDO.setLogisticsNodeEn(deliveryPackageItemDto.getLogisticsNode());
            List<LogisticsTrackNodeDO> logisticsTrackNodeDOList = Optional.ofNullable(deliveryPackageDto.getLogisticsTrackNodeDOList()).orElse(this.doGetLogisticsTrackNodeList(DeliveryPackageTypeEnum.AE_DELIVERY_PACKAGE.getType()));
            Map<String, List<LogisticsTrackNodeDO>> mapByNode = logisticsTrackNodeDOList.stream().collect(Collectors.groupingBy(LogisticsTrackNodeDO::getNode));
            tempLogisticsPackageDO.setLogisticsNode(Optional.ofNullable(mapByNode.get(deliveryPackageItemDto.getLogisticsNode())).orElse(Collections.singletonList(new LogisticsTrackNodeDO())).get(0).getInsideCn());
            tempLogisticsPackageDO.setLogisticsStatus(deliveryPackageItemDto.getLogisticsStatus());
            tempLogisticsPackageDO.setReceivingGoodTime(deliveryPackageItemDto.getReceivingGoodTime());

            if (id == null) {     //?????????????????????
                // ????????????????????????????????????????????????????????????????????????
                tempLogisticsPackageDO.setType(deliveryPackageDto.getType());
                tempLogisticsPackageDO.setPurchaseChannel(deliveryPackageDto.getPurchaseChannel());
                tempLogisticsPackageDO.setSalesOrderId(deliveryPackageItemDto.getSalesOrderId());
                tempLogisticsPackageDO.setChannelOrderId(deliveryPackageItemDto.getChannelOrderId());
                tempLogisticsPackageDO.setDeliveryStatus(DeliveryPackageDeliveryStatusEnum.SENDED.getCode());
                tempLogisticsPackageDO.setLogisticsCode(deliveryPackageItemDto.getLogisticsCode());
                tempLogisticsPackageDO.setLogisticsName(deliveryPackageItemDto.getLogisticsCode());
                tempLogisticsPackageDO.setLogisticsChannel(deliveryPackageItemDto.getLogisticsCode());
                tempLogisticsPackageDO.setLogisticsOrderNo(logisticsCommonUtils.generateDeliveryPackageNo());
                tempLogisticsPackageDO.setLogisticsArea(deliveryPackageItemDto.getLogisticsArea());
                tempLogisticsPackageDO.setLogisticsTrackingCode(deliveryPackageItemDto.getLogisticsTrackingCode());
                tempLogisticsPackageDO.setLogisticsWayBillNo(deliveryPackageItemDto.getLogisticsTrackingCode());
                tempLogisticsPackageDO.setDeliveryTime(deliveryPackageItemDto.getDeliveryTime());
                if (deliveryPackageItemDto.getLogisticsStatus() == null) {
                    tempLogisticsPackageDO.setLogisticsStatus(DeliveryPackageLogisticsStatusEnum.DELIVERED.getCode());
                }
                logisticsPackageMapper.insert(tempLogisticsPackageDO);
            } else {     //???????????????id, ?????????????????????
                tempLogisticsPackageDO.setId(id);
                logisticsPackageMapper.updateById(tempLogisticsPackageDO);
            }

            if (trackFlag) {    //??????????????????
                this.updateLogisticsTrack(deliveryPackageItemDto, tempLogisticsPackageDO.getId());
                LogisticsChangeMsgDto logisticsChangeMsgDto = new LogisticsChangeMsgDto();
                logisticsChangeMsgDto.setLogisticsWayBillNo(logisticsPackageDO.getLogisticsWayBillNo());
                logisticsChangeMsgDto.setLogisticsTrackingCode(logisticsPackageDO.getLogisticsTrackingCode());
                logisticsChangeMsgDto.setType(DeliveryPackageTypeEnum.AE_DELIVERY_PACKAGE.getType());
                logisticsChangeMsgDto.setNoticeTime(new Date());
                sendLogisticsChangeMesToMqToNoticePlatform(logisticsChangeMsgDto);
            }
        });
        return true;
    }

    @Override
    public List<SalesOrderLogisticsGTO.SalesOrderLogisticsResponse> getSalesOrderLogistics(SalesOrderLogisticsDto salesOrderLogisticsDto) {
        List<String> salesOrderIdList = salesOrderLogisticsDto.getSalesOrderIdList();

        //????????????????????????
        LambdaQueryWrapper<LogisticsPackageDO> logisticsPackageDOLambdaQueryWrapper = Wrappers.<LogisticsPackageDO>lambdaQuery()
                .eq(LogisticsPackageDO::getIsValid, Constant.YES)
                .in(LogisticsPackageDO::getSalesOrderId, salesOrderLogisticsDto.getSalesOrderIdList());
        List<LogisticsPackageDO> logisticsPackageDOList = logisticsPackageMapper.selectList(logisticsPackageDOLambdaQueryWrapper);
        List<Long> packageIdList = logisticsPackageDOList.stream().map(LogisticsPackageDO::getId).collect(Collectors.toList());
        Map<String, List<LogisticsPackageDO>> logisticsPackageDOMap = logisticsPackageDOList.stream().collect(Collectors.groupingBy(LogisticsPackageDO::getSalesOrderId));

        Map<Long, List<LogisticsPackageItemDO>> logisticsPackageItemDOMap = new HashMap<>();
        if (packageIdList.size() != 0) {
            //?????????????????? item ??????
            LambdaQueryWrapper<LogisticsPackageItemDO> logisticsPackageItemDOLambdaQueryWrapper = Wrappers.<LogisticsPackageItemDO>lambdaQuery()
                    .in(LogisticsPackageItemDO::getPackageId, packageIdList);
            List<LogisticsPackageItemDO> logisticsPackageItemDOList = logisticsPackageItemMapper.selectList(logisticsPackageItemDOLambdaQueryWrapper);
            logisticsPackageItemDOMap.putAll(logisticsPackageItemDOList.stream().collect(Collectors.groupingBy(LogisticsPackageItemDO::getPackageId)));
        }

        List<SalesOrderLogisticsGTO.SalesOrderLogisticsResponse> salesOrderLogisticsResponseDtoList = new ArrayList<>();
        salesOrderIdList.forEach(salesOrderId -> {
            List<LogisticsPackageDO> tempLogisticsPackageDOList = Optional.ofNullable(logisticsPackageDOMap.get(salesOrderId)).orElse(new ArrayList<>());      //??????????????????
            Map<Integer, List<LogisticsPackageDO>> tempLogisticsPackageDOMap = tempLogisticsPackageDOList.stream().collect(Collectors.groupingBy(LogisticsPackageDO::getType));
            Map<String, SalesOrderLogisticsGTO.RepeatedSalesOrderLogisticsItemResponse> salesOrderLogisticsItemResponseDtoMap = new HashMap<>();
            tempLogisticsPackageDOMap.forEach((type, logisticsPackageList) -> {
                List<SalesOrderLogisticsGTO.SalesOrderLogisticsItemResponse> salesOrderLogisticsItemResponseList = new ArrayList<>();
                DeliveryPackageTypeEnum deliveryPackageTypeEnum = DeliveryPackageTypeEnum.getDeliveryPackageTypeEnumByType(type);
                if (deliveryPackageTypeEnum.equals(DeliveryPackageTypeEnum.ABROAD_DELIVERY_PACKAGE))     // 1688 ????????????
                {
                    logisticsPackageList.forEach(logisticsPackageDO -> {
                        List<LogisticsPackageItemDO> tempLogisticsPackageItemDOList = Optional.ofNullable(logisticsPackageItemDOMap.get(logisticsPackageDO.getId())).orElse(new ArrayList<>());
                        List<String> itemIdList = tempLogisticsPackageItemDOList.stream().map(LogisticsPackageItemDO::getItemId).collect(Collectors.toList());
                        itemIdList.forEach(itemId -> {
                            SalesOrderLogisticsGTO.SalesOrderLogisticsItemResponse salesOrderLogisticsItemResponse = SalesOrderLogisticsGTO.SalesOrderLogisticsItemResponse.newBuilder()
                                    .setId(logisticsPackageDO.getId())
                                    .setItemId(itemId)
                                    .setLogisticsOrderNo(logisticsPackageDO.getLogisticsOrderNo())
                                    .setChannelOrderId(Optional.ofNullable(logisticsPackageDO.getChannelOrderId()).orElse(""))
                                    .setLogisticsName(Optional.ofNullable(logisticsPackageDO.getLogisticsName()).orElse(""))
                                    .setLogisticsNode(Optional.ofNullable(logisticsPackageDO.getLogisticsNode()).orElse(""))
                                    .build();
                            salesOrderLogisticsItemResponseList.add(salesOrderLogisticsItemResponse);
                        });
                    });
                } else      //AE ????????????
                {
                    logisticsPackageList.forEach(logisticsPackageDO -> {
                        SalesOrderLogisticsGTO.SalesOrderLogisticsItemResponse salesOrderLogisticsItemResponse = SalesOrderLogisticsGTO.SalesOrderLogisticsItemResponse.newBuilder()
                                .setId(logisticsPackageDO.getId())
                                .setLogisticsOrderNo(logisticsPackageDO.getLogisticsOrderNo())
                                .setChannelOrderId(Optional.ofNullable(logisticsPackageDO.getChannelOrderId()).orElse(""))
                                .setLogisticsName(Optional.ofNullable(logisticsPackageDO.getLogisticsName()).orElse(""))
                                .setLogisticsNode(Optional.ofNullable(logisticsPackageDO.getLogisticsNode()).orElse(""))
                                .build();
                        salesOrderLogisticsItemResponseList.add(salesOrderLogisticsItemResponse);
                    });
                }

                SalesOrderLogisticsGTO.RepeatedSalesOrderLogisticsItemResponse repeatedSalesOrderLogisticsItemResponse = SalesOrderLogisticsGTO.RepeatedSalesOrderLogisticsItemResponse.newBuilder()
                        .addAllRepeatedItem(salesOrderLogisticsItemResponseList)
                        .buildPartial();
                salesOrderLogisticsItemResponseDtoMap.put(deliveryPackageTypeEnum.getChannel(), repeatedSalesOrderLogisticsItemResponse);
            });

            //?????? salesOrderid ???????????????
            SalesOrderLogisticsGTO.SalesOrderLogisticsResponse salesOrderLogisticsResponse = SalesOrderLogisticsGTO.SalesOrderLogisticsResponse.newBuilder()
                    .setSalesOrderId(salesOrderId)
                    .putAllItemMap(salesOrderLogisticsItemResponseDtoMap)
                    .build();
            salesOrderLogisticsResponseDtoList.add(salesOrderLogisticsResponse);
        });
        return salesOrderLogisticsResponseDtoList;
    }

    @Override
    public List<SalesOrderLogisticsGTO.SalesOrderLogisticsListResponse> getSalesOrderLogisticsList(SalesOrderLogisticsDto salesOrderLogisticsDto) {
        List<String> salesOrderIdList = salesOrderLogisticsDto.getSalesOrderIdList();
        if (salesOrderIdList.size() == 0) return new ArrayList<>();

        //????????????????????????
        LambdaQueryWrapper<LogisticsPackageDO> logisticsPackageDOLambdaQueryWrapper = Wrappers.<LogisticsPackageDO>lambdaQuery()
                .eq(LogisticsPackageDO::getIsValid, Constant.YES)
                .in(LogisticsPackageDO::getSalesOrderId, salesOrderLogisticsDto.getSalesOrderIdList());
        List<LogisticsPackageDO> logisticsPackageDOList = logisticsPackageMapper.selectList(logisticsPackageDOLambdaQueryWrapper);

        Map<String, List<LogisticsPackageDO>> logisticsPackageDOMap = logisticsPackageDOList.stream().collect(Collectors.groupingBy(LogisticsPackageDO::getSalesOrderId));
        List<SalesOrderLogisticsGTO.SalesOrderLogisticsListResponse> salesOrderLogisticsListResponseResultList = new ArrayList<>();
        salesOrderIdList.forEach(salesOrderId -> {
            List<SalesOrderLogisticsGTO.RepeatedSalesOrderLogisticsItemListResponse> repeatedSalesOrderLogisticsItemListResponseList = new ArrayList<>();
            List<LogisticsPackageDO> tempLogisticsPackageDOList = Optional.ofNullable(logisticsPackageDOMap.get(salesOrderId)).orElse(new ArrayList<>());
            tempLogisticsPackageDOList.forEach(logisticsPackageDO -> {
                SalesOrderLogisticsGTO.RepeatedSalesOrderLogisticsItemListResponse repeatedSalesOrderLogisticsItemListResponse = SalesOrderLogisticsGTO.RepeatedSalesOrderLogisticsItemListResponse.newBuilder()
                        .setLogisticsName(Optional.ofNullable(logisticsPackageDO.getLogisticsName()).orElse(""))
                        .setLogisticsOrderNo(logisticsPackageDO.getLogisticsOrderNo())
                        .setLogisticsNode(Optional.ofNullable(logisticsPackageDO.getLogisticsNode()).orElse(""))
                        .build();
                repeatedSalesOrderLogisticsItemListResponseList.add(repeatedSalesOrderLogisticsItemListResponse);
            });

            SalesOrderLogisticsGTO.SalesOrderLogisticsListResponse salesOrderLogisticsListResponseResult = SalesOrderLogisticsGTO.SalesOrderLogisticsListResponse.newBuilder()
                    .setSalesOrderId(salesOrderId)
                    .addAllItemList(repeatedSalesOrderLogisticsItemListResponseList)
                    .build();

            salesOrderLogisticsListResponseResultList.add(salesOrderLogisticsListResponseResult);
        });
        return salesOrderLogisticsListResponseResultList;
    }

    @Override
    public List<ChannelOrderLogisticsGTO.ChannelOrderLogisticsResponse> getChannelLogistics(ChannelOrderLogisticsDto channelOrderLogisticsDto) {
        Integer type = channelOrderLogisticsDto.getType();
        List<String> channelOrderIdList = Optional.ofNullable(channelOrderLogisticsDto.getChannelOrderIdList()).orElse(new ArrayList<>());
        List<ChannelOrderLogisticsGTO.ChannelOrderLogisticsResponse> channelOrderLogisticsResponseList = new ArrayList<>();
        if (channelOrderIdList.size() != 0) {
            if (type.equals(DeliveryPackagePurchaseChannelEnum.ALIBABA.getType()) || type.equals(DeliveryPackagePurchaseChannelEnum.PDD.getType())
                    || type.equals(DeliveryPackagePurchaseChannelEnum.TAOBAO.getType())) {
                List<LogisticsPurchasePackageDO> logisticsPurchasePackageDOList_primary = logisticsPurchasePackageMapper.selectListByFindInSet(channelOrderIdList);
                // ????????????????????????????????????
                List<LogisticsPurchasePackageDO> logisticsPurchasePackageDOList = new ArrayList<>();
                logisticsPurchasePackageDOList_primary.stream().forEach(item -> {
                    if (item.getChannelOrderId().contains(",")) {
                        String[] channelOrderIds = item.getChannelOrderId().split(",");
                        for (String channelOrderId : channelOrderIds) {
                            LogisticsPurchasePackageDO logisticsPurchasePackageDO = new LogisticsPurchasePackageDO();
                            BeanUtils.copyProperties(item, logisticsPurchasePackageDO);
                            logisticsPurchasePackageDO.setChannelOrderId(channelOrderId);
                            logisticsPurchasePackageDOList.add(logisticsPurchasePackageDO);
                        }
                    } else {
                        logisticsPurchasePackageDOList.add(item);
                    }
                });
                Map<String, List<LogisticsPurchasePackageDO>> logisticsPurchasePackageDOMap = logisticsPurchasePackageDOList.stream().collect(Collectors.groupingBy(LogisticsPurchasePackageDO::getChannelOrderId));
                channelOrderIdList.forEach(channelOrderId -> {
                    List<ChannelOrderLogisticsGTO.ChannelOrderLogisticsItemResponse> channelOrderLogisticsItemResponseList = new ArrayList<>();
                    List<LogisticsPurchasePackageDO> tempLogisticsPurchasePackageDOList = Optional.ofNullable(logisticsPurchasePackageDOMap.get(channelOrderId)).orElse(new ArrayList<>());
                    tempLogisticsPurchasePackageDOList.forEach(logisticsPurchasePackageDO -> {
                        ChannelOrderLogisticsGTO.ChannelOrderLogisticsItemResponse channelOrderLogisticsItemResponse = ChannelOrderLogisticsGTO.ChannelOrderLogisticsItemResponse.newBuilder()
                                .setId(logisticsPurchasePackageDO.getId())
                                .setCode(Optional.ofNullable(logisticsPurchasePackageDO.getExpressBillNo()).orElseGet(() -> ""))
                                .setStatus(Optional.ofNullable(logisticsPurchasePackageDO.getStatus()).orElseGet(() -> ""))
                                .setIsWarehouse(logisticsPurchasePackageDO.getWarehousing().equals(PurchasePackageWarehousingTypeEnum.YES.getType()))
                                .setPackageNo(Optional.ofNullable(logisticsPurchasePackageDO.getPackageNo()).orElseGet(() -> ""))
                                .setExpressCompanyName(Optional.ofNullable(logisticsPurchasePackageDO.getExpressCompanyName()).orElseGet(() -> ""))
                                .setExpressCompanyCode(Optional.ofNullable(logisticsPurchasePackageDO.getExpressCompanyCode()).orElseGet(() -> ""))
                                .setExpressBillNo(Optional.ofNullable(logisticsPurchasePackageDO.getExpressBillNo()).orElseGet(() -> ""))
                                .setChannelOrderId(Optional.ofNullable(logisticsPurchasePackageDO.getChannelOrderId()).orElseGet(() -> ""))
                                .build();
                        channelOrderLogisticsItemResponseList.add(channelOrderLogisticsItemResponse);
                    });

                    ChannelOrderLogisticsGTO.ChannelOrderLogisticsResponse channelOrderLogisticsResponseDto = ChannelOrderLogisticsGTO.ChannelOrderLogisticsResponse.newBuilder()
                            .setChannelOrderId(channelOrderId)
                            .addAllItemList(channelOrderLogisticsItemResponseList)
                            .build();

                    channelOrderLogisticsResponseList.add(channelOrderLogisticsResponseDto);
                });
            } else   //AE ?????????????????????????????????
            {
                LambdaQueryWrapper<LogisticsPackageDO> logisticsPackageDOLambdaQueryWrapper = Wrappers.<LogisticsPackageDO>lambdaQuery()
                        .in(LogisticsPackageDO::getChannelOrderId, channelOrderIdList);
                List<LogisticsPackageDO> logisticsPackageDOList = logisticsPackageMapper.selectList(logisticsPackageDOLambdaQueryWrapper);
                Map<String, List<LogisticsPackageDO>> logisticsPackageDOMap = logisticsPackageDOList.stream().collect(Collectors.groupingBy(LogisticsPackageDO::getChannelOrderId));
                channelOrderIdList.forEach(channelOrderId -> {
                    List<ChannelOrderLogisticsGTO.ChannelOrderLogisticsItemResponse> channelOrderLogisticsItemResponseList = new ArrayList<>();
                    List<LogisticsPackageDO> tempLogisticsPackageDOList = Optional.ofNullable(logisticsPackageDOMap.get(channelOrderId)).orElse(new ArrayList<>());
                    tempLogisticsPackageDOList.forEach(logisticsPackageDO -> {
                        ChannelOrderLogisticsGTO.ChannelOrderLogisticsItemResponse channelOrderLogisticsItemResponse = ChannelOrderLogisticsGTO.ChannelOrderLogisticsItemResponse.newBuilder()
                                .setId(logisticsPackageDO.getId())
                                .setCode(Optional.ofNullable(logisticsPackageDO.getLogisticsWayBillNo()).orElse(""))
                                .setPackageNo(Optional.ofNullable(logisticsPackageDO.getLogisticsOrderNo()).orElseGet(() -> ""))
                                .setExpressCompanyName(Optional.ofNullable(logisticsPackageDO.getLogisticsName()).orElseGet(() -> ""))
                                .setExpressCompanyCode(Optional.ofNullable(logisticsPackageDO.getLogisticsCode()).orElseGet(() -> ""))
                                .setExpressBillNo(Optional.ofNullable(logisticsPackageDO.getLogisticsWayBillNo()).orElseGet(() -> ""))
                                .setChannelOrderId(Optional.ofNullable(logisticsPackageDO.getChannelOrderId()).orElseGet(() -> ""))
                                .build();
                        channelOrderLogisticsItemResponseList.add(channelOrderLogisticsItemResponse);
                    });


                    ChannelOrderLogisticsGTO.ChannelOrderLogisticsResponse channelOrderLogisticsResponseDto = ChannelOrderLogisticsGTO.ChannelOrderLogisticsResponse.newBuilder()
                            .setChannelOrderId(channelOrderId)
                            .addAllItemList(channelOrderLogisticsItemResponseList)
                            .build();

                    channelOrderLogisticsResponseList.add(channelOrderLogisticsResponseDto);
                });
            }
        }
        return channelOrderLogisticsResponseList;
    }

    @Override
    public List<String> getChannerOrderIdByPackageId(List<String> packageIdList) {
        LambdaQueryWrapper<LogisticsPackageDO> logisticsPackageDOLambdaQueryWrapper = Wrappers.<LogisticsPackageDO>lambdaQuery()
                .eq(LogisticsPackageDO::getIsValid, Constant.YES)
                .in(LogisticsPackageDO::getId, packageIdList);
        List<LogisticsPackageDO> logisticsPackageDOList = logisticsPackageMapper.selectList(logisticsPackageDOLambdaQueryWrapper);
        Set<String> channerOrderIdSet = logisticsPackageDOList.stream().map(LogisticsPackageDO::getChannelOrderId).collect(Collectors.toSet());
        return new ArrayList<>(channerOrderIdSet);
    }

    @Override
    public List<String> getSalesOrderIdByPackageId(List<String> packageIdList) {
        LambdaQueryWrapper<LogisticsPackageDO> logisticsPackageDOLambdaQueryWrapper = Wrappers.<LogisticsPackageDO>lambdaQuery()
                .eq(LogisticsPackageDO::getIsValid, Constant.YES)
                .in(LogisticsPackageDO::getId, packageIdList);
        List<LogisticsPackageDO> logisticsPackageDOList = logisticsPackageMapper.selectList(logisticsPackageDOLambdaQueryWrapper);
        Set<String> salesOrderIdSet = logisticsPackageDOList.stream().map(LogisticsPackageDO::getSalesOrderId).collect(Collectors.toSet());
        return new ArrayList<>(salesOrderIdSet);
    }

    @Override
    public Boolean save(LogisticsPackageAddReq logisticsPackageAddReq) {
        LogisticsPackageDO logisticsPackageDO = new LogisticsPackageDO();
        BeanUtils.copyProperties(logisticsPackageAddReq, logisticsPackageDO);
        logisticsPackageDO.setCreateBy(UserContext.getCurrentUserName());
        if (StringUtils.isNotBlank(logisticsPackageDO.getLogisticsOrderNo())) {
            LambdaQueryWrapper<LogisticsPackageDO> queryWrapper = Wrappers.<LogisticsPackageDO>lambdaQuery()
                    .eq(LogisticsPackageDO::getLogisticsOrderNo, logisticsPackageDO.getLogisticsOrderNo());
            LogisticsPackageDO oldLogisticsPackage = logisticsPackageMapper.selectOne(queryWrapper);
            if (!Objects.isNull(oldLogisticsPackage)) {
                throw new GlobalException(ExceptionEnum.REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), "???????????????" + logisticsPackageDO.getLogisticsOrderNo() + "??????????????????????????????????????????????????????");
            }
        } else {
            logisticsPackageDO.setLogisticsOrderNo(logisticsCommonUtils.generateDeliveryPackageNo());
        }
        logisticsPackageDO.setDeliveryStatus(DeliveryPackageDeliveryStatusEnum.PREPARATION.getCode());
        logisticsPackageDO.setLogisticsStatus(DeliveryPackageLogisticsStatusEnum.CREATED.getCode());
        // ????????????
        ILogisticManager logisticManager = logisticFactory.getLogisticTypeFromCode(logisticsPackageAddReq.getLogisticsCode());
        LogisticOrderSearchDto searchDto = new LogisticOrderSearchDto();
        searchDto.setOrderNo(logisticsPackageAddReq.getLogisticsWayBillNo());
        searchDto.setLogisticsOrderNo(logisticsPackageDO.getLogisticsOrderNo());
        searchDto.setWayBillNo(logisticsPackageDO.getLogisticsWayBillNo());
        LogisticPrintLabelResponseDto logisticPrintLabelResponseDto = logisticManager.getLogisticPrintLabel(searchDto);
        if (logisticPrintLabelResponseDto.isSuccessSign()) {
            logisticsPackageDO.setLogisticsLabelUrl(logisticPrintLabelResponseDto.getLogisticsLabel());
        } else {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "??????????????????");
        }
        return logisticsPackageMapper.insert(logisticsPackageDO) > 1 ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public Boolean updatePackage(LogisticsPackageAddReq logisticsPackageAddReq) {
        long total = logisticsPackageAddReq.getLogisticsPackageItems().stream()
                .filter(item -> item.getChannel().equals(DeliveryPackagePurchaseChannelEnum.AE.getType()) || item.getChannel().equals(DeliveryPackagePurchaseChannelEnum.CJ.getType())).count();
        if (total > 0) {
            throw new GlobalException(ExceptionEnum.REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), "?????????????????????????????????????????????1688??????????????????????????????");
        }
        List<LogisticsPackageItemReq> logisticsPackageItems = logisticsPackageAddReq.getLogisticsPackageItems();
        long count1 = logisticsPackageItems.stream().filter(item -> StringUtils.isNotBlank(item.getPurchaseId())).count();
        long count2 = logisticsPackageItems.stream().filter(item -> StringUtils.isNotBlank(item.getItemId())).count();
        if (count1 < 1 || count2 < 1) {
            throw new GlobalException(ExceptionEnum.REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), "??????????????????????????????????????????purchaseId,itemId??????????????????");
        }
        //????????? item ??????
        LambdaQueryWrapper<LogisticsPackageItemDO> queryWrapper = Wrappers.<LogisticsPackageItemDO>lambdaQuery()
                .in(LogisticsPackageItemDO::getPurchaseId, logisticsPackageItems.stream().map(LogisticsPackageItemReq::getPurchaseId).collect(Collectors.toList()));
        List<LogisticsPackageItemDO> logisticsPackageItemDOs = logisticsPackageItemMapper.selectList(queryWrapper);
        if (Objects.isNull(logisticsPackageItemDOs) || logisticsPackageItemDOs.size() < 1) {
            throw new GlobalException(ExceptionEnum.REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), "??????????????????????????????????????????" + logisticsPackageAddReq.getSalesOrderId() + "???????????????id??????");
        }
        Set<Long> pacakgeIdList = logisticsPackageItemDOs.stream().map(LogisticsPackageItemDO::getPackageId).collect(Collectors.toSet());
        //???????????????
        LambdaQueryWrapper<LogisticsPackageDO> queryWrapperForPackage = Wrappers.<LogisticsPackageDO>lambdaQuery()
                .eq(LogisticsPackageDO::getSalesOrderId, logisticsPackageAddReq.getSalesOrderId())
                .eq(LogisticsPackageDO::getIsValid, Constant.YES)
                .in(LogisticsPackageDO::getId, pacakgeIdList);
        List<LogisticsPackageDO> logisticsPackageDOList = logisticsPackageMapper.selectList(queryWrapperForPackage);
        if (Objects.isNull(logisticsPackageDOList) || logisticsPackageDOList.size() < 1) {
            throw new GlobalException(ExceptionEnum.REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), "??????????????????????????????????????????" + logisticsPackageAddReq.getSalesOrderId() + "????????????");
        }
        LambdaQueryWrapper<LogisticsPackageDO> newLogisticPackageQuery = Wrappers.<LogisticsPackageDO>lambdaQuery()
                .eq(LogisticsPackageDO::getLogisticsOrderNo, logisticsPackageAddReq.getLogisticsOrderNo())
                .eq(LogisticsPackageDO::getIsValid, Constant.YES);
        LogisticsPackageDO newLogisticPackage = logisticsPackageMapper.selectOne(newLogisticPackageQuery);
        if (Objects.isNull(newLogisticPackage) || Objects.isNull(newLogisticPackage.getId())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "???????????????????????????????????????????????????????????????????????????");
        }
        // ????????????????????? ??? ???????????????
        HashSet<Integer> hashSet = new HashSet(8);
        hashSet.add(DeliveryPackageDeliveryStatusEnum.PREPARATIONFAIL.getCode());
        hashSet.add(DeliveryPackageDeliveryStatusEnum.SENDED.getCode());
        if (logisticsPackageDOList.stream()
                .filter(vo -> !hashSet.contains(vo.getDeliveryStatus())).count() > 0) {
            throw new GlobalException(ExceptionEnum.REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), "????????????????????????????????????????????????????????????????????????");
        }
        ///?????????????????????
        LambdaQueryWrapper<LogisticsPackageAddressDO> queryWrapperForAdress = Wrappers.<LogisticsPackageAddressDO>lambdaQuery()
                .in(LogisticsPackageAddressDO::getPackageId, pacakgeIdList);
        List<LogisticsPackageAddressDO> logisticsPackageAddressDOList = logisticsPackageAddressMapper.selectList(queryWrapperForAdress);
        if (logisticsPackageAddressDOList.isEmpty()) {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "?????????????????????");
        }
//        LogisticsPackageAddressDO logisticsPackageAddressDO = new LogisticsPackageAddressDO();
//        BeanUtils.copyProperties(logisticsPackageAddressDOList.get(0),logisticsPackageAddressDO);
        // ??????????????????
        AddressDto addressDto = getAddressDtoFromRemoteOrderService(logisticsPackageAddReq.getSalesOrderId());
        LogisticsPackageAddressDO logisticsPackageAddressDO = new LogisticsPackageAddressDO();
        BeanUtils.copyProperties(addressDto, logisticsPackageAddressDO);
        //?????????????????????
        LambdaQueryWrapper<LogisticsProductDO> logisticsProductDOLambdaQueryWrapper = Wrappers.<LogisticsProductDO>lambdaQuery()
                .in(LogisticsProductDO::getRelationId, pacakgeIdList)
                .eq(LogisticsProductDO::getType, PackageProductTypeEnum.DELIVERY_PACKAGE.getType());
        List<LogisticsProductDO> logisticsProductDOList = logisticsProductMapper.selectList(logisticsProductDOLambdaQueryWrapper);

        // ????????????????????????????????????????????????.
        List<LogisticsPackageItemDO> addToNewPackageItems = logisticsPackageItemDOs;
        Map<Long, List<LogisticsPackageItemDO>> mapByAddToNewPackageItems = addToNewPackageItems.stream().collect(Collectors.groupingBy(LogisticsPackageItemDO::getPackageId));
        LambdaQueryWrapper<LogisticsPackageItemDO> queryWrapperForAllToNewPackage = Wrappers.<LogisticsPackageItemDO>lambdaQuery()
                .in(LogisticsPackageItemDO::getPackageId, logisticsPackageDOList.stream().map(LogisticsPackageDO::getId).collect(Collectors.toSet()));
        List<LogisticsPackageItemDO> allLogisticsItem = logisticsPackageItemMapper.selectList(queryWrapperForAllToNewPackage);
        Map<Long, List<LogisticsPackageItemDO>> mapByAllLogisticsItem = allLogisticsItem.stream().collect(Collectors.groupingBy(LogisticsPackageItemDO::getPackageId));
        // ???????????????????????????
        List<LogisticsPackageItemDO> toVaildList = new ArrayList<>();
        List<LogisticsPackageItemDO> toDeletedItemList = new ArrayList<>();
        mapByAddToNewPackageItems.entrySet().forEach(item -> {
            if (item.getValue().size() == mapByAllLogisticsItem.get(item.getKey()).size()) {
                toVaildList.addAll(item.getValue());
            } else {
                toDeletedItemList.addAll(item.getValue());
            }
        });
        // ????????????
        if (!toVaildList.isEmpty()) {
            List<LogisticsPackageDO> packageDOS = toVaildList.stream().map(item -> {
                LogisticsPackageDO logisticsPackageDO = new LogisticsPackageDO();
                logisticsPackageDO.setIsValid(Constant.NO_0);
                logisticsPackageDO.setId(item.getPackageId());
                logisticsPackageDO.setUpdateBy(StringUtils.isNotBlank(UserContext.getCurrentUserName()) ? UserContext.getCurrentUserName() : Constant.SYSTEM_ROOT);
                return logisticsPackageDO;
            }).collect(Collectors.toList());
            packageDOS.forEach(packageDO -> logisticsPackageMapper.updateById(packageDO));
        }
        // ????????????
        if (!toDeletedItemList.isEmpty()) {
            List<Long> list = toDeletedItemList.stream().map(LogisticsPackageItemDO::getId).collect(Collectors.toList());
            LambdaUpdateWrapper<LogisticsPackageItemDO> updateWrapperForDelete = Wrappers.<LogisticsPackageItemDO>lambdaUpdate()
                    .set(LogisticsPackageItemDO::getIsDeleted, Constant.YES)
                    .set(LogisticsPackageItemDO::getUpdateBy, StringUtils.isNotBlank(UserContext.getCurrentUserName()) ? UserContext.getCurrentUserName() : Constant.SYSTEM_ROOT)
                    .in(LogisticsPackageItemDO::getId, list);
            logisticsPackageItemMapper.update(null, updateWrapperForDelete);
        }
        ////////--------------------------???????????????------------------------////////////////////
        List<LogisticsPackageDO> packageDOS = logisticsPackageDOList;
        // ????????????????????????????????????item
        newLogisticPackage.setSalesOrderId(logisticsPackageAddReq.getSalesOrderId());
        newLogisticPackage.setChannelOrderId(packageDOS.stream().map(LogisticsPackageDO::getChannelOrderId).collect(Collectors.joining(",")));
        newLogisticPackage.setUpdateBy(StringUtils.isNotBlank(UserContext.getCurrentUserName()) ? UserContext.getCurrentUserName() : Constant.SYSTEM_ROOT);
        newLogisticPackage.setIsValid(Constant.YES);
        logisticsPackageMapper.updateById(newLogisticPackage);
        LambdaQueryWrapper<LogisticsPackageItemDO> queryForNewItem = Wrappers.<LogisticsPackageItemDO>lambdaQuery()
                .eq(LogisticsPackageItemDO::getPackageId, newLogisticPackage.getId());
        List<LogisticsPackageItemDO> logisticsPackageItemDOForNewPackageList = logisticsPackageItemMapper.selectList(queryForNewItem);
        List<DepositoryPurchaseStatusMsgDto> depositoryPurchaseStatusMsgDtoList = new ArrayList<>(); // mq ????????????
        if (logisticsPackageItemDOForNewPackageList.isEmpty()) {
            // ????????????
            List<LogisticsPackageItemDO> logisticsPackageItemDOList = new ArrayList<>();
            logisticsPackageItems.stream().forEach(vo -> {
                LogisticsPackageItemDO packageItem = new LogisticsPackageItemDO();
                packageItem.setPackageId(newLogisticPackage.getId());
                packageItem.setSku(vo.getSku());
                packageItem.setItemId(vo.getItemId());
                packageItem.setPurchaseId(vo.getPurchaseId());
                packageItem.setCreateBy(StringUtils.isNotBlank(UserContext.getCurrentUserName()) ? UserContext.getCurrentUserName() : Constant.SYSTEM_ROOT);
                logisticsPackageItemDOList.add(packageItem);
            });
            logisticsPackageItemMapper.insertLogisticsPackageItemBatch(logisticsPackageItemDOList);
            // ????????????
            logisticsPackageAddressDO.setId(null);
            logisticsPackageAddressDO.setPackageId(newLogisticPackage.getId());
            logisticsPackageAddressDO.setCreateBy(StringUtils.isNotBlank(UserContext.getCurrentUserName()) ? UserContext.getCurrentUserName() : Constant.SYSTEM_ROOT);
            logisticsPackageAddressMapper.insert(logisticsPackageAddressDO);
            // ??????????????????
            logisticsProductDOList.stream().forEach(item -> {
                item.setRelationId(newLogisticPackage.getId());
                item.setCreateTime(null);
                item.setUpdateTime(null);
                item.setId(null);
            });
            logisticsProductMapper.insertLogisticsProductBatch(logisticsProductDOList);
            // ?????????????????????mq
            logisticsPackageItems.stream().forEach(item -> {
                DepositoryPurchaseStatusMsgDto msgDto = new DepositoryPurchaseStatusMsgDto();
                msgDto.setPurchaseId(item.getPurchaseId());
                msgDto.setItemId(item.getItemId());
                msgDto.setSalesOrderId(logisticsPackageAddReq.getSalesOrderId());
                depositoryPurchaseStatusMsgDtoList.add(msgDto);
            });
        } else {
            // ??????????????????????????????
            Map<String, List<LogisticsPackageItemReq>> mapByAddItem = logisticsPackageItems.stream().collect(Collectors.groupingBy(LogisticsPackageItemReq::getItemId));
            // ???????????????????????????itemId
            Set<String> exsitedItemSet = logisticsPackageItemDOForNewPackageList.stream().map(LogisticsPackageItemDO::getItemId).collect(Collectors.toSet());
            // ??????????????????itemId
            exsitedItemSet.forEach(item -> mapByAddItem.remove(item));
            if (mapByAddItem.isEmpty()) {
                return Boolean.TRUE;
            }
            LambdaQueryWrapper<LogisticsProductDO> wrapperForNewProduct = Wrappers.<LogisticsProductDO>lambdaQuery()
                    .eq(LogisticsProductDO::getRelationId, newLogisticPackage.getId());
            List<LogisticsProductDO> existedProducts = logisticsProductMapper.selectList(wrapperForNewProduct);
            Set<String> existedProductSet = existedProducts.stream().map(LogisticsProductDO::getSku).collect(Collectors.toSet());
            // ???????????????????????????sku
            List<LogisticsProductDO> requiredInsertSku = logisticsProductDOList.stream().filter(item -> !existedProductSet.contains(item.getSku())).collect(Collectors.toList());
            List<LogisticsPackageItemDO> logisticsPackageItemDOList = new ArrayList<>();
            mapByAddItem.values().stream().forEach(items -> {
                items.stream().forEach(vo -> {
                    LogisticsPackageItemDO packageItem = new LogisticsPackageItemDO();
                    packageItem.setPackageId(newLogisticPackage.getId());
                    packageItem.setSku(vo.getSku());
                    packageItem.setItemId(vo.getItemId());
                    packageItem.setPurchaseId(vo.getPurchaseId());
                    packageItem.setCreateBy(StringUtils.isNotBlank(UserContext.getCurrentUserName()) ? UserContext.getCurrentUserName() : Constant.SYSTEM_ROOT);
                    logisticsPackageItemDOList.add(packageItem);
                });
            });
            logisticsPackageItemMapper.insertLogisticsPackageItemBatch(logisticsPackageItemDOList);
            if (!requiredInsertSku.isEmpty()) {
                requiredInsertSku.stream().forEach(item -> {
                    item.setRelationId(newLogisticPackage.getId());
                    item.setCreateTime(null);
                    item.setUpdateTime(null);
                });
                logisticsProductMapper.insertLogisticsProductBatch(requiredInsertSku);
            }
        }
        Integer status = Objects.isNull(newLogisticPackage.getDeliveryStatus()) ? DepositoryPurchaseStatusEnum.PREPARATION.getStatusCode() : newLogisticPackage.getDeliveryStatus();
        sendMsgUtils.sendPurchaseIntoDepositoryMsg(depositoryPurchaseStatusMsgDtoList, status);
        return Boolean.TRUE;
    }


    @Transactional
    @Override
    public Boolean insertDirectSendPackage(DeliveryPackageDto deliveryPackageDto) {
        deliveryPackageDto.getList().stream().forEach(deliveryPackageItemDto -> {
            LogisticsPackageDO tempLogisticsPackageDO = new LogisticsPackageDO();
            tempLogisticsPackageDO.setLogisticsChannelStatus(deliveryPackageItemDto.getLogisticsChannelStatus());
            tempLogisticsPackageDO.setLogisticsNodeEn(deliveryPackageItemDto.getLogisticsNode());
            tempLogisticsPackageDO.setLogisticsStatus(deliveryPackageItemDto.getLogisticsStatus());
            tempLogisticsPackageDO.setReceivingGoodTime(deliveryPackageItemDto.getReceivingGoodTime());
            tempLogisticsPackageDO.setType(deliveryPackageDto.getType());
            tempLogisticsPackageDO.setPurchaseChannel(deliveryPackageDto.getPurchaseChannel());
            tempLogisticsPackageDO.setSalesOrderId(deliveryPackageItemDto.getSalesOrderId());
            tempLogisticsPackageDO.setChannelOrderId(deliveryPackageItemDto.getChannelOrderId());
            tempLogisticsPackageDO.setDeliveryStatus(DeliveryPackageDeliveryStatusEnum.SENDED.getCode());
            tempLogisticsPackageDO.setLogisticsCode(deliveryPackageItemDto.getLogisticsCode());
            tempLogisticsPackageDO.setLogisticsName(deliveryPackageItemDto.getLogisticsName());
            tempLogisticsPackageDO.setLogisticsChannel(deliveryPackageItemDto.getLogisticsChannel());
            tempLogisticsPackageDO.setLogisticsOrderNo(logisticsCommonUtils.generateDeliveryPackageNo());
            tempLogisticsPackageDO.setLogisticsArea(deliveryPackageItemDto.getLogisticsArea());
            tempLogisticsPackageDO.setLogisticsTrackingCode(deliveryPackageItemDto.getLogisticsTrackingCode());
            tempLogisticsPackageDO.setLogisticsWayBillNo(deliveryPackageItemDto.getLogisticsWaybillNo());
            tempLogisticsPackageDO.setDeliveryTime(deliveryPackageItemDto.getDeliveryTime());
            if (deliveryPackageItemDto.getLogisticsStatus() == null) {
                tempLogisticsPackageDO.setLogisticsStatus(DeliveryPackageLogisticsStatusEnum.DELIVERED.getCode());
            }
            logisticsPackageMapper.insert(tempLogisticsPackageDO);
        });
        return true;
    }

    @Override
    public Boolean noticeUser(List<Long> idList) {
        if (Objects.isNull(idList) || idList.isEmpty()) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "???????????????????????????????????????");
        }
        LambdaQueryWrapper<LogisticsPackageDO> queryWrapper = Wrappers.<LogisticsPackageDO>lambdaQuery().in(LogisticsPackageDO::getId, idList);
        List<LogisticsPackageDO> logisticsPackageDOS = logisticsPackageMapper.selectList(queryWrapper);
        if (Objects.isNull(logisticsPackageDOS) || logisticsPackageDOS.isEmpty()) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "?????????????????????????????????");
        }
        LambdaUpdateWrapper<LogisticsPackageDO> updateWrapper = Wrappers.<LogisticsPackageDO>lambdaUpdate()
                .set(LogisticsPackageDO::getNoticeUser, Constant.YES)
                .in(LogisticsPackageDO::getId, idList);
        int i = logisticsPackageMapper.update(null, updateWrapper);
        return i > 0;
    }

    @Override
    public Boolean updateLogisticsTrackingCode(List<LogisticsTrackingCodeUpdateReq> logisticsTrackingCodeUpdateReqList) {
        Integer type = logisticsTrackingCodeUpdateReqList.get(0).getType();
        Set<String> newTrackCodeSet = logisticsTrackingCodeUpdateReqList.stream().map(LogisticsTrackingCodeUpdateReq::getNewLogisticsTrackingCode).collect(Collectors.toSet());
        Map<String, List<LogisticsTrackingCodeUpdateReq>> mapByNewLogisticsTrackCode = logisticsTrackingCodeUpdateReqList.stream().collect(Collectors.groupingBy(LogisticsTrackingCodeUpdateReq::getNewLogisticsTrackingCode));;
        Set<String> updateTrackCodeSet = new HashSet<>();
        if (DeliveryPackageTypeEnum.ABROAD_DELIVERY_PACKAGE.getType().equals(type)) {
            updateTrackCodeSet.addAll(logisticsTrackingCodeUpdateReqList.stream().map(LogisticsTrackingCodeUpdateReq::getNewLogisticsTrackingCode).collect(Collectors.toSet()));
        }else{
            updateTrackCodeSet.addAll(logisticsTrackingCodeUpdateReqList.stream().map(item -> item.getChannelOrderId()+"-"+item.getNewLogisticsTrackingCode()).collect(Collectors.toSet()));
        }
        LambdaQueryWrapper<LogisticsPackageDO> queryWrapper = Wrappers.<LogisticsPackageDO>lambdaQuery()
                .eq(LogisticsPackageDO::getType,type)
                .in(LogisticsPackageDO::getLogisticsTrackingCode, newTrackCodeSet);
        List<LogisticsPackageDO> logisticsPackageDOS = logisticsPackageMapper.selectList(queryWrapper);
        Set<String> existTrackCodeSet = new HashSet<>();
        if (!Objects.isNull(logisticsPackageDOS) && !logisticsPackageDOS.isEmpty()) {
            if (DeliveryPackageTypeEnum.ABROAD_DELIVERY_PACKAGE.getType().equals(type)) {
                existTrackCodeSet.addAll(logisticsPackageDOS.stream().map(LogisticsPackageDO::getLogisticsTrackingCode).collect(Collectors.toSet()));
            }else{
                existTrackCodeSet.addAll(logisticsPackageDOS.stream().map(item -> item.getChannelOrderId()+"-"+item.getLogisticsTrackingCode()).collect(Collectors.toSet()));
            }
            if (updateTrackCodeSet.stream().anyMatch(item -> existTrackCodeSet.contains(item))) {
                String trackCode = updateTrackCodeSet.stream().filter(item -> existTrackCodeSet.contains(item)).findFirst().get();
                throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "???????????????????????????????????????" + (trackCode.contains("-") ? StringUtils.substring(trackCode, trackCode.indexOf("-")) : trackCode));
            }
        }
       return this.doUpdateLogisticsTrackingCode(logisticsTrackingCodeUpdateReqList);
    }

    @Transactional
    @Override
    public File importLogisticsTrackingCodeExcel(String fileUrl, Integer type) {
        ImportExcelVO importExcelVO = new ImportExcelVO();
        AtomicReference<Boolean> atomicReferenceErrorFlag = new AtomicReference<>(false);
        String name = FilenameUtils.getName(fileUrl);
        String localFileName = "/data/tmp/" + name;
        File localFile = new File(localFileName);
        try {
            FileUtils.copyURLToFile(new URL(fileUrl), localFile);
            List<LogisticsTrackingCodeUpdateImportReq> logisticsTrackingCodeUpdateImportReqList = ExcelUtil.importExcel(localFileName, 0, 1, LogisticsTrackingCodeUpdateImportReq.class);
            if (logisticsTrackingCodeUpdateImportReqList.size() > 1000) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "???????????????????????????1000???");
            }
            if (logisticsTrackingCodeUpdateImportReqList.size() == 0) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "???????????????????????????0???");
            }
            // ?????????????????????
            logisticsTrackingCodeUpdateImportReqList.stream().forEach(item -> {
                item.setChannelOrderId(StringUtils.isNotBlank(item.getChannelOrderId()) ? StringUtils.deleteWhitespace(item.getChannelOrderId()) : null);
                item.setNewLogisticsTrackingCode(StringUtils.isNotBlank(item.getNewLogisticsTrackingCode()) ? StringUtils.deleteWhitespace(item.getNewLogisticsTrackingCode()) : null);
                item.setOldLogisticsTrackingCode(StringUtils.isNotBlank(item.getOldLogisticsTrackingCode()) ? StringUtils.deleteWhitespace(item.getOldLogisticsTrackingCode()) : null);
            });
            // ?????????????????????
            Set<String> oldTrackCodeSet = logisticsTrackingCodeUpdateImportReqList.stream().map(item -> item.getOldLogisticsTrackingCode()).collect(Collectors.toSet());
            if (oldTrackCodeSet.isEmpty() || oldTrackCodeSet.stream().allMatch(item ->item==null)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "??????????????????????????????");
            }
            // ?????? ???????????????  AE ??????????????? + ???????????????
            Map<String, List<LogisticsTrackingCodeUpdateImportReq>> mapByNewLogisticsTrackCode = null;
            Set<String> newTrackCodeSet = new HashSet<>();
            if (DeliveryPackageTypeEnum.ABROAD_DELIVERY_PACKAGE.getType().equals(type)) {
                mapByNewLogisticsTrackCode = logisticsTrackingCodeUpdateImportReqList.stream().collect(Collectors.groupingBy(LogisticsTrackingCodeUpdateImportReq::getNewLogisticsTrackingCode));
                newTrackCodeSet.addAll(logisticsTrackingCodeUpdateImportReqList.stream().map(LogisticsTrackingCodeUpdateImportReq::getNewLogisticsTrackingCode).collect(Collectors.toSet()));
            }else{
                mapByNewLogisticsTrackCode = logisticsTrackingCodeUpdateImportReqList.stream().collect(Collectors.groupingBy(item -> item.getChannelOrderId()+"-"+item.getNewLogisticsTrackingCode()));
                newTrackCodeSet.addAll(logisticsTrackingCodeUpdateImportReqList.stream().map(item -> item.getChannelOrderId()+"-"+item.getNewLogisticsTrackingCode()).collect(Collectors.toSet()));
            }
            mapByNewLogisticsTrackCode.entrySet().forEach(item ->{
                if (item.getValue().size() > 1) {
                    atomicReferenceErrorFlag.set(true);
                    item.getValue().stream().forEach(req ->{
                        req.setErrorMsg("??????????????????????????????????????????????????????:"+item.getValue().get(0).getNewLogisticsTrackingCode());
                    });
                }
            });
            if (!atomicReferenceErrorFlag.get()) {
                Map<String, List<LogisticsTrackingCodeUpdateImportReq>> mapByUpdateChannelTrack = null;
                Map<String, List<LogisticsPackageDO>> mapByQueryChannelTrack = null;
                List<LogisticsPackageDO> logisticsPackageDOS = null;
                if (DeliveryPackageTypeEnum.ABROAD_DELIVERY_PACKAGE.getType().equals(type)) {
                    mapByUpdateChannelTrack = logisticsTrackingCodeUpdateImportReqList.stream().filter(item -> StringUtils.isNotBlank(item.getOldLogisticsTrackingCode())).collect(Collectors.groupingBy(item ->item.getOldLogisticsTrackingCode()));
                    LambdaQueryWrapper<LogisticsPackageDO> queryForPackage = Wrappers.<LogisticsPackageDO>lambdaQuery()
                            .eq(LogisticsPackageDO::getType, DeliveryPackageTypeEnum.ABROAD_DELIVERY_PACKAGE.getType())
                            .in(LogisticsPackageDO::getLogisticsTrackingCode, oldTrackCodeSet);
                    logisticsPackageDOS = logisticsPackageMapper.selectList(queryForPackage);
                    mapByQueryChannelTrack = logisticsPackageDOS.stream().collect(Collectors.groupingBy(item -> item.getLogisticsTrackingCode()));
                }else{
                    mapByUpdateChannelTrack = logisticsTrackingCodeUpdateImportReqList.stream().filter(item -> StringUtils.isNotBlank(item.getChannelOrderId()) && StringUtils.isNotBlank(item.getOldLogisticsTrackingCode()))
                            .collect(Collectors.groupingBy(item -> item.getChannelOrderId() + "-" + item.getOldLogisticsTrackingCode()));
                    LambdaQueryWrapper<LogisticsPackageDO> queryForPackage = Wrappers.<LogisticsPackageDO>lambdaQuery()
                            .eq(LogisticsPackageDO::getType, DeliveryPackageTypeEnum.AE_DELIVERY_PACKAGE.getType())
                            .in(LogisticsPackageDO::getLogisticsTrackingCode, oldTrackCodeSet);
                    logisticsPackageDOS = logisticsPackageMapper.selectList(queryForPackage);
                    mapByQueryChannelTrack = logisticsPackageDOS.stream().collect(Collectors.groupingBy(item -> item.getChannelOrderId() + "-" + item.getLogisticsTrackingCode()));
                }
                if (logisticsPackageDOS.stream().anyMatch(item -> newTrackCodeSet.contains(item.getLogisticsTrackingCode()))) {
                    Set<String> existTrackCode = logisticsPackageDOS.stream().filter(item -> newTrackCodeSet.contains(item.getLogisticsTrackingCode())).map(item -> item.getLogisticsTrackingCode()).collect(Collectors.toSet());
                    for (String trackCode : existTrackCode) {
                        Optional.ofNullable(mapByUpdateChannelTrack.get(trackCode)).orElseGet(() ->Collections.emptyList()).forEach(item ->{
                            item.setErrorMsg("???????????????????????????????????????" + (trackCode.contains("-") ? StringUtils.substring(trackCode, trackCode.indexOf("-")) : trackCode));
                            atomicReferenceErrorFlag.set(true);
                        });
                    }
                }
                for (Map.Entry<String, List<LogisticsTrackingCodeUpdateImportReq>> item : mapByUpdateChannelTrack.entrySet()) {
                    List<LogisticsPackageDO> logisticsPackageDOS1 = mapByQueryChannelTrack.get(item.getKey());
                    if (Objects.isNull(logisticsPackageDOS1) || logisticsPackageDOS1.isEmpty()) {
                        item.getValue().get(0).setErrorMsg("????????????????????????????????????????????????");
                        atomicReferenceErrorFlag.set(true);
                    } else {
                        item.getValue().get(0).setId(logisticsPackageDOS1.get(0).getId());
                    }
                }
            }
            if (!atomicReferenceErrorFlag.get()) {
                // ??????null
                StringBuilder sb = new StringBuilder();
                logisticsTrackingCodeUpdateImportReqList.forEach(logisticsTrackingCodeUpdateImportReq ->{
                    try {
                        sb.setLength(0); // ????????????
                        List<Field> fields = Arrays.asList(logisticsTrackingCodeUpdateImportReq.getClass().getDeclaredFields());
                        List<Field> sortedFieldList = fields.stream().filter(field -> field.isAnnotationPresent(Excel.class)).sorted(Comparator.comparing(field -> field.getAnnotation(Excel.class).orderNum())).collect(Collectors.toList());
                        for (Field field : sortedFieldList) {
                            field.setAccessible(true);
                            NotBlank notBlankAnnotation = field.getAnnotation(NotBlank.class);
                            if (Objects.isNull(notBlankAnnotation)) {
                                continue;
                            }
                            Class<?>[] groups = notBlankAnnotation.groups();
                            Set<Integer> validateSet = new HashSet<>(4);
                            for (Class<?> aClass : Arrays.asList(groups)) {
                                Object instance = aClass.newInstance();
                                validateSet.add((Integer) aClass.getDeclaredField("VALUE").get(instance));
                            }
                            Object o = field.get(logisticsTrackingCodeUpdateImportReq);
                            if (Objects.isNull(o) && !Objects.isNull(notBlankAnnotation) && validateSet.contains(type)) {
                                String errorMsg = logisticsTrackingCodeUpdateImportReq.getErrorMsg();
                                sb.append(errorMsg).append(StringUtils.isNotBlank(errorMsg) ? "," : "").append(notBlankAnnotation.message());
                                logisticsTrackingCodeUpdateImportReq.setErrorMsg(sb.toString());
                                atomicReferenceErrorFlag.set(true);
                            }
                        }
                    } catch (Exception e) {
                        log.error("AE??????EXCEL??????????????????,cause {}", e);
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,e.getMessage());
                    }
                });
            }
            // ????????????
            importExcelVO.setSuccess(atomicReferenceErrorFlag.get());
            if (atomicReferenceErrorFlag.get()) {
                String uuid = UUID.randomUUID().toString();
                String errorLocalFile = "/data/tmp/" + uuid + ".xls";
                File file = new File(errorLocalFile);
                ExcelUtil.getExport(logisticsTrackingCodeUpdateImportReqList, null, "", LogisticsTrackingCodeUpdateImportReq.class, new FileOutputStream(file));
                return file;
            }
            List<LogisticsTrackingCodeUpdateReq> logisticsTrackingCodeUpdateReqList = logisticsTrackingCodeUpdateImportReqList.stream().map(item -> {
                LogisticsTrackingCodeUpdateReq codeUpdateReq = new LogisticsTrackingCodeUpdateReq();
                codeUpdateReq.setId(item.getId());
                codeUpdateReq.setType(type);
                codeUpdateReq.setNewLogisticsTrackingCode(item.getNewLogisticsTrackingCode());
                codeUpdateReq.setOldLogisticsTrackingCode(item.getOldLogisticsTrackingCode());
                return codeUpdateReq;
            }).collect(Collectors.toList());
            this.doUpdateLogisticsTrackingCode(logisticsTrackingCodeUpdateReqList);
        } catch (Exception e) {
            log.error("AE??????EXCEL??????????????????,cause {}", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, e.getMessage());
        }finally {
            localFile.delete();
        }
        return null;
    }

    private Boolean doUpdateLogisticsTrackingCode(List<LogisticsTrackingCodeUpdateReq> logisticsTrackingCodeUpdateReqList) {
        for (LogisticsTrackingCodeUpdateReq logisticsTrackingCodeUpdateReq : logisticsTrackingCodeUpdateReqList) {
            LambdaUpdateWrapper<LogisticsPackageDO> updateWrapper = Wrappers.<LogisticsPackageDO>lambdaUpdate()
                    .set(LogisticsPackageDO::getHandleTrackingCode, Constant.YES)
                    // ae \ cj
                    .set(!DeliveryPackageTypeEnum.ABROAD_DELIVERY_PACKAGE.getType().equals(logisticsTrackingCodeUpdateReq.getType()),LogisticsPackageDO::getLogisticsWayBillNo, logisticsTrackingCodeUpdateReq.getNewLogisticsTrackingCode())
                    // ??????
                    .set(DeliveryPackageTypeEnum.ABROAD_DELIVERY_PACKAGE.getType().equals(logisticsTrackingCodeUpdateReq.getType()) && StringUtils.isNotBlank(logisticsTrackingCodeUpdateReq.getNewLogisticsWaybillNo())
                            ,LogisticsPackageDO::getLogisticsWayBillNo, logisticsTrackingCodeUpdateReq.getNewLogisticsWaybillNo())
                    .set(LogisticsPackageDO::getLogisticsTrackingCode, logisticsTrackingCodeUpdateReq.getNewLogisticsTrackingCode())
                    .set(LogisticsPackageDO::getUpdateBy, StringUtils.isNotBlank(UserContext.getCurrentUserName()) ? UserContext.getCurrentUserName() : Constant.SYSTEM_ROOT)
                    .eq(LogisticsPackageDO::getId, logisticsTrackingCodeUpdateReq.getId());
            logisticsPackageMapper.update(null,updateWrapper);
        }
        return Boolean.TRUE;
    }

    private List<LogisticsPackageTrackVO> get1688LogisticsPackageTrack(List<LogisticsPackageTrackDO> tempLogisticsPackageTrackDOList, Map<String, LogisticsTrackNodeDO> tempLogisticsTrackNodeDOMap) {
        List<LogisticsPackageTrackVO> logisticsPackageTrackVOList = new ArrayList<>();
        tempLogisticsPackageTrackDOList.forEach(logisticsPackageTrackDO -> {
            String status = logisticsPackageTrackDO.getStatus();
            LogisticsTrackNodeDO logisticsTrackNodeDO = Optional.ofNullable(tempLogisticsTrackNodeDOMap.get(status)).orElse(new LogisticsTrackNodeDO());
            LogisticsPackageTrackVO logisticsPackageTrackVO = new LogisticsPackageTrackVO();
            logisticsPackageTrackVO.setNode(status);
            logisticsPackageTrackVO.setNodeDesc(logisticsTrackNodeDO.getDescCn());
            logisticsPackageTrackVO.setInsideEn(logisticsTrackNodeDO.getInsideEn());
            logisticsPackageTrackVO.setInsideCn(logisticsTrackNodeDO.getInsideCn());
            logisticsPackageTrackVO.setContent(logisticsPackageTrackDO.getContent());
            logisticsPackageTrackVO.setTimeZone(logisticsPackageTrackDO.getTimeZone());
            logisticsPackageTrackVO.setTrackTime(logisticsPackageTrackDO.getEventTime());

            logisticsPackageTrackVOList.add(logisticsPackageTrackVO);
        });
        return logisticsPackageTrackVOList;
    }

    private List<LogisticsPackageTrackVO> getAeLogisticsPackageTrack(List<LogisticsPackageTrackDO> tempLogisticsPackageTrackDOList, List<LogisticsTrackNodeDO> logisticsTrackNodeDOList) {
        List<LogisticsPackageTrackVO> logisticsPackageTrackVOList = new ArrayList<>();
        tempLogisticsPackageTrackDOList.forEach(logisticsPackageTrackDO -> {
            String status = logisticsPackageTrackDO.getStatus();

            AtomicReference<LogisticsTrackNodeDO> AtomicReferenceLogisticsTrackNodeDO = new AtomicReference(null);
            logisticsTrackNodeDOList.forEach(logisticsTrackNodeDO -> {
                if (StringUtils.indexOfIgnoreCase(status, logisticsTrackNodeDO.getNode()) != -1) {
                    AtomicReferenceLogisticsTrackNodeDO.set(logisticsTrackNodeDO);
                }
            });

            LogisticsTrackNodeDO logisticsTrackNodeDO = AtomicReferenceLogisticsTrackNodeDO.get();

            LogisticsPackageTrackVO logisticsPackageTrackVO = new LogisticsPackageTrackVO();
            logisticsPackageTrackVO.setNode(status);
            logisticsPackageTrackVO.setInsideCn(logisticsTrackNodeDO == null ? "" : logisticsTrackNodeDO.getInsideCn());
            logisticsPackageTrackVO.setInsideEn(logisticsTrackNodeDO == null ? "" : logisticsTrackNodeDO.getInsideEn());
            logisticsPackageTrackVO.setContent(logisticsPackageTrackDO.getContent());
            logisticsPackageTrackVO.setTrackTime(logisticsPackageTrackDO.getEventTime());

            logisticsPackageTrackVOList.add(logisticsPackageTrackVO);
        });
        return logisticsPackageTrackVOList;
    }

    private List<LogisticsPackageTrackVO> getCJLogisticsPackageTrack(List<LogisticsPackageTrackDO> tempLogisticsPackageTrackDOList, List<LogisticsTrackNodeDO> logisticsTrackNodeDOList) {
        List<LogisticsPackageTrackVO> logisticsPackageTrackVOList = new ArrayList<>();
        tempLogisticsPackageTrackDOList.forEach(logisticsPackageTrackDO -> {
            String status = logisticsPackageTrackDO.getStatus();
            AtomicReference<LogisticsTrackNodeDO> AtomicReferenceLogisticsTrackNodeDO = new AtomicReference(null);
            logisticsTrackNodeDOList.forEach(logisticsTrackNodeDO -> {
                if (StringUtils.indexOfIgnoreCase(status, logisticsTrackNodeDO.getNode()) != -1) {
                    AtomicReferenceLogisticsTrackNodeDO.set(logisticsTrackNodeDO);
                }
            });
            LogisticsTrackNodeDO logisticsTrackNodeDO = AtomicReferenceLogisticsTrackNodeDO.get();
            LogisticsPackageTrackVO logisticsPackageTrackVO = new LogisticsPackageTrackVO();
            logisticsPackageTrackVO.setNode(status);
            logisticsPackageTrackVO.setInsideCn(logisticsTrackNodeDO == null ? "" : logisticsTrackNodeDO.getInsideCn());
            logisticsPackageTrackVO.setInsideEn(logisticsTrackNodeDO == null ? "" : logisticsTrackNodeDO.getInsideEn());
            String content = logisticsPackageTrackDO.getContent();
            //??????????????????
            if (StringUtils.isNotEmpty(content)) {
                content = DesensibilisationUtil.toLowerCase(content);
                content = DesensibilisationUtil.cleanAddress(content);
                content = DesensibilisationUtil.cjCityReplace(content);
                logisticsPackageTrackVO.setContent(content);
            }
            logisticsPackageTrackVO.setTrackTime(logisticsPackageTrackDO.getEventTime());
            logisticsPackageTrackVOList.add(logisticsPackageTrackVO);
        });
        return logisticsPackageTrackVOList;
    }

    private Boolean updateLogisticsTrack(DeliveryPackageDto.DeliveryPackageItemDto deliveryPackageItemDto, Long packageId) {
        //???????????????????????????????????????????????????????????????
        logisticsPackageTrackMapper.deleteLogisticsPackageTrackByPackageId(packageId);

        List<LogisticsPackageTrackDO> logisticsPackageTrackDOList = new ArrayList<>();
        List<DeliveryPackageDto.DeliveryPackageTrackDto> deliveryPackageTrackDtoList = deliveryPackageItemDto.getTrackList();
        deliveryPackageTrackDtoList.forEach(deliveryPackageTrackDto -> {
            LogisticsPackageTrackDO logisticsPackageTrackDO = BeanConvertUtils.copyProperties(deliveryPackageTrackDto, LogisticsPackageTrackDO.class);
            logisticsPackageTrackDO.setId(SnowflakeIdUtils.getId());
            logisticsPackageTrackDO.setPackageId(packageId);
            logisticsPackageTrackDO.setStatus(StringUtils.substring(logisticsPackageTrackDO.getStatus(), 0, 80));
            logisticsPackageTrackDO.setEventTime(DateFormatUtils.format(deliveryPackageTrackDto.getEventDate(), "yyyy-MM-dd HH:mm:ss"));
            logisticsPackageTrackDOList.add(logisticsPackageTrackDO);
        });

        logisticsPackageTrackMapper.insertLogisticsPackageTrackBatch(logisticsPackageTrackDOList);
        return true;
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????
     *
     * @param logisticsPackageTrackDOList ??????????????????
     * @param packageId                   ????????????ID
     * @return ????????????
     * @auth qiuhao
     */
    private Boolean updateLogisticsTrack(List<LogisticsPackageTrackDO> logisticsPackageTrackDOList, Long packageId) {
        //???????????????????????????????????????????????????????????????
        logisticsPackageTrackMapper.deleteLogisticsPackageTrackByPackageId(packageId);
        logisticsPackageTrackMapper.insertLogisticsPackageTrackBatch(logisticsPackageTrackDOList);
        return true;
    }

    private Boolean updateLogisticsPackage(LogisticsPackageDO logisticsPackageDO) {
        LambdaUpdateWrapper<LogisticsPackageDO> objectLambdaUpdateWrapper = Wrappers.lambdaUpdate();
        objectLambdaUpdateWrapper.eq(LogisticsPackageDO::getId, logisticsPackageDO.getId())
                .set(LogisticsPackageDO::getReceivingGoodTime, logisticsPackageDO.getReceivingGoodTime())
                .set(LogisticsPackageDO::getCjLogisticsStatus, logisticsPackageDO.getCjLogisticsStatus())
                .set(LogisticsPackageDO::getLogisticsNode, logisticsPackageDO.getLogisticsNode());
        logisticsPackageMapper.update(logisticsPackageDO, objectLambdaUpdateWrapper);
        return true;
    }

    @Transactional
    Boolean updateLogistics(List<LogisticsPackageTrackDO> logisticsPackageTrackDOList, Long packageId, LogisticsPackageDO logisticsPackageDO) {
        //????????????????????????
        updateLogisticsTrack(logisticsPackageTrackDOList, packageId);
        //???????????????????????????
        updateLogisticsPackage(logisticsPackageDO);
        return true;
    }


    private List<Delivery1688PackageExcel> list1688DeliveryPackage(ExportExcelReq<DeliveryPackageReq> exportExcelReq) {
        DeliveryPackageReq deliveryPackageReq = Optional.ofNullable(exportExcelReq.getData()).orElse(new DeliveryPackageReq());
        if (exportExcelReq.getIdList() != null && exportExcelReq.getIdList().size() != 0)        //?????? id  List
        {
            deliveryPackageReq.setIdList(exportExcelReq.getIdList());
        }

        List<LogisticsPackageDO> logisticsPackageDOList = logisticsPackageMapper.queryLogisticsPackageList(null, deliveryPackageReq);
        List<Delivery1688PackageExcel> delivery1688PackageExcelList = new ArrayList<>();
        logisticsPackageDOList.forEach(logisticsPackageDO -> {
            Delivery1688PackageExcel delivery1688PackageExcel = new Delivery1688PackageExcel();
            delivery1688PackageExcel.setLogisticsOrderNo(logisticsPackageDO.getLogisticsOrderNo());
            delivery1688PackageExcel.setLogisticsWayBillNo(logisticsPackageDO.getLogisticsWayBillNo());
            delivery1688PackageExcel.setIsValid(logisticsPackageDO.getIsValid() == Constant.YES ? "???" : "???");
            delivery1688PackageExcel.setLogisticsTrackingCode(logisticsPackageDO.getLogisticsTrackingCode());
            delivery1688PackageExcel.setLogisticsName(logisticsPackageDO.getLogisticsName());
            delivery1688PackageExcel.setChannelName(logisticsPackageDO.getChannelName());
            delivery1688PackageExcel.setLogisticsArea(logisticsPackageDO.getLogisticsArea());
            delivery1688PackageExcel.setDeliveryStatus(DeliveryPackageDeliveryStatusEnum.getDeliveryPackageDeliveryStatusEnumByCode(logisticsPackageDO.getDeliveryStatus()).getName());
            delivery1688PackageExcel.setLogisticsNode(logisticsPackageDO.getLogisticsNode());
            delivery1688PackageExcel.setSalesOrderId(logisticsPackageDO.getSalesOrderId());
            delivery1688PackageExcel.setErrorInfo(logisticsPackageDO.getErrorInfo());
            delivery1688PackageExcel.setNoticeUser(logisticsPackageDO.getNoticeUser().equals(Constant.YES) ? "???" : "???");
            if (logisticsPackageDO.getCreateTime() != null) {
                delivery1688PackageExcel.setCreateTime(DateFormatUtils.format(logisticsPackageDO.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            }
            if (logisticsPackageDO.getUpdateTime() != null) {
                delivery1688PackageExcel.setUpdateTime(DateFormatUtils.format(logisticsPackageDO.getUpdateTime(), "yyyy-MM-dd HH:mm:ss"));
            }
            if (logisticsPackageDO.getAcceptTime() != null) {
                delivery1688PackageExcel.setAcceptTime(DateFormatUtils.format(logisticsPackageDO.getAcceptTime(), "yyyy-MM-dd HH:mm:ss"));
            }
            if (logisticsPackageDO.getDeliveryTime() != null)
                delivery1688PackageExcel.setDeliveryTime(DateFormatUtils.format(logisticsPackageDO.getDeliveryTime(), "yyyy-MM-dd HH:mm:ss"));
            if (logisticsPackageDO.getReceivingGoodTime() != null)
                delivery1688PackageExcel.setReceivingGoodTime(DateFormatUtils.format(logisticsPackageDO.getReceivingGoodTime(), "yyyy-MM-dd HH:mm:ss"));
            delivery1688PackageExcelList.add(delivery1688PackageExcel);
        });
        return delivery1688PackageExcelList;
    }


    private List<DeliveryAePackageExcel> listAeDeliveryPackage(ExportExcelReq<DeliveryPackageReq> exportExcelReq) {
        DeliveryPackageReq deliveryPackageReq = Optional.ofNullable(exportExcelReq.getData()).orElse(new DeliveryPackageReq());
        if (exportExcelReq.getIdList() != null && exportExcelReq.getIdList().size() != 0)        //?????? id  List
        {
            deliveryPackageReq.setIdList(exportExcelReq.getIdList());
        }

        List<LogisticsPackageDO> logisticsPackageDOList = logisticsPackageMapper.queryLogisticsPackageList(null, deliveryPackageReq);
        List<DeliveryAePackageExcel> deliveryAePackageExcelList = new ArrayList<>();
        logisticsPackageDOList.forEach(logisticsPackageDO -> {
            DeliveryAePackageExcel deliveryAePackageExcel = new DeliveryAePackageExcel();
            deliveryAePackageExcel.setLogisticsOrderNo(logisticsPackageDO.getLogisticsOrderNo());
            deliveryAePackageExcel.setLogisticsTrackingCode(logisticsPackageDO.getLogisticsTrackingCode());
            deliveryAePackageExcel.setChannelName(logisticsPackageDO.getChannelName());
            deliveryAePackageExcel.setLogisticsNode(logisticsPackageDO.getLogisticsNode());
            deliveryAePackageExcel.setChannelOrderId(logisticsPackageDO.getChannelOrderId());
            deliveryAePackageExcel.setCreateTime(DateFormatUtils.format(logisticsPackageDO.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            if (logisticsPackageDO.getReceivingGoodTime() != null)
                deliveryAePackageExcel.setReceivingGoodTime(DateFormatUtils.format(logisticsPackageDO.getReceivingGoodTime(), "yyyy-MM-dd HH:mm:ss"));
            deliveryAePackageExcel.setUpdateTime(DateFormatUtils.format(logisticsPackageDO.getUpdateTime(), "yyyy-MM-dd HH:mm:ss"));
            deliveryAePackageExcelList.add(deliveryAePackageExcel);
        });
        return deliveryAePackageExcelList;
    }

    private OutDepositoryOrderDto getOutDepositoryOrderDto(LogisticsPackageDO logisticsPackageDO, List<LogisticsProductDO> logisticsProductDOList, LogisticsPackageAddressDO logisticsPackageAddressDO) {
        OutDepositoryOrderDto outDepositoryOrderDto = new OutDepositoryOrderDto();
        outDepositoryOrderDto.setDispatchOrderId(logisticsPackageDO.getLogisticsOrderNo());
        outDepositoryOrderDto.setSalesOrderId(logisticsPackageDO.getSalesOrderId());
        outDepositoryOrderDto.setAddress(this.getAddress(logisticsPackageAddressDO));
        outDepositoryOrderDto.setExpress(this.getExpress(logisticsPackageDO));
        outDepositoryOrderDto.setRemark(logisticsPackageDO.getLogisticsRemark());
        outDepositoryOrderDto.setOrderItems(this.getOutDepositoryOrderItem(logisticsProductDOList));
        outDepositoryOrderDto.setEstimatedWeight(logisticsPackageDO.getLogisticsWeight());
        return outDepositoryOrderDto;
    }

    private AddressDto getAddress(LogisticsPackageAddressDO logisticsPackageAddressDO) {
        AddressDto addressDto = new AddressDto();
        addressDto.setFirstName(logisticsPackageAddressDO.getFirstName());
        addressDto.setLastName(logisticsPackageAddressDO.getLastName());
        addressDto.setCompany(logisticsPackageAddressDO.getCompany());
        addressDto.setCountryCode(logisticsPackageAddressDO.getCountryCode());
        addressDto.setProvince(logisticsPackageAddressDO.getProvince());
        addressDto.setCity(logisticsPackageAddressDO.getCity());
        addressDto.setStreet1(logisticsPackageAddressDO.getStreet1());
        addressDto.setStreet2(logisticsPackageAddressDO.getStreet2());
        addressDto.setPostCode(logisticsPackageAddressDO.getPostCode());
        addressDto.setTel(logisticsPackageAddressDO.getTel());
        addressDto.setEmail(logisticsPackageAddressDO.getEmail());
        addressDto.setTaxNumber(logisticsPackageAddressDO.getTaxNumber());
        return addressDto;
    }

    private ExpressDto getExpress(LogisticsPackageDO logisticsPackageDO) {
        ILogisticManager logisticManager = logisticFactory.getLogisticTypeFromCode(logisticsPackageDO.getLogisticsCode());

        LogisticOrderSearchDto logisticOrderSearchDto = new LogisticOrderSearchDto();
        logisticOrderSearchDto.setOrderNo(logisticsPackageDO.getLogisticsProcessCode());
        logisticOrderSearchDto.setLogisticsOrderNo(logisticsPackageDO.getLogisticsOrderNo());
        logisticOrderSearchDto.setWayBillNo(logisticsPackageDO.getLogisticsWayBillNo());
        ExpressDto expressDto = new ExpressDto();
        //???????????????
        if (StringUtils.isBlank(logisticsPackageDO.getLogisticsLabelUrl())) {
            LogisticPrintLabelResponseDto logisticPrintLabelResponseDto = logisticManager.getLogisticPrintLabel(logisticOrderSearchDto);
            if (logisticPrintLabelResponseDto.isSuccessSign()) {
                expressDto.setTrackingNumber(logisticsPackageDO.getLogisticsTrackingCode());     //?????????????????????
                expressDto.setLabelUrl(logisticPrintLabelResponseDto.getLogisticsLabel());
                expressDto.setServiceId(WBDepositoryRegisterServiceConfig.getServicerIdFromCode(logisticsPackageDO.getLogisticsCode(), logisticsPackageDO.getLogisticsChannel()));

            } else {
                throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "??????????????????");
            }
        }
//        //???????????????
//        if (StringUtils.isBlank(logisticsPackageDO.getLogisticsTrackingCode())) {
//            logisticOrderSearchDto.setProcessCode(logisticsPackageDO.getLogisticsProcessCode());
//            LogisticOrderDetailResponseDto logisticDetail = logisticManager.getLogisticDetail(logisticOrderSearchDto);
//            if (logisticDetail.isSuccessSign()) {
//                expressDto.setTrackingNumber(logisticDetail.getTrackingNumber());
//            } else {
//                throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "?????????????????????");
//            }
//        }
        return expressDto;
    }

    private List<OutDepositoryOrderItemDto> getOutDepositoryOrderItem(List<LogisticsProductDO> logisticsProductDOList) {
        List<OutDepositoryOrderItemDto> outDepositoryOrderItemDtoList = new ArrayList<>();
        logisticsProductDOList.forEach(logisticsProductDO -> {
            OutDepositoryOrderItemDto outDepositoryOrderItemDto = new OutDepositoryOrderItemDto();
            outDepositoryOrderItemDto.setSku(logisticsProductDO.getSku());
            outDepositoryOrderItemDto.setQuantity(logisticsProductDO.getProductCount());
            outDepositoryOrderItemDtoList.add(outDepositoryOrderItemDto);
        });

        return outDepositoryOrderItemDtoList;
    }

    private void doRefresh1688DeliveryPackage(List<LogisticsPackageDO> logisticsPackageDOList) {
        for (LogisticsPackageDO logisticsPackageDO : logisticsPackageDOList) {
            if (logisticsPackageDO.getType().equals(DeliveryPackageTypeEnum.ABROAD_DELIVERY_PACKAGE.getType())) {        //????????????????????????????????????????????????
                // ?????????????????????????????????????????????Job????????????---?????????????????????????????????
                try {
                    applicationContext.publishEvent(new LogisticPackageAgainPushEvent(logisticsPackageDO));
                    String logisticsTrackingCode = logisticsPackageDO.getLogisticsTrackingCode();       //??????????????????
                    ILogisticManager logisticManager = logisticFactory.getLogisticTypeFromCode(logisticsPackageDO.getLogisticsCode());
                    LogisticOrderSearchDto logisticOrderSearchDto = new LogisticOrderSearchDto();
                    logisticOrderSearchDto.setLogisticsOrderNo(logisticsPackageDO.getLogisticsOrderNo());
                    logisticOrderSearchDto.setProcessCode(logisticsPackageDO.getLogisticsProcessCode());
                    logisticOrderSearchDto.setWayBillNo(logisticsPackageDO.getLogisticsWayBillNo());
                    //???????????????????????????????????????????????????????????????????????????
                    LogisticsPackageDO tempLogisticsPackageDO = new LogisticsPackageDO();
                    tempLogisticsPackageDO.setId(logisticsPackageDO.getId());
                    tempLogisticsPackageDO.setSalesOrderId(logisticsPackageDO.getSalesOrderId());
                    tempLogisticsPackageDO.setLogisticsTrackingCode(logisticsTrackingCode);
                    if (StringUtils.isBlank(logisticsTrackingCode)) {
                        LogisticOrderDetailResponseDto logisticOrderDetailResponseDto = logisticManager.getLogisticDetail(logisticOrderSearchDto);
                        //???????????????????????????????????????????????????
                        if (!logisticOrderDetailResponseDto.isSuccessSign() || StringUtils.isBlank(logisticOrderDetailResponseDto.getTrackingNumber())) {
                            continue;
                        }
                        logisticsTrackingCode = logisticOrderDetailResponseDto.getTrackingNumber();
                        tempLogisticsPackageDO.setLogisticsTrackingCode(logisticsTrackingCode);
                        if (StringUtils.isNoneBlank(logisticOrderDetailResponseDto.getChannelTrackingNumber())) {
                            tempLogisticsPackageDO.setLogisticsChannelTrackingCode(logisticOrderDetailResponseDto.getChannelTrackingNumber());
                        }
                    }
                    logisticOrderSearchDto.setTrackingNumber(logisticsTrackingCode);
                    LogisticTrackResponseDto logisticTrackResponseDto = logisticManager.getLogisticTrackList(logisticOrderSearchDto);
                    boolean isEndToSendMessage = false;
                    if (logisticTrackResponseDto.isSuccessSign()) {
                        List<TrackPointDto> trackPointDtoList = logisticTrackResponseDto.getTrackPointDtos();
                        TrackPointDto lastTrackPoint = trackPointDtoList.stream().sorted(Comparator.comparing(TrackPointDto::getEventTime).reversed()).findFirst().orElseGet(() -> TrackPointDto.builder().build());
                        boolean isUpdate = checkRemoteLogisticsTrackIsUpdateByLastEventTime(logisticsPackageDO, lastTrackPoint.getEventTime());
                        if (!isUpdate) {
                            continue;
                        }
                        String lastNode = logisticTrackResponseDto.getLastNode();
                        Boolean isEnd = logisticTrackResponseDto.getIsEnd();

                        // ?????????????????????  @qiuhao??????????????????????????????????????????
                        if (logisticsPackageDO.getLogisticsReceived() == null || !logisticsPackageDO.getLogisticsReceived()) {
                            List<TrackPointDto> logisticCompanyReceivedPackage = logisticManager.getLogisticCompanyReceivedPackage(logisticTrackResponseDto);
                            if (logisticCompanyReceivedPackage != null && logisticCompanyReceivedPackage.size() > 0) {
                                TrackPointDto trackPointDto = logisticCompanyReceivedPackage.get(0);
                                tempLogisticsPackageDO.setDeliveryStatus(DeliveryPackageDeliveryStatusEnum.SENDED.getCode());
                                tempLogisticsPackageDO.setLogisticsReceived(Constant.STATUS_YES);
                                if (trackPointDto != null) {
                                    String eventTime = trackPointDto.getEventTime();
                                    if (eventTime != null) {
                                        String s = StringUtil.cleanUpTheLetter(eventTime);
                                        if (s != null) {
                                            DateTime parse = DateUtil.parse(s);
                                            tempLogisticsPackageDO.setAcceptTime(parse);
                                        }
                                    }
                                }
                                if (tempLogisticsPackageDO.getAcceptTime() == null) {
                                    log.info("????????????????????????????????????????????????????????????????????????" + JSON.toJSONString(logisticsPackageDOList));
                                }
                            }
                        }
                        if (StringUtils.isNoneBlank(lastNode)) {      //????????????????????????
                            tempLogisticsPackageDO.setLogisticsNodeEn(lastNode);
                            List<LogisticsTrackNodeDO> logisticsTrackNodeDOS = doGetLogisticsTrackNodeList(DeliveryPackageTypeEnum.ABROAD_DELIVERY_PACKAGE.getType());
                            Map<String, List<LogisticsTrackNodeDO>> mapByNode = logisticsTrackNodeDOS.stream().collect(Collectors.groupingBy(LogisticsTrackNodeDO::getNode));
                            tempLogisticsPackageDO.setLogisticsNode(Optional.ofNullable(mapByNode.get(lastNode)).orElse(Collections.singletonList(new LogisticsTrackNodeDO())).get(0).getInsideCn());
                        }

                        if (isEnd) {       //???????????????????????????
                            isEndToSendMessage = true;
                            tempLogisticsPackageDO.setLogisticsStatus(DeliveryPackageLogisticsStatusEnum.RECEIVED.getCode());       //?????????
                            String receiveGoodTimeStr = logisticTrackResponseDto.getReceiveGoodTime();
                            Date receiveGoodTime = null;
                            if (StringUtils.isNotBlank(receiveGoodTimeStr)) {
                                if (receiveGoodTimeStr.contains("T")) {
                                    receiveGoodTimeStr = receiveGoodTimeStr.replaceAll("'", "").replaceAll("T", " ");
                                }
                                try {
                                    receiveGoodTime = simpleDateFormate.parse(receiveGoodTimeStr);
                                } catch (ParseException e) {
                                    log.error("???????????????????????????????????????{}", receiveGoodTimeStr);
                                }
                            }
                            tempLogisticsPackageDO.setReceivingGoodTime(Objects.isNull(receiveGoodTime) ? new Date() : receiveGoodTime);        //??????????????????
                        }

                        List<LogisticsPackageTrackDO> logisticsPackageTrackDOList = new ArrayList<>();
                        trackPointDtoList.forEach(trackPointDto -> {
                            LogisticsPackageTrackDO logisticsPackageTrackDO = new LogisticsPackageTrackDO();
                            logisticsPackageTrackDO.setId(SnowflakeIdUtils.getId());
                            logisticsPackageTrackDO.setPackageId(logisticsPackageDO.getId());
                            logisticsPackageTrackDO.setTimeZone(trackPointDto.getTimeZone());
                            logisticsPackageTrackDO.setStatus(trackPointDto.getStatus());
                            logisticsPackageTrackDO.setContent(trackPointDto.getContent());
                            logisticsPackageTrackDO.setLocation(trackPointDto.getLocation());
                            logisticsPackageTrackDO.setEventTime(trackPointDto.getEventTime());

                            logisticsPackageTrackDOList.add(logisticsPackageTrackDO);
                        });
                        logisticsPackageTrackMapper.deleteLogisticsPackageTrackByPackageId(logisticsPackageDO.getId());     //??????????????????
                        if (logisticsPackageTrackDOList.size() != 0)
                            logisticsPackageTrackMapper.insertLogisticsPackageTrackBatch(logisticsPackageTrackDOList);     //????????????????????????
                    }

                    //??????????????????
                    logisticsPackageMapper.updateById(tempLogisticsPackageDO);
                    LogisticsChangeMsgDto logisticsChangeMsgDto = new LogisticsChangeMsgDto();
                    logisticsChangeMsgDto.setLogisticsWayBillNo(logisticsPackageDO.getLogisticsWayBillNo());
                    logisticsChangeMsgDto.setLogisticsTrackingCode(logisticsPackageDO.getLogisticsTrackingCode());
                    logisticsChangeMsgDto.setType(DeliveryPackageTypeEnum.ABROAD_DELIVERY_PACKAGE.getType());
                    logisticsChangeMsgDto.setNoticeTime(new Date());
                    sendLogisticsChangeMesToMqToNoticePlatform(logisticsChangeMsgDto);
                    if (isEndToSendMessage) {
                        sendReceivedStatusToMq(tempLogisticsPackageDO);
                    }
                } catch (Exception e) {
                    log.error("??????1688???????????????????????????????????????{}??????????????????{}", logisticsPackageDO, e);
                }
            }
        }
    }

    @Autowired
    @Getter
    private RestTemplate restTemplate;

    public void doSegmentRefreshCjDeliveryPackage(List<LogisticsPackageDO> logisticsPackageDOList) {
        //????????????
        List<List> segment = ListUtil.segment(logisticsPackageDOList, 20);
        for (List list : segment) {
            try {
                //????????????????????????
                doRefreshCjDeliveryPackage(list);
            }catch (Exception e){
                log.error("??????CJ??????????????????" + e.getMessage());
            }

            //??????????????????  ????????????
            try {
                if (list != null) {
                    ArrayList<String> strings = new ArrayList<>();
                    for (Object logisticsPackageDO : list) {
                        LogisticsPackageDO a = (LogisticsPackageDO) logisticsPackageDO;
                        strings.add(a.getChannelCode());
                    }
                    CjChannelOrderGTO.ListCjChanelOrderInfoRequest build = CjChannelOrderGTO.ListCjChanelOrderInfoRequest.newBuilder().addAllChannelOrderId(strings).build();
                    //????????????
                    CjChannelOrderGTO.ListCjChanelOrderInfoReply listCjChanelOrderInfoReply = cjChannelOrderClient.listCjChannelOrderInfo(build);
                    //??????????????????
                    if (listCjChanelOrderInfoReply != null && listCjChanelOrderInfoReply.getSuccess()) {
                        List<CjChannelOrderGTO.CjChannelOrderInfo> dataList = listCjChanelOrderInfoReply.getDataList();
                        if (dataList != null && dataList.size() > 0) {
                            for (CjChannelOrderGTO.CjChannelOrderInfo cjChannelOrderInfo : dataList) {
                                String orderStatus = cjChannelOrderInfo.getOrderStatus();
                                if (orderStatus != null) {
                                    //?????????????????????????????????  ???????????????????????????????????????
                                    if (orderStatus.equals(CjOrderStatusEnum.DELIVERED.getStatus())) {
                                        updateLogisticsStatus(cjChannelOrderInfo.getChannelOrderId(), DeliveryPackageLogisticsStatusEnum.RECEIVED.getCode());
                                    }
                                    if (orderStatus.equals((CjOrderStatusEnum.CANCELLED.getStatus()))) {
                                        updateLogisticsStatus(cjChannelOrderInfo.getChannelOrderId(), DeliveryPackageLogisticsStatusEnum.CANCEL.getCode());
                                    }
                                }
                            }
                        } else {
                            log.info("?????????????????????????????????????????????:" + dataList);
                        }
                    } else {
                        //????????????????????????
                        if (listCjChanelOrderInfoReply != null) {
                            log.error("??????????????????????????????????????????:" + listCjChanelOrderInfoReply.getErrorCode() + listCjChanelOrderInfoReply.getMsg());
                        } else {
                            log.error("??????????????????????????????????????????:" + "null");
                        }
                    }
                }
            } catch (Exception e) {
                log.error("????????????????????????????????????:" + e.getMessage());
            } finally {
                continue;
            }
        }

    }


    public void updateLogisticsStatus(String cjChannelOrderId, Integer logisticsStatus) {
        //30  ?????????(?????????)   50 ?????????
        //channel_order_id
        LambdaUpdateWrapper<LogisticsPackageDO> objectLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        objectLambdaUpdateWrapper.set(LogisticsPackageDO::getLogisticsStatus, logisticsStatus);
        objectLambdaUpdateWrapper.eq(LogisticsPackageDO::getChannelOrderId, cjChannelOrderId);
        LogisticsPackageDO logisticsPackageDO = new LogisticsPackageDO();
        logisticsPackageDO.setLogisticsStatus(logisticsStatus);
        logisticsPackageDO.setChannelOrderId(cjChannelOrderId);
        logisticsPackageMapper.update(logisticsPackageDO, objectLambdaUpdateWrapper);
    }


    public void doRefreshCjDeliveryPackage(List<LogisticsPackageDO> logisticsPackageDOList) {
        // ?????????????????????Mq????????????
        HashSet<String> trackNoToMqSet = new HashSet<>();
        if (logisticsPackageDOList != null && logisticsPackageDOList.size() > 0) {
            ArrayList<String> strings = new ArrayList<>();
            for (LogisticsPackageDO t : logisticsPackageDOList) {
                strings.add(t.getLogisticsTrackingCode());
            }
            CjBaseResultDTO<List<CjTrackNumberFindDTO>> trackNumber = null;
            if (logisticsPackageDOList.get(0).getType() == 0) {
                trackNumber = TestController.cjTest;
                logisticsPackageDOList.get(0).setType(4);
            } else {
                try {
                    trackNumber = cjHttpUtils.findTrackNumber(strings);
                }catch (Exception e){
                    log.error("??????CJ??????????????????" + e.getMessage());
                }
            }
            if (trackNumber != null && HttpStatus.OK.value() == trackNumber.getCode()) {
                List<CjTrackNumberFindDTO> data = trackNumber.getData();
                if (data != null && data.size() > 0) {
                    //??????????????????
                    for (CjTrackNumberFindDTO cjTrackNumberFindDTO : data) {
                        for (LogisticsPackageDO logisticsPackageDO : logisticsPackageDOList) {
                            if (cjTrackNumberFindDTO != null && logisticsPackageDO != null) {
                                String trackingNumber = cjTrackNumberFindDTO.getTrackingNumber();
                                //?????? ?????????????????????????????????
                                if (trackingNumber != null && trackingNumber.equals(logisticsPackageDO.getLogisticsTrackingCode())) {
                                    List<CjTrackNumberFindDTO.CJLogisticRouteResponse> routes = cjTrackNumberFindDTO.getRoutes();
                                    if (routes != null) {
                                        // ????????????????????????
                                        CjTrackNumberFindDTO.CJLogisticRouteResponse cjLogisticRouteResponse = routes.stream().sorted(Comparator.comparing(CjTrackNumberFindDTO.CJLogisticRouteResponse::getAcceptTime).reversed()).findFirst().orElseGet(() -> new CjTrackNumberFindDTO.CJLogisticRouteResponse());
                                        boolean isUpdate = checkRemoteLogisticsTrackIsUpdateByLastEventTime(logisticsPackageDO, cjLogisticRouteResponse.getAcceptTime());
                                        if (!isUpdate) {
                                            continue;
                                        }
                                        trackNoToMqSet.add(trackingNumber);
                                        ArrayList<LogisticsPackageTrackDO> logisticsPackageTrackDOS = new ArrayList<>();
                                        Long id = logisticsPackageDO.getId();
                                        //??????????????????
                                        String maxTrackTime = "0";
                                        for (CjTrackNumberFindDTO.CJLogisticRouteResponse route : routes) {
                                            if (route != null) {
                                                String remark = route.getRemark();
                                                if (StringUtils.isNotEmpty(remark)) {
                                         /*       remark = DesensibilisationUtil.toLowerCase(remark);
                                                remark = DesensibilisationUtil.cleanAddress(remark);
                                                remark = DesensibilisationUtil.cjCityReplace(remark);*/
                                                    //CJ????????????code???
                                                    Integer cjLogisticsTrackStatus = DesensibilisationUtil.getCJLogisticsTrackStatus(remark);
                                                    LogisticsPackageTrackDO logisticsPackageTrackDO = new LogisticsPackageTrackDO();
                                                    logisticsPackageTrackDO.setPackageId(id);
                                                    logisticsPackageTrackDO.setId(SnowflakeIdUtils.getId());
                                                    //??????????????????
                                                    logisticsPackageTrackDO.setStatus(LogisticTrackCJEnum.getLogisticTrackCJEnum(cjLogisticsTrackStatus).getName());
                                                    //??????????????????
                                                    logisticsPackageTrackDO.setContent(remark);
                                                    logisticsPackageTrackDO.setLocation(route.getAcceptAddress());
                                                    String acceptTime = route.getAcceptTime();
                                                    if (StringUtils.isNotEmpty(acceptTime)) {
                                                        logisticsPackageTrackDO.setEventTime(acceptTime);
                                                        //????????????????????????
                                                        int compareTo = maxTrackTime.compareTo(acceptTime);
                                                        if (compareTo < 0) {
                                                            maxTrackTime = acceptTime;
                                                            if (cjLogisticsTrackStatus == LogisticTrackCJEnum.SIGN.getCode()) {
                                                                logisticsPackageDO.setReceivingGoodTime(DateUtil.parse(acceptTime));
                                                            }
                                                            logisticsPackageDO.setLogisticsNode(LogisticTrackCJEnum.getLogisticTrackCJEnum(cjLogisticsTrackStatus).getName());
                                                            // logisticsPackageDO.set
                                                       /* if (cjLogisticsTrackStatus != null) {
                                                            //???????????????
                                                            logisticsPackageDO.setLogisticsStatus(LogisticTrackCJEnum.getLogisticTrackCJEnum(cjLogisticsTrackStatus).getLogisticStatus());
                                                        }*/

                                                        }
                                                    }
                                                    logisticsPackageTrackDOS.add(logisticsPackageTrackDO);
                                                }
                                            }
                                        }
                                        logisticsPackageDO.setCjLogisticsStatus(cjTrackNumberFindDTO.getTrackingStatus());
                                        if (logisticsPackageTrackDOS != null && logisticsPackageTrackDOS.size() > 0) {
                                            updateLogistics(logisticsPackageTrackDOS, id, logisticsPackageDO);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                //???????????? ??????
                String s = null;
                if (trackNumber != null) {
                    s = JSON.toJSONString(trackNumber);
                }
                log.error("CJ???????????????????????????{}" + s + "??????????????????????????????????????????" + JSONArray.toJSONString(strings));
                throw new GlobalException(ExceptionEnum.RETURN_CODE_100603.getCode(), ExceptionEnum.RETURN_CODE_100603.getMsg() + s);
            }
        }
        if (!trackNoToMqSet.isEmpty()) {
            trackNoToMqSet.stream().forEach(item -> {
                LogisticsChangeMsgDto logisticsChangeMsgDto = new LogisticsChangeMsgDto();
                logisticsChangeMsgDto.setLogisticsWayBillNo(item);
                logisticsChangeMsgDto.setLogisticsTrackingCode(item);
                logisticsChangeMsgDto.setType(DeliveryPackageTypeEnum.CJ_DELIVERY_PACKAGE.getType());
                logisticsChangeMsgDto.setNoticeTime(new Date());
                sendLogisticsChangeMesToMqToNoticePlatform(logisticsChangeMsgDto);
            });
        }
    }


    private void doRefreshAeDeliveryPackage(List<LogisticsPackageDO> logisticsPackageDOList) {
        List<LogisticsTrackNodeDO> logisticsTrackNodeDOList = this.doGetLogisticsTrackNodeList(DeliveryPackageTypeEnum.AE_DELIVERY_PACKAGE.getType());
        for (LogisticsPackageDO logisticsPackageDO : logisticsPackageDOList) {
            try {
                List<String> notRefreshPackage = new ArrayList();
                //??????????????????
                String logisticsChannelStatus = logisticsPackageDO.getLogisticsChannelStatus();
                OpenPlatformDTO<AliexpressTradeDsOrderGetResponse.AeopOrderInfo> openPlatformDTO = aeOpenUtils.orderGet(Long.valueOf(logisticsPackageDO.getChannelOrderId()));
                if (openPlatformDTO.getSuccess() && openPlatformDTO.getResult() != null) {
                    AliexpressTradeDsOrderGetResponse.AeopOrderInfo aeopOrderInfo = openPlatformDTO.getResult();
                    logisticsChannelStatus = aeopOrderInfo.getLogisticsStatus();
                    // AE ???????????????????????????
                    List<String> newTrackList = aeopOrderInfo.getLogisticsInfoList().stream().map(AliexpressTradeDsOrderGetResponse.AeopOrderLogisticsInfo::getLogisticsNo).collect(Collectors.toList());
                    if (!newTrackList.contains(logisticsPackageDO.getLogisticsTrackingCode()) && Constant.NO_0.equals(logisticsPackageDO.getHandleTrackingCode())) {
                        // ?????????
                        if (!notRefreshPackage.contains(logisticsPackageDO.getChannelOrderId())) {
                            TransactionCallback transactionCallback = (TransactionStatus status) -> {
                                LambdaQueryWrapper<LogisticsPackageDO> queryPackage = Wrappers.<LogisticsPackageDO>lambdaQuery()
                                        .eq(LogisticsPackageDO::getHandleTrackingCode,Constant.NO_0)
                                        .eq(LogisticsPackageDO::getChannelOrderId, logisticsPackageDO.getChannelOrderId());
                                List<LogisticsPackageDO> logisticsPackageDOS = logisticsPackageMapper.selectList(queryPackage);
                                // ??????
                                if (!Objects.isNull(logisticsPackageDOS) && !logisticsPackageDOS.isEmpty()) {
                                    for (int i = 0; i < logisticsPackageDOS.size() && i < newTrackList.size(); i++) {
                                        logisticsPackageDOS.get(i).setLogisticsTrackingCode(newTrackList.get(i));
                                        logisticsPackageDOS.get(i).setLogisticsWayBillNo(newTrackList.get(i));
                                        logisticsPackageMapper.updateById(logisticsPackageDOS.get(i));
                                    }
                                }
                                return true;
                            };
                            transactionTemplate.execute(transactionCallback);
                            notRefreshPackage.add(logisticsPackageDO.getChannelOrderId());
                        }
                        continue;// ????????????????????????????????????
                    }
                }

                DeliveryPackageDto.DeliveryPackageItemDto deliveryPackageItemDto = new DeliveryPackageDto.DeliveryPackageItemDto();
                deliveryPackageItemDto.setSalesOrderId(logisticsPackageDO.getSalesOrderId());
                deliveryPackageItemDto.setChannelOrderId(logisticsPackageDO.getChannelOrderId());
                deliveryPackageItemDto.setLogisticsCode(logisticsPackageDO.getLogisticsCode());
                deliveryPackageItemDto.setLogisticsTrackingCode(logisticsPackageDO.getLogisticsTrackingCode());
                deliveryPackageItemDto.setLogisticsChannelStatus(logisticsChannelStatus);
                deliveryPackageItemDto.setLogisticsArea(logisticsPackageDO.getLogisticsArea());

                DeliveryPackageDto deliveryPackageDto = new DeliveryPackageDto();
                deliveryPackageDto.setList(Collections.singletonList(deliveryPackageItemDto));
                deliveryPackageDto.setLogisticsTrackNodeDOList(logisticsTrackNodeDOList);
                this.updateAeDeliveryPackage(deliveryPackageDto);
            } catch (Exception e) {
                log.error("??????AE???????????????????????????????????????{}??????????????????{}", logisticsPackageDO, e);
            }
        }
    }

    private OpenPlatformDTO<AeTrackinginfoDTO> doGetAeLogisticsTrackinginfo(String logisticsNo, String outRef, String serviceName, String area) {
        AliexpressLogisticsDsTrackinginfoQueryRequest aliexpressLogisticsDsTrackinginfoQueryRequest = new AliexpressLogisticsDsTrackinginfoQueryRequest();
        aliexpressLogisticsDsTrackinginfoQueryRequest.setLogisticsNo(logisticsNo);
        aliexpressLogisticsDsTrackinginfoQueryRequest.setOrigin("ESCROW");
        aliexpressLogisticsDsTrackinginfoQueryRequest.setOutRef(outRef);
        aliexpressLogisticsDsTrackinginfoQueryRequest.setServiceName(serviceName);
        aliexpressLogisticsDsTrackinginfoQueryRequest.setToArea(area);
        return aeOpenUtils.logisticsTrackinginfoQuery(aliexpressLogisticsDsTrackinginfoQueryRequest);
    }


    private void logisticsTrackinginfoQuery(DeliveryPackageDto deliveryPackageDto) {
        List<LogisticsTrackNodeDO> logisticsTrackNodeDOList = Optional.ofNullable(deliveryPackageDto.getLogisticsTrackNodeDOList()).orElse(this.doGetLogisticsTrackNodeList(DeliveryPackageTypeEnum.AE_DELIVERY_PACKAGE.getType()));
        List<DeliveryPackageDto.DeliveryPackageItemDto> deliveryPackageItemDtoList = deliveryPackageDto.getList();
        deliveryPackageItemDtoList.forEach(deliveryPackageItemDto -> {
            if (!deliveryPackageItemDto.getTrackFlag()) return;

            //?????? AE ?????????????????????
            List<DeliveryPackageDto.DeliveryPackageTrackDto> deliveryPackageTrackDtoList = new ArrayList<>();
            OpenPlatformDTO<AeTrackinginfoDTO> openPlatformDTO = doGetAeLogisticsTrackinginfo(deliveryPackageItemDto.getLogisticsTrackingCode(), deliveryPackageItemDto.getChannelOrderId(), deliveryPackageItemDto.getLogisticsCode(), deliveryPackageItemDto.getLogisticsArea());
            if (openPlatformDTO.getSuccess()) {
                AeTrackinginfoDTO aeTrackinginfoDTO = openPlatformDTO.getResult();
                List<AliexpressLogisticsDsTrackinginfoQueryResponse.Details> detailsList = Optional.ofNullable(aeTrackinginfoDTO.getDetails()).orElse(new ArrayList<>());
                if (detailsList.size() == 0) {
                    deliveryPackageItemDto.setTrackFlag(Boolean.FALSE);
                    return;
                }

                detailsList.forEach(details -> {
                    DeliveryPackageDto.DeliveryPackageTrackDto deliveryPackageTrackDto = new DeliveryPackageDto.DeliveryPackageTrackDto();
                    deliveryPackageTrackDto.setEventDate(details.getEventDate());
                    deliveryPackageTrackDto.setStatus(details.getEventDesc());
                    deliveryPackageTrackDto.setContent(details.getEventDesc());
                    deliveryPackageTrackDto.setLocation(details.getAddress());
                    deliveryPackageTrackDtoList.add(deliveryPackageTrackDto);
                });

                String lastNode = "";
                Date maybeReceiveGoodTime = null;
                Optional<DeliveryPackageDto.DeliveryPackageTrackDto> optional = deliveryPackageTrackDtoList.stream().sorted(Comparator.comparing(DeliveryPackageDto.DeliveryPackageTrackDto::getEventDate).reversed()).findFirst();
                if (optional.isPresent()) {
                    DeliveryPackageDto.DeliveryPackageTrackDto deliveryPackageTrackDto = optional.get();
                    String status = deliveryPackageTrackDto.getStatus();        //??????????????????
                    maybeReceiveGoodTime = deliveryPackageTrackDto.getEventDate();        //????????????????????????
                    for (LogisticsTrackNodeDO logisticsTrackNodeDO : logisticsTrackNodeDOList) {
                        String node = logisticsTrackNodeDO.getNode();
                        if (StringUtils.indexOfIgnoreCase(status, node) != -1) {
                            lastNode = logisticsTrackNodeDO.getInsideEn();
                            break;
                        }
                    }
                }

                // AE ????????????
                if (StringUtils.indexOfIgnoreCase(lastNode, "Delivered") != -1 || StringUtils.indexOfIgnoreCase(lastNode, "elivered") != -1) {
                    deliveryPackageItemDto.setLogisticsStatus(DeliveryPackageLogisticsStatusEnum.RECEIVED.getCode());
                    deliveryPackageItemDto.setReceivingGoodTime(Objects.isNull(maybeReceiveGoodTime) ? (new Date()) : maybeReceiveGoodTime);
                }
                // AE ????????????
                Optional<DeliveryPackageDto.DeliveryPackageTrackDto> sendTime = deliveryPackageTrackDtoList.stream().sorted(Comparator.comparing(DeliveryPackageDto.DeliveryPackageTrackDto::getEventDate)).findFirst();
                if (sendTime.isPresent()) {
                    deliveryPackageItemDto.setDeliveryTime(sendTime.get().getEventDate());
                }
                deliveryPackageItemDto.setLogisticsNode(lastNode);
                deliveryPackageItemDto.setTrackList(deliveryPackageTrackDtoList);       //????????????????????????
            } else {
                deliveryPackageItemDto.setTrackFlag(Boolean.FALSE);
            }
        });
    }

    private List<LogisticsTrackNodeDO> doGetLogisticsTrackNodeList(Integer type) {
        return logisticsTrackNodeService.getLogisticsTrackNodeList(type);
    }

    private String queryCancelStatus(IDepositoryManager depositoryManager, String orderNo) {
        CancelDepositoryOrderResponse depositoryOrderResponse = depositoryManager.cancelOutDepositoryOrder(orderNo);
        if (depositoryOrderResponse.getSuccessSign()) {
            return depositoryOrderResponse.getStatus();
        }
        return "fail";
    }

    private void cancalSendMsgToMq(Map<Long, List<LogisticsPackageItemDO>> logisticsPackageItemDOMap, LogisticsPackageDO logisticsPackageDO) {
        //????????????????????????
        List<DepositoryPurchaseStatusMsgDto> depositoryPurchaseStatusMsgDtoList = new ArrayList<>();
        List<LogisticsPackageItemDO> tempLogisticsPackageItemDOList = Optional.ofNullable(logisticsPackageItemDOMap.get(logisticsPackageDO.getId())).orElse(new ArrayList<>());
        tempLogisticsPackageItemDOList.forEach(logisticsPackageItemDO -> {
            DepositoryPurchaseStatusMsgDto depositoryPurchaseStatusMsgDto = new DepositoryPurchaseStatusMsgDto();
            depositoryPurchaseStatusMsgDto.setSalesOrderId(logisticsPackageDO.getSalesOrderId());
            depositoryPurchaseStatusMsgDto.setPurchaseId(logisticsPackageItemDO.getPurchaseId());
            depositoryPurchaseStatusMsgDto.setItemId(logisticsPackageItemDO.getItemId());
            depositoryPurchaseStatusMsgDtoList.add(depositoryPurchaseStatusMsgDto);
        });

        //???????????????????????????
        if (depositoryPurchaseStatusMsgDtoList.size() != 0) {
            sendMsgUtils.sendPurchaseIntoDepositoryMsg(depositoryPurchaseStatusMsgDtoList, DepositoryPurchaseStatusEnum.CANCELSEND.getStatusCode());        //????????????
        }
    }


    private boolean checkRemoteLogisticsTrackIsUpdateByLastEventTime(LogisticsPackageDO logisticsPackageDO, String queryTime) {
        if (StringUtils.isBlank(queryTime)) {
            return false;
        }
        LambdaQueryWrapper<LogisticsPackageTrackDO> queryWrapper = Wrappers.<LogisticsPackageTrackDO>lambdaQuery()
                .select(LogisticsPackageTrackDO::getId, LogisticsPackageTrackDO::getEventTime)
                .eq(LogisticsPackageTrackDO::getPackageId, logisticsPackageDO.getId())
                .orderByDesc(LogisticsPackageTrackDO::getEventTime);
        Page<LogisticsPackageTrackDO> trackDOPage = new Page<>();
        Page<LogisticsPackageTrackDO> logisticsPackageTrackDOPage = logisticsPackageTrackMapper.selectPage(trackDOPage, queryWrapper);
        List<LogisticsPackageTrackDO> records = logisticsPackageTrackDOPage.getRecords();
        if (!records.isEmpty() && StringUtils.isNotBlank(records.get(0).getEventTime())) {
            if (queryTime.compareTo(records.get(0).getEventTime()) < 1) {
                return false;
            }
        }
        return true;
    }

    private boolean sendLogisticsChangeMesToMqToNoticePlatform(LogisticsChangeMsgDto logisticsChangeMsgDto) {
        try {
            String message = JSON.toJSONString(logisticsChangeMsgDto);
            SendResult sendResult = rocketmqUtils.syncSend(Constant.LOGISTIC_TRACE_CHANGE_MQ, message);
            log.info("???????????????????????????MQ??????????????????{}???,??????????????????{}????????????mq???????????????,{}", sendResult.getSendStatus(), sendResult, message);
            if (SEND_OK.equals(sendResult.getSendStatus())) {
                return true;
            }
        } catch (Exception e) {
            log.error("never occur,{}", e);
        }
        return false;
    }

    private boolean sendReceivedStatusToMq(LogisticsPackageDO logisticsPackageDO) {
        try {
            LambdaQueryWrapper<LogisticsPackageItemDO> queryWrapper = Wrappers.<LogisticsPackageItemDO>lambdaQuery().eq(LogisticsPackageItemDO::getPackageId, logisticsPackageDO.getId());
            List<LogisticsPackageItemDO> logisticsPackageItemDOList = logisticsPackageItemMapper.selectList(queryWrapper);
            if (!logisticsPackageItemDOList.isEmpty()) {
                List<DepositoryPurchaseStatusMsgDto> depositoryPurchaseStatusMsgDtoList = logisticsPackageItemDOList.stream().map(item -> {
                    DepositoryPurchaseStatusMsgDto depositoryPurchaseStatusMsgDto = new DepositoryPurchaseStatusMsgDto();
                    depositoryPurchaseStatusMsgDto.setSalesOrderId(logisticsPackageDO.getSalesOrderId());
                    depositoryPurchaseStatusMsgDto.setPurchaseId(item.getPurchaseId());
                    depositoryPurchaseStatusMsgDto.setItemId(item.getItemId());
                    return depositoryPurchaseStatusMsgDto;
                }).collect(Collectors.toList());
                sendMsgUtils.sendPurchaseIntoDepositoryMsg(depositoryPurchaseStatusMsgDtoList, DepositoryPurchaseStatusEnum.RECEIVED.getStatusCode());
            }
        } catch (Exception e) {
            log.error("never occur,{}", e);
        }
        return true;
    }

    private boolean outDepositoryLimitTime(String logisticsWayBillNo) {
        log.info("??????????????????????????????????????????,???????????????{}", logisticsWayBillNo);
        return redissLockUtil.tryLock(logisticsWayBillNo, TimeUnit.SECONDS, -1, 180);
    }

    private void removeOutDepositoryLimit(String logisticsWayBillNo) {
        log.info("??????????????????????????????????????????-????????????,???????????????{}", logisticsWayBillNo);
        redissLockUtil.unlock(logisticsWayBillNo);
    }

    private AddressDto getAddressDtoFromRemoteOrderService(String salesOrderId) {
        ArrayList<String> strings = new ArrayList<>();
        strings.add(salesOrderId);
        OrderUserInfoGTO.OrderUserInfoRequest build = OrderUserInfoGTO.OrderUserInfoRequest.newBuilder().addAllOrderSaleId(strings).build();
        OrderUserInfoGTO.OrderUserInfoResult orderUserInfo = null;
        try {
            orderUserInfo = orderServiceClient.getOrderUserInfo(build);
        } catch (Exception e) {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "?????????????????????" + e.getMessage());
        }
        AddressDto addressDto = null;
        if (orderUserInfo != null && orderUserInfo.getDataList() != null && orderUserInfo.getDataList().size() > 0) {
            OrderUserInfoGTO.OrderUserInfo orderUserInfo1 = orderUserInfo.getDataList().get(0);
            addressDto = new AddressDto();
            addressDto.setFirstName(orderUserInfo1.getFirstName());
            addressDto.setLastName(orderUserInfo1.getLastName());
            addressDto.setCountryCode(orderUserInfo1.getRegion());
            addressDto.setProvince(orderUserInfo1.getProvince());
            addressDto.setCity(orderUserInfo1.getCity());
            addressDto.setStreet1(orderUserInfo1.getDoor());
            addressDto.setStreet2(orderUserInfo1.getDoor2());
            addressDto.setPostCode(orderUserInfo1.getPostcode());
            addressDto.setTel(orderUserInfo1.getTel());
            addressDto.setEmail(orderUserInfo1.getEmail());
            addressDto.setTaxNumber(orderUserInfo1.getDutyNo());
        } else {

            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "????????????????????? ??????????????????????????????");
        }
        return addressDto;
    }
}

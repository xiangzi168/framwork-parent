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
        //修改为30已制单状态搜寻
        LambdaQueryWrapper<LogisticsPackageDO> logisticsPackageDOLambdaQueryWrapper = Wrappers.<LogisticsPackageDO>lambdaQuery()
                .ge(LogisticsPackageDO::getCreateTime, calendar.getTime())     //90 天之内的数据
                .between(LogisticsPackageDO::getLogisticsStatus, DeliveryPackageLogisticsStatusEnum.CREATED.getCode(), DeliveryPackageLogisticsStatusEnum.DELIVERED.getCode())
                .eq(LogisticsPackageDO::getIsValid, Constant.YES);      //有效数据
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
        // 物流商未揽收 没有该节点 通过表的logistics_received字段来判断是否揽收
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
        //查询包裹发货单数据
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
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "操作太快了，稍等两分钟试试！");
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
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "至少传一个运单号，现在是null");
        }
        try {
            //物流单数据
            LambdaQueryWrapper<LogisticsPackageDO> logisticsPackageDOLambdaQueryWrapper = Wrappers.<LogisticsPackageDO>lambdaQuery()
                    .in(LogisticsPackageDO::getLogisticsWayBillNo, wayBillNoList);
            List<LogisticsPackageDO> logisticsPackageDOList = logisticsPackageMapper.selectList(logisticsPackageDOLambdaQueryWrapper);
            List<Long> idList = logisticsPackageDOList.stream().map(LogisticsPackageDO::getId).collect(Collectors.toList());
            //商品数据
            LambdaQueryWrapper<LogisticsProductDO> logisticsProductDOLambdaQueryWrapper = Wrappers.<LogisticsProductDO>lambdaQuery()
                    .in(LogisticsProductDO::getRelationId, idList)
                    .eq(LogisticsProductDO::getType, PackageProductTypeEnum.DELIVERY_PACKAGE.getType());
            List<LogisticsProductDO> logisticsProductDOList = logisticsProductMapper.selectList(logisticsProductDOLambdaQueryWrapper);
            Map<Long, List<LogisticsProductDO>> logisticsProductDOMap = logisticsProductDOList.stream().collect(Collectors.groupingBy(LogisticsProductDO::getRelationId));
            //物流单 itemId 数据
            LambdaQueryWrapper<LogisticsPackageItemDO> logisticsPackageItemDOLambdaQueryWrapper = Wrappers.<LogisticsPackageItemDO>lambdaQuery()
                    .in(LogisticsPackageItemDO::getPackageId, idList);
            List<LogisticsPackageItemDO> logisticsPackageItemDOList = logisticsPackageItemMapper.selectList(logisticsPackageItemDOLambdaQueryWrapper);
            Map<Long, List<LogisticsPackageItemDO>> logisticsPackageItemDOMap = logisticsPackageItemDOList.stream().collect(Collectors.groupingBy(LogisticsPackageItemDO::getPackageId));

            //地址数据
            LambdaQueryWrapper<LogisticsPackageAddressDO> logisticsPackageAddressDOLambdaQueryWrapper = Wrappers.<LogisticsPackageAddressDO>lambdaQuery()
                    .in(LogisticsPackageAddressDO::getPackageId, idList);
            List<LogisticsPackageAddressDO> logisticsPackageAddressDOList = logisticsPackageAddressMapper.selectList(logisticsPackageAddressDOLambdaQueryWrapper);
            Map<Long, LogisticsPackageAddressDO> logisticsPackageAddressDOMap = logisticsPackageAddressDOList.stream().collect(Collectors.toMap(LogisticsPackageAddressDO::getPackageId, logisticsPackageAddressDO -> logisticsPackageAddressDO));

            //生成物流仓库驱动类
            IDepositoryManager depositoryManager = depositoryFactory.createDepositoryManager(DepositoryTypeEnum.WANB.getCode());

            AtomicReference<Boolean> atomicReferenceFlag = new AtomicReference<>(false);
            logisticsPackageDOList.forEach(logisticsPackageDO -> {
                if (logisticsPackageDO.getType().equals(DeliveryPackageTypeEnum.AE_DELIVERY_PACKAGE.getType()))      //只有境外发货单才需要推送仓库
                {
                    return;
                }
                // 增加操作人
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
                // fix:仓库是跟踪号是运单号，避免修改太多，先修改这里
                outDepositoryOrderDto.getExpress().setTrackingNumber(logisticsPackageDO.getLogisticsWayBillNo());
    //            outDepositoryOrderDto.getExpress().setLabelUrl(logisticsPackageDO.getLogisticsLabelUrl());
    //            outDepositoryOrderDto.getExpress().setServiceId(WBDepositoryRegisterServiceConfig.getServicerIdFromCode(logisticsPackageDO.getLogisticsCode(), logisticsPackageDO.getLogisticsChannel()));
                OutDepositoryResultDto outDepositoryResultDto = depositoryManager.addOutDepositoryOrder(outDepositoryOrderDto);
                if (outDepositoryResultDto.isSuccessSign()) {
                    //修改发货包裹单的状态为   发货中
    //                logisticsPackageDO.setLogisticsStatus(DeliveryPackageLogisticsStatusEnum.INDELIVERY.getCode());     //发货中
                    logisticsPackageDO.setLogisticsStatus(DeliveryPackageLogisticsStatusEnum.CREATED.getCode());     //发货中
                    logisticsPackageDO.setDeliveryStatus(DeliveryPackageDeliveryStatusEnum.SENDING.getCode());
                    logisticsPackageDO.setLogisticsTrackingCode(outDepositoryOrderDto.getExpress().getTrackingNumber());
                    logisticsPackageDO.setLogisticsLabelUrl(outDepositoryOrderDto.getExpress().getLabelUrl());
                    logisticsPackageMapper.updateById(logisticsPackageDO);

                    //发货中
                    sendMsgUtils.sendPurchaseIntoDepositoryMsg(depositoryPurchaseStatusMsgDtoList, DepositoryPurchaseStatusEnum.SENDING.getStatusCode());       //发货中

                    atomicReferenceFlag.set(true);
                } else      //推单出库失败
                {
                    logisticsPackageDO.setErrorInfo(outDepositoryResultDto.getErrorMsg());       //异常信息
                    logisticsPackageDO.setDeliveryStatus(DeliveryPackageDeliveryStatusEnum.FAILPUSH.getCode());
                    logisticsPackageMapper.updateById(logisticsPackageDO);      // 修改当前物流单

                    sendMsgUtils.sendPurchaseIntoDepositoryMsg(depositoryPurchaseStatusMsgDtoList, DepositoryPurchaseStatusEnum.FAILPUSH.getStatusCode());       //推送失败
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
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "参数异常");
        }
        return deliveryPackageCannelVOList.get(0);
    }

    @Override
    public List<DeliveryPackageCannelVO> cancelBatch(DeliveryPackageOperationReq deliveryPackageOperationReq) {
        List<String> wayBillNoList = deliveryPackageOperationReq.getLogisticsWayBillNos();
        List<Long> idLists = deliveryPackageOperationReq.getIdList();

        if ((Objects.isNull(wayBillNoList) || wayBillNoList.isEmpty()) && (Objects.isNull(idLists) || idLists.isEmpty())) {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "取消的单号不能是null");
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

        //返回数据
        List<DeliveryPackageCannelVO> deliveryPackageCannelVOList = new ArrayList<>();

        //生成物流仓库驱动类
        IDepositoryManager depositoryManager = depositoryFactory.createDepositoryManager(DepositoryTypeEnum.WANB.getCode());

        ArrayList<LogisticsPackageDO> requiredAgainQueryDepositoryList = new ArrayList<>();
        //取消发货
        logisticsPackageDOList.forEach(logisticsPackageDO -> {
            //只有境外发货单才才能取消出库
            if (logisticsPackageDO.getType().equals(DeliveryPackageTypeEnum.AE_DELIVERY_PACKAGE.getType())) {
                return;
            }
            logisticsPackageDO.setUpdateBy(UserContext.getCurrentUserName());
            DeliveryPackageCannelVO deliveryPackageCannelVO = new DeliveryPackageCannelVO();
            deliveryPackageCannelVO.setId(logisticsPackageDO.getId());
            deliveryPackageCannelVOList.add(deliveryPackageCannelVO);

            //只有发货中的数据才可能设置取取消发货
//            if (logisticsPackageDO.getLogisticsStatus().equals(DeliveryPackageLogisticsStatusEnum.INDELIVERY.getCode())) {//发货中
            if (logisticsPackageDO.getLogisticsStatus().equals(DeliveryPackageLogisticsStatusEnum.CREATED.getCode())) {
                CancelDepositoryOrderResponse cancelDepositoryOrderResponse = depositoryManager.cancelOutDepositoryOrder(logisticsPackageDO.getLogisticsOrderNo());
                if (cancelDepositoryOrderResponse.getSuccessSign()) {
                    //设置返回状态
                    deliveryPackageCannelVO.setStatus(cancelDepositoryOrderResponse.getStatus());

                    //修改数据的对象
                    LogisticsPackageDO tempLogisticsPackageDO = new LogisticsPackageDO();
                    tempLogisticsPackageDO.setUpdateBy(UserContext.getCurrentUserName());
                    tempLogisticsPackageDO.setId(logisticsPackageDO.getId());
                    tempLogisticsPackageDO.setCancelRemark(StringUtils.isNotBlank(operationRemark) ? operationRemark : "请求取消发货");        //设置取消原因

                    //取消发货成功
                    if (LogisticItemEnum.LogisticItemEnumWanB.DepositoryChannelOrderStatusEnum.ACCEPTED.getStatus().equals(cancelDepositoryOrderResponse.getStatus())) {
                        Integer logisticsStatus = DeliveryPackageLogisticsStatusEnum.CANCEL.getCode();       //已取消
                        tempLogisticsPackageDO.setLogisticsStatus(logisticsStatus);     //设置为取消
                        tempLogisticsPackageDO.setDeliveryStatus(DeliveryPackageDeliveryStatusEnum.CANCELSEND.getCode());
//                        tempLogisticsPackageDO.setIsValid(Constant.NO_0);       //设置为无效
                        tempLogisticsPackageDO.setCancelRemark("仓库【已接收】：" + tempLogisticsPackageDO.getCancelRemark());        //设置取消原因
                        cancalSendMsgToMq(logisticsPackageItemDOMap, logisticsPackageDO);
                    } else if (LogisticItemEnum.LogisticItemEnumWanB.DepositoryChannelOrderStatusEnum.REQUESTED.getStatus().equals(cancelDepositoryOrderResponse.getStatus())) {
                        requiredAgainQueryDepositoryList.add(logisticsPackageDO);
                    } else if (LogisticItemEnum.LogisticItemEnumWanB.DepositoryChannelOrderStatusEnum.REJECTED.getStatus().equals(cancelDepositoryOrderResponse.getStatus())) {
                        tempLogisticsPackageDO.setCancelRemark("仓库【已拒绝】：" + tempLogisticsPackageDO.getCancelRemark());
                        deliveryPackageCannelVO.setErrorMsg(cancelDepositoryOrderResponse.getErrorMsg());
                    }
                    //修改物流发货包裹单数据
                    logisticsPackageMapper.updateById(tempLogisticsPackageDO);
                } else {
                    deliveryPackageCannelVO.setErrorMsg(cancelDepositoryOrderResponse.getErrorMsg());
                }
            } else {
                deliveryPackageCannelVO.setErrorMsg("只有发货中状态才能取消发货");
            }
        });
        if (!requiredAgainQueryDepositoryList.isEmpty()) {
            Runnable runnable = () -> {
                for (LogisticsPackageDO logisticsPackageDO : requiredAgainQueryDepositoryList) {
                    for (int i = 0; i < 3; i++) {
                        String status = queryCancelStatus(depositoryManager, logisticsPackageDO.getLogisticsOrderNo());
                        if (LogisticItemEnum.LogisticItemEnumWanB.DepositoryChannelOrderStatusEnum.ACCEPTED.getStatus().equals(status)) {
                            Integer logisticsStatus = DeliveryPackageLogisticsStatusEnum.CANCEL.getCode();       //已取消
                            LogisticsPackageDO tempLogisticsPackageDO = new LogisticsPackageDO();
                            tempLogisticsPackageDO.setId(logisticsPackageDO.getId());
                            tempLogisticsPackageDO.setLogisticsStatus(logisticsStatus);     //设置为取消
                            tempLogisticsPackageDO.setDeliveryStatus(DeliveryPackageDeliveryStatusEnum.CANCELSEND.getCode());
//                            tempLogisticsPackageDO.setIsValid(Constant.NO_0);       //设置为无效
                            tempLogisticsPackageDO.setCancelRemark("仓库【已接收】：" + tempLogisticsPackageDO.getCancelRemark());        //设置取消原因
                            //修改物流发货包裹单数据
                            logisticsPackageMapper.updateById(tempLogisticsPackageDO);
                            //组装发送消息数据
                            cancalSendMsgToMq(logisticsPackageItemDOMap, logisticsPackageDO);
                            break;
                        } else if (LogisticItemEnum.LogisticItemEnumWanB.DepositoryChannelOrderStatusEnum.REJECTED.getStatus().equals(status)) {
                            LogisticsPackageDO tempLogisticsPackageDO = new LogisticsPackageDO();
                            tempLogisticsPackageDO.setId(logisticsPackageDO.getId());
                            tempLogisticsPackageDO.setCancelRemark("仓库【已拒绝】：" + tempLogisticsPackageDO.getCancelRemark());        //设置取消原因
                            //修改物流发货包裹单数据
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
        // 将取消失败的抛出异常
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
        //包裹单数据
        LambdaQueryWrapper<LogisticsPackageDO> logisticsPackageDOLambdaQueryWrapper = Wrappers.<LogisticsPackageDO>lambdaQuery()
                .in(LogisticsPackageDO::getLogisticsWayBillNo, billNos)
                .eq(LogisticsPackageDO::getSalesOrderId, salesOrderId);
        List<LogisticsPackageDO> logisticsPackageDOList = logisticsPackageMapper.selectList(logisticsPackageDOLambdaQueryWrapper);
        List<Long> packageIdList = logisticsPackageDOList.stream().map(LogisticsPackageDO::getId).collect(Collectors.toList());

        //包裹单 item 数据
        LambdaQueryWrapper<LogisticsPackageItemDO> logisticsPackageItemDOLambdaQueryWrapper = Wrappers.<LogisticsPackageItemDO>lambdaQuery()
//                .in(LogisticsPackageItemDO::getItemId, itemIdList)
                .in(LogisticsPackageItemDO::getPackageId, packageIdList);
        List<LogisticsPackageItemDO> logisticsPackageItemDOList_all = logisticsPackageItemMapper.selectList(logisticsPackageItemDOLambdaQueryWrapper);
        // 找到需要修改的itemId
        List<LogisticsPackageItemDO> logisticsPackageItemDOList = logisticsPackageItemDOList_all.stream().filter(item -> itemIdList.contains(item.getItemId())).collect(Collectors.toList());
        Map<String, List<LogisticsPackageItemDO>> logisticsPackageItemDOMap = logisticsPackageItemDOList.stream().collect(Collectors.groupingBy(LogisticsPackageItemDO::getSku));

        //包裹单商品数据
        LambdaQueryWrapper<LogisticsProductDO> logisticsProductDOLambdaQueryWrapper = Wrappers.<LogisticsProductDO>lambdaQuery()
                .in(LogisticsProductDO::getRelationId, packageIdList)
                .eq(LogisticsProductDO::getType, PackageProductTypeEnum.DELIVERY_PACKAGE.getType());
        List<LogisticsProductDO> logisticsProductDOList = logisticsProductMapper.selectList(logisticsProductDOLambdaQueryWrapper);

      /*  //包裹单地址数据
        LambdaQueryWrapper<LogisticsPackageAddressDO> logisticsPackageAddressDOLambdaQueryWrapper = Wrappers.<LogisticsPackageAddressDO>lambdaQuery()
                .in(LogisticsPackageAddressDO::getPackageId, packageIdList);
        List<LogisticsPackageAddressDO> logisticsPackageAddressDOList = logisticsPackageAddressMapper.selectList(logisticsPackageAddressDOLambdaQueryWrapper);*/

        for (LogisticsPackageDO logisticsPackageDO : logisticsPackageDOList) {
            if (!logisticsPackageDO.getLogisticsStatus().equals(DeliveryPackageLogisticsStatusEnum.DELIVERED.getCode())) {
                throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "选择商品物流单状态只能是已发货状态，其他状态不支持该操作");
            }
        }
      /*  if (logisticsPackageAddressDOList.size() == 0) {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "销售单地址异常");
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
        // 修改成如果包裹还有商品，不能设置成无效
        Map<Long, List<LogisticsPackageItemDO>> mapByPackageId = logisticsPackageItemDOList_all.stream().collect(Collectors.groupingBy(LogisticsPackageItemDO::getPackageId));
        Map<Long, List<LogisticsPackageItemDO>> mapByPackageToUpdate = logisticsPackageItemDOList.stream().collect(Collectors.groupingBy(LogisticsPackageItemDO::getPackageId));
        mapByPackageToUpdate.entrySet().forEach(item -> {
            if (mapByPackageId.get(item.getKey()).size() == item.getValue().size()) {
                // 直接 无效
                LambdaUpdateWrapper<LogisticsPackageDO> logisticsPackageDOLambdaUpdateWrapper = Wrappers.<LogisticsPackageDO>lambdaUpdate()
                        .set(LogisticsPackageDO::getIsValid, Constant.NO_0)
                        .set(LogisticsPackageDO::getUpdateBy, UserContext.getCurrentUserName())
                        .eq(LogisticsPackageDO::getId, item.getKey());
                logisticsPackageMapper.update(null, logisticsPackageDOLambdaUpdateWrapper);
            }
            // 删除itemId
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
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "未找到需要刷新物流的包裹");
        }

        LambdaQueryWrapper<LogisticsPackageDO> logisticsPackageDOLambdaQueryWrapper = Wrappers.<LogisticsPackageDO>lambdaQuery()
                .in(LogisticsPackageDO::getId, idList);
        List<LogisticsPackageDO> logisticsPackageDOList = logisticsPackageMapper.selectList(logisticsPackageDOLambdaQueryWrapper);
        // 针对手动更新物流，删除旧轨迹和修改成未签收状态
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
        if (abroadLogisticsPackageDOList.size() != 0) this.doRefresh1688DeliveryPackage(list);        //1688 物流数据刷新
        if (aeLogisticsPackageDOList.size() != 0)
            this.doRefreshAeDeliveryPackage(aeLogisticsPackageDOList);      //AE 物流数据刷新
        if (cjlogisticsPackageDOS.size() != 0)
            this.doSegmentRefreshCjDeliveryPackage(cjlogisticsPackageDOS);      //CJ 物流数据刷新
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
                throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "参数异常");
            }
            if (logisticsPackageDO.getIsValid() == Constant.NO_0) {
                throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "所选中物流发货包裹单标记失效失败！");
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

            //物流轨迹数据
            LambdaQueryWrapper<LogisticsPackageTrackDO> logisticsPackageTrackDOLambdaQueryWrapper = Wrappers.<LogisticsPackageTrackDO>lambdaQuery()
                    .in(LogisticsPackageTrackDO::getPackageId, logisticsPackageIdList);
            List<LogisticsPackageTrackDO> logisticsPackageTrackDOList = logisticsPackageTrackMapper.selectList(logisticsPackageTrackDOLambdaQueryWrapper);
            logisticsPackageTrackDOMap.putAll(logisticsPackageTrackDOList.stream().collect(Collectors.groupingBy(LogisticsPackageTrackDO::getPackageId)));
            //查询包裹发货单商品数据
            LambdaQueryWrapper<LogisticsProductDO> logisticsProductDOLambdaQueryWrapper = Wrappers.<LogisticsProductDO>lambdaQuery()
                    .eq(LogisticsProductDO::getType, PackageProductTypeEnum.DELIVERY_PACKAGE.getType())
                    .in(LogisticsProductDO::getRelationId, logisticsPackageIdList);
            List<LogisticsProductDO> logisticsProductDOList = logisticsProductMapper.selectList(logisticsProductDOLambdaQueryWrapper);
            logisticsProductDOMap.putAll(logisticsProductDOList.stream().collect(Collectors.groupingBy(LogisticsProductDO::getRelationId)));
            //查询包裹发货单商品 item 数据
            LambdaQueryWrapper<LogisticsPackageItemDO> logisticsPackageItemDOLambdaQueryWrapper = Wrappers.<LogisticsPackageItemDO>lambdaQuery()
                    .in(LogisticsPackageItemDO::getPackageId, logisticsPackageIdList);
            List<LogisticsPackageItemDO> logisticsPackageItemDOList = logisticsPackageItemMapper.selectList(logisticsPackageItemDOLambdaQueryWrapper);
            logisticsPackageItemDOMap.putAll(logisticsPackageItemDOList.stream().collect(Collectors.groupingBy(LogisticsPackageItemDO::getPackageId)));

            //物流渠道数据
            LambdaQueryWrapper<LogisticsChannelDO> logisticsChannelDOLambdaQueryWrapper = Wrappers.<LogisticsChannelDO>lambdaQuery()
                    .in(LogisticsChannelDO::getLogisticsCode, logisticsCodeList);
            List<LogisticsChannelDO> logisticsChannelDOList = logisticsChannelMapper.selectList(logisticsChannelDOLambdaQueryWrapper);
            logisticsChannelDOMap.putAll(logisticsChannelDOList.stream().collect(Collectors.toMap(logisticsChannelDO -> (logisticsChannelDO.getLogisticsCode() + logisticsChannelDO.getChannelCode()), logisticsChannelDO -> logisticsChannelDO)));

        }
        //查询包裹发货单物流数据
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

            deliveryPackageVO.setChannelCode(logisticsPackageDO.getLogisticsChannel());     //设置物流渠道信息
            deliveryPackageVO.setChannelName(logisticsChannelDO.getChannelName());      //设置物流渠道信息
            // 排序
            Collections.sort(logisticsPackageTrackVOList, ((o1, o2) -> o2.getTrackTime().compareTo(o1.getTrackTime())));
            deliveryPackageVO.setTrackList(logisticsPackageTrackVOList);       //物流信息
            deliveryPackageVO.setProductList(logisticsProductVOList);       //商品数据
            deliveryPackageVOList.add(deliveryPackageVO);
        });
        return deliveryPackageVOList;
    }

    @Override
    public File exportDeliveryPackageExcel(ExportExcelReq<DeliveryPackageReq> exportExcelReq) {
        DeliveryPackageReq deliveryPackageReq = Optional.ofNullable(exportExcelReq.getData()).orElse(new DeliveryPackageReq());
        Integer type = deliveryPackageReq.getType();
        log.info("获取到的deliveryPackageReq.getType()类型是：{}", type);
        if (type.equals(DeliveryPackageTypeEnum.ABROAD_DELIVERY_PACKAGE.getType()))       //导出 1688
        {
            List<Delivery1688PackageExcel> delivery1688PackageExcelList = this.list1688DeliveryPackage(exportExcelReq);
            return FilePlusUtils.exportExcel(exportExcelReq, delivery1688PackageExcelList, Delivery1688PackageExcel.class);
        } else if (type.equals(DeliveryPackageTypeEnum.AE_DELIVERY_PACKAGE.getType()))    //导出 AE
        {
            List<DeliveryAePackageExcel> deliveryAePackageExcelList = this.listAeDeliveryPackage(exportExcelReq);
            return FilePlusUtils.exportExcel(exportExcelReq, deliveryAePackageExcelList, DeliveryAePackageExcel.class);

        } else if (type.equals(DeliveryPackageTypeEnum.CJ_DELIVERY_PACKAGE.getType())) {     //导出 CJ
            List<DeliveryAePackageExcel> deliveryAePackageExcelList = this.listAeDeliveryPackage(exportExcelReq);
            return FilePlusUtils.exportExcel(exportExcelReq, deliveryAePackageExcelList, DeliveryAePackageExcel.class);
        }
        throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200101, "请输入正确导出的类型");
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

            //首先查询发货单数据，如果发货单数据不存在，那么插入数据，如果存在，那么则修改数据
            LogisticsPackageDO logisticsPackageDO = Optional.ofNullable(logisticsPackageMapper.selectOne(logisticsPackageDOLambdaQueryWrapper)).orElse(new LogisticsPackageDO());
            Long id = logisticsPackageDO.getId();
            ///////////////////////////如何内容相同不必更新/////////////////////////////////
            List<DeliveryPackageDto.DeliveryPackageTrackDto> trackList = Optional.ofNullable(deliveryPackageItemDto.getTrackList()).orElseGet(() -> Collections.emptyList());
            DeliveryPackageDto.DeliveryPackageTrackDto trackDto = trackList.stream().sorted(Comparator.comparing(DeliveryPackageDto.DeliveryPackageTrackDto::getEventDate).reversed()).findFirst().orElseGet(() -> new DeliveryPackageDto.DeliveryPackageTrackDto());
            try {
                String lastDate = simpleDateFormate.format(trackDto.getEventDate());
                if (id != null && !checkRemoteLogisticsTrackIsUpdateByLastEventTime(logisticsPackageDO, lastDate)) {
                    return;
                }
            } catch (Exception e) {
                log.error("物流跟踪--解析AE时间异常，时间是：{}", trackDto.getEventDate());
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

            if (id == null) {     //插入发货单数据
                // 这些状态值在第一次插入就可以了，后面就不再修改了
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
            } else {     //插入发货单id, 修改发货单数据
                tempLogisticsPackageDO.setId(id);
                logisticsPackageMapper.updateById(tempLogisticsPackageDO);
            }

            if (trackFlag) {    //物流轨迹实现
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

        //查询发货包裹数据
        LambdaQueryWrapper<LogisticsPackageDO> logisticsPackageDOLambdaQueryWrapper = Wrappers.<LogisticsPackageDO>lambdaQuery()
                .eq(LogisticsPackageDO::getIsValid, Constant.YES)
                .in(LogisticsPackageDO::getSalesOrderId, salesOrderLogisticsDto.getSalesOrderIdList());
        List<LogisticsPackageDO> logisticsPackageDOList = logisticsPackageMapper.selectList(logisticsPackageDOLambdaQueryWrapper);
        List<Long> packageIdList = logisticsPackageDOList.stream().map(LogisticsPackageDO::getId).collect(Collectors.toList());
        Map<String, List<LogisticsPackageDO>> logisticsPackageDOMap = logisticsPackageDOList.stream().collect(Collectors.groupingBy(LogisticsPackageDO::getSalesOrderId));

        Map<Long, List<LogisticsPackageItemDO>> logisticsPackageItemDOMap = new HashMap<>();
        if (packageIdList.size() != 0) {
            //查询发货包裹 item 数据
            LambdaQueryWrapper<LogisticsPackageItemDO> logisticsPackageItemDOLambdaQueryWrapper = Wrappers.<LogisticsPackageItemDO>lambdaQuery()
                    .in(LogisticsPackageItemDO::getPackageId, packageIdList);
            List<LogisticsPackageItemDO> logisticsPackageItemDOList = logisticsPackageItemMapper.selectList(logisticsPackageItemDOLambdaQueryWrapper);
            logisticsPackageItemDOMap.putAll(logisticsPackageItemDOList.stream().collect(Collectors.groupingBy(LogisticsPackageItemDO::getPackageId)));
        }

        List<SalesOrderLogisticsGTO.SalesOrderLogisticsResponse> salesOrderLogisticsResponseDtoList = new ArrayList<>();
        salesOrderIdList.forEach(salesOrderId -> {
            List<LogisticsPackageDO> tempLogisticsPackageDOList = Optional.ofNullable(logisticsPackageDOMap.get(salesOrderId)).orElse(new ArrayList<>());      //发货包裹数据
            Map<Integer, List<LogisticsPackageDO>> tempLogisticsPackageDOMap = tempLogisticsPackageDOList.stream().collect(Collectors.groupingBy(LogisticsPackageDO::getType));
            Map<String, SalesOrderLogisticsGTO.RepeatedSalesOrderLogisticsItemResponse> salesOrderLogisticsItemResponseDtoMap = new HashMap<>();
            tempLogisticsPackageDOMap.forEach((type, logisticsPackageList) -> {
                List<SalesOrderLogisticsGTO.SalesOrderLogisticsItemResponse> salesOrderLogisticsItemResponseList = new ArrayList<>();
                DeliveryPackageTypeEnum deliveryPackageTypeEnum = DeliveryPackageTypeEnum.getDeliveryPackageTypeEnumByType(type);
                if (deliveryPackageTypeEnum.equals(DeliveryPackageTypeEnum.ABROAD_DELIVERY_PACKAGE))     // 1688 渠道物流
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
                } else      //AE 渠道物流
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

            //设置 salesOrderid 组合的数据
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

        //查询发货包裹数据
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
                // 将渠道订单号拆分成单个值
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
            } else   //AE 查询的是发货包裹的数据
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
                throw new GlobalException(ExceptionEnum.REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), "包裹号：【" + logisticsPackageDO.getLogisticsOrderNo() + "】已存在，请重新输入或使用系统默认！");
            }
        } else {
            logisticsPackageDO.setLogisticsOrderNo(logisticsCommonUtils.generateDeliveryPackageNo());
        }
        logisticsPackageDO.setDeliveryStatus(DeliveryPackageDeliveryStatusEnum.PREPARATION.getCode());
        logisticsPackageDO.setLogisticsStatus(DeliveryPackageLogisticsStatusEnum.CREATED.getCode());
        // 获取面单
        ILogisticManager logisticManager = logisticFactory.getLogisticTypeFromCode(logisticsPackageAddReq.getLogisticsCode());
        LogisticOrderSearchDto searchDto = new LogisticOrderSearchDto();
        searchDto.setOrderNo(logisticsPackageAddReq.getLogisticsWayBillNo());
        searchDto.setLogisticsOrderNo(logisticsPackageDO.getLogisticsOrderNo());
        searchDto.setWayBillNo(logisticsPackageDO.getLogisticsWayBillNo());
        LogisticPrintLabelResponseDto logisticPrintLabelResponseDto = logisticManager.getLogisticPrintLabel(searchDto);
        if (logisticPrintLabelResponseDto.isSuccessSign()) {
            logisticsPackageDO.setLogisticsLabelUrl(logisticPrintLabelResponseDto.getLogisticsLabel());
        } else {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "查询面单失败");
        }
        return logisticsPackageMapper.insert(logisticsPackageDO) > 1 ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public Boolean updatePackage(LogisticsPackageAddReq logisticsPackageAddReq) {
        long total = logisticsPackageAddReq.getLogisticsPackageItems().stream()
                .filter(item -> item.getChannel().equals(DeliveryPackagePurchaseChannelEnum.AE.getType()) || item.getChannel().equals(DeliveryPackagePurchaseChannelEnum.CJ.getType())).count();
        if (total > 0) {
            throw new GlobalException(ExceptionEnum.REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), "需要添加包裹的商品的渠道只能是1688或备货、淘宝、拼多多");
        }
        List<LogisticsPackageItemReq> logisticsPackageItems = logisticsPackageAddReq.getLogisticsPackageItems();
        long count1 = logisticsPackageItems.stream().filter(item -> StringUtils.isNotBlank(item.getPurchaseId())).count();
        long count2 = logisticsPackageItems.stream().filter(item -> StringUtils.isNotBlank(item.getItemId())).count();
        if (count1 < 1 || count2 < 1) {
            throw new GlobalException(ExceptionEnum.REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), "需要添加包裹的商品不能为空，purchaseId,itemId至少有一个值");
        }
        //包裹单 item 数据
        LambdaQueryWrapper<LogisticsPackageItemDO> queryWrapper = Wrappers.<LogisticsPackageItemDO>lambdaQuery()
                .in(LogisticsPackageItemDO::getPurchaseId, logisticsPackageItems.stream().map(LogisticsPackageItemReq::getPurchaseId).collect(Collectors.toList()));
        List<LogisticsPackageItemDO> logisticsPackageItemDOs = logisticsPackageItemMapper.selectList(queryWrapper);
        if (Objects.isNull(logisticsPackageItemDOs) || logisticsPackageItemDOs.size() < 1) {
            throw new GlobalException(ExceptionEnum.REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), "数据库没找到对应的销售订单【" + logisticsPackageAddReq.getSalesOrderId() + "】下，采购id数据");
        }
        Set<Long> pacakgeIdList = logisticsPackageItemDOs.stream().map(LogisticsPackageItemDO::getPackageId).collect(Collectors.toSet());
        //包裹单数据
        LambdaQueryWrapper<LogisticsPackageDO> queryWrapperForPackage = Wrappers.<LogisticsPackageDO>lambdaQuery()
                .eq(LogisticsPackageDO::getSalesOrderId, logisticsPackageAddReq.getSalesOrderId())
                .eq(LogisticsPackageDO::getIsValid, Constant.YES)
                .in(LogisticsPackageDO::getId, pacakgeIdList);
        List<LogisticsPackageDO> logisticsPackageDOList = logisticsPackageMapper.selectList(queryWrapperForPackage);
        if (Objects.isNull(logisticsPackageDOList) || logisticsPackageDOList.size() < 1) {
            throw new GlobalException(ExceptionEnum.REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), "数据库没找到对应的销售订单【" + logisticsPackageAddReq.getSalesOrderId() + "】的数据");
        }
        LambdaQueryWrapper<LogisticsPackageDO> newLogisticPackageQuery = Wrappers.<LogisticsPackageDO>lambdaQuery()
                .eq(LogisticsPackageDO::getLogisticsOrderNo, logisticsPackageAddReq.getLogisticsOrderNo())
                .eq(LogisticsPackageDO::getIsValid, Constant.YES);
        LogisticsPackageDO newLogisticPackage = logisticsPackageMapper.selectOne(newLogisticPackageQuery);
        if (Objects.isNull(newLogisticPackage) || Objects.isNull(newLogisticPackage.getId())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "查询添加的包裹不存在，或者已经设置为无效，请核查！");
        }
        // 仅支持制单失败 和 已发送状态
        HashSet<Integer> hashSet = new HashSet(8);
        hashSet.add(DeliveryPackageDeliveryStatusEnum.PREPARATIONFAIL.getCode());
        hashSet.add(DeliveryPackageDeliveryStatusEnum.SENDED.getCode());
        if (logisticsPackageDOList.stream()
                .filter(vo -> !hashSet.contains(vo.getDeliveryStatus())).count() > 0) {
            throw new GlobalException(ExceptionEnum.REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), "仅支持制单失败和已发送状态才可以添加到新的包裹单");
        }
        ///包裹单地址数据
        LambdaQueryWrapper<LogisticsPackageAddressDO> queryWrapperForAdress = Wrappers.<LogisticsPackageAddressDO>lambdaQuery()
                .in(LogisticsPackageAddressDO::getPackageId, pacakgeIdList);
        List<LogisticsPackageAddressDO> logisticsPackageAddressDOList = logisticsPackageAddressMapper.selectList(queryWrapperForAdress);
        if (logisticsPackageAddressDOList.isEmpty()) {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "销售单地址异常");
        }
//        LogisticsPackageAddressDO logisticsPackageAddressDO = new LogisticsPackageAddressDO();
//        BeanUtils.copyProperties(logisticsPackageAddressDOList.get(0),logisticsPackageAddressDO);
        // 远程获得地址
        AddressDto addressDto = getAddressDtoFromRemoteOrderService(logisticsPackageAddReq.getSalesOrderId());
        LogisticsPackageAddressDO logisticsPackageAddressDO = new LogisticsPackageAddressDO();
        BeanUtils.copyProperties(addressDto, logisticsPackageAddressDO);
        //包裹单商品数据
        LambdaQueryWrapper<LogisticsProductDO> logisticsProductDOLambdaQueryWrapper = Wrappers.<LogisticsProductDO>lambdaQuery()
                .in(LogisticsProductDO::getRelationId, pacakgeIdList)
                .eq(LogisticsProductDO::getType, PackageProductTypeEnum.DELIVERY_PACKAGE.getType());
        List<LogisticsProductDO> logisticsProductDOList = logisticsProductMapper.selectList(logisticsProductDOLambdaQueryWrapper);

        // 如果包裹还有商品，不能设置为无效.
        List<LogisticsPackageItemDO> addToNewPackageItems = logisticsPackageItemDOs;
        Map<Long, List<LogisticsPackageItemDO>> mapByAddToNewPackageItems = addToNewPackageItems.stream().collect(Collectors.groupingBy(LogisticsPackageItemDO::getPackageId));
        LambdaQueryWrapper<LogisticsPackageItemDO> queryWrapperForAllToNewPackage = Wrappers.<LogisticsPackageItemDO>lambdaQuery()
                .in(LogisticsPackageItemDO::getPackageId, logisticsPackageDOList.stream().map(LogisticsPackageDO::getId).collect(Collectors.toSet()));
        List<LogisticsPackageItemDO> allLogisticsItem = logisticsPackageItemMapper.selectList(queryWrapperForAllToNewPackage);
        Map<Long, List<LogisticsPackageItemDO>> mapByAllLogisticsItem = allLogisticsItem.stream().collect(Collectors.groupingBy(LogisticsPackageItemDO::getPackageId));
        // 设置无效标识的包裹
        List<LogisticsPackageItemDO> toVaildList = new ArrayList<>();
        List<LogisticsPackageItemDO> toDeletedItemList = new ArrayList<>();
        mapByAddToNewPackageItems.entrySet().forEach(item -> {
            if (item.getValue().size() == mapByAllLogisticsItem.get(item.getKey()).size()) {
                toVaildList.addAll(item.getValue());
            } else {
                toDeletedItemList.addAll(item.getValue());
            }
        });
        // 设置无效
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
        // 设置删除
        if (!toDeletedItemList.isEmpty()) {
            List<Long> list = toDeletedItemList.stream().map(LogisticsPackageItemDO::getId).collect(Collectors.toList());
            LambdaUpdateWrapper<LogisticsPackageItemDO> updateWrapperForDelete = Wrappers.<LogisticsPackageItemDO>lambdaUpdate()
                    .set(LogisticsPackageItemDO::getIsDeleted, Constant.YES)
                    .set(LogisticsPackageItemDO::getUpdateBy, StringUtils.isNotBlank(UserContext.getCurrentUserName()) ? UserContext.getCurrentUserName() : Constant.SYSTEM_ROOT)
                    .in(LogisticsPackageItemDO::getId, list);
            logisticsPackageItemMapper.update(null, updateWrapperForDelete);
        }
        ////////--------------------------新包裹操作------------------------////////////////////
        List<LogisticsPackageDO> packageDOS = logisticsPackageDOList;
        // 查询新包裹内容及插入新的item
        newLogisticPackage.setSalesOrderId(logisticsPackageAddReq.getSalesOrderId());
        newLogisticPackage.setChannelOrderId(packageDOS.stream().map(LogisticsPackageDO::getChannelOrderId).collect(Collectors.joining(",")));
        newLogisticPackage.setUpdateBy(StringUtils.isNotBlank(UserContext.getCurrentUserName()) ? UserContext.getCurrentUserName() : Constant.SYSTEM_ROOT);
        newLogisticPackage.setIsValid(Constant.YES);
        logisticsPackageMapper.updateById(newLogisticPackage);
        LambdaQueryWrapper<LogisticsPackageItemDO> queryForNewItem = Wrappers.<LogisticsPackageItemDO>lambdaQuery()
                .eq(LogisticsPackageItemDO::getPackageId, newLogisticPackage.getId());
        List<LogisticsPackageItemDO> logisticsPackageItemDOForNewPackageList = logisticsPackageItemMapper.selectList(queryForNewItem);
        List<DepositoryPurchaseStatusMsgDto> depositoryPurchaseStatusMsgDtoList = new ArrayList<>(); // mq 消息对象
        if (logisticsPackageItemDOForNewPackageList.isEmpty()) {
            // 新的包裹
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
            // 地址插入
            logisticsPackageAddressDO.setId(null);
            logisticsPackageAddressDO.setPackageId(newLogisticPackage.getId());
            logisticsPackageAddressDO.setCreateBy(StringUtils.isNotBlank(UserContext.getCurrentUserName()) ? UserContext.getCurrentUserName() : Constant.SYSTEM_ROOT);
            logisticsPackageAddressMapper.insert(logisticsPackageAddressDO);
            // 商品详情插入
            logisticsProductDOList.stream().forEach(item -> {
                item.setRelationId(newLogisticPackage.getId());
                item.setCreateTime(null);
                item.setUpdateTime(null);
                item.setId(null);
            });
            logisticsProductMapper.insertLogisticsProductBatch(logisticsProductDOList);
            // 发布制单状态到mq
            logisticsPackageItems.stream().forEach(item -> {
                DepositoryPurchaseStatusMsgDto msgDto = new DepositoryPurchaseStatusMsgDto();
                msgDto.setPurchaseId(item.getPurchaseId());
                msgDto.setItemId(item.getItemId());
                msgDto.setSalesOrderId(logisticsPackageAddReq.getSalesOrderId());
                depositoryPurchaseStatusMsgDtoList.add(msgDto);
            });
        } else {
            // 新包裹有数据（去重）
            Map<String, List<LogisticsPackageItemReq>> mapByAddItem = logisticsPackageItems.stream().collect(Collectors.groupingBy(LogisticsPackageItemReq::getItemId));
            // 过滤掉已经存在数据itemId
            Set<String> exsitedItemSet = logisticsPackageItemDOForNewPackageList.stream().map(LogisticsPackageItemDO::getItemId).collect(Collectors.toSet());
            // 移除已存在的itemId
            exsitedItemSet.forEach(item -> mapByAddItem.remove(item));
            if (mapByAddItem.isEmpty()) {
                return Boolean.TRUE;
            }
            LambdaQueryWrapper<LogisticsProductDO> wrapperForNewProduct = Wrappers.<LogisticsProductDO>lambdaQuery()
                    .eq(LogisticsProductDO::getRelationId, newLogisticPackage.getId());
            List<LogisticsProductDO> existedProducts = logisticsProductMapper.selectList(wrapperForNewProduct);
            Set<String> existedProductSet = existedProducts.stream().map(LogisticsProductDO::getSku).collect(Collectors.toSet());
            // 过滤掉已经存在数据sku
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
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "至少选择一个需要通知的包裹");
        }
        LambdaQueryWrapper<LogisticsPackageDO> queryWrapper = Wrappers.<LogisticsPackageDO>lambdaQuery().in(LogisticsPackageDO::getId, idList);
        List<LogisticsPackageDO> logisticsPackageDOS = logisticsPackageMapper.selectList(queryWrapper);
        if (Objects.isNull(logisticsPackageDOS) || logisticsPackageDOS.isEmpty()) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "查询不到需要通知的数据");
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
                throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "系统中已存在该物流追踪号：" + (trackCode.contains("-") ? StringUtils.substring(trackCode, trackCode.indexOf("-")) : trackCode));
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
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "单次导入应小于等于1000行");
            }
            if (logisticsTrackingCodeUpdateImportReqList.size() == 0) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "单次导入应大于等于0行");
            }
            // 去掉前后的空格
            logisticsTrackingCodeUpdateImportReqList.stream().forEach(item -> {
                item.setChannelOrderId(StringUtils.isNotBlank(item.getChannelOrderId()) ? StringUtils.deleteWhitespace(item.getChannelOrderId()) : null);
                item.setNewLogisticsTrackingCode(StringUtils.isNotBlank(item.getNewLogisticsTrackingCode()) ? StringUtils.deleteWhitespace(item.getNewLogisticsTrackingCode()) : null);
                item.setOldLogisticsTrackingCode(StringUtils.isNotBlank(item.getOldLogisticsTrackingCode()) ? StringUtils.deleteWhitespace(item.getOldLogisticsTrackingCode()) : null);
            });
            // 验证数据存在性
            Set<String> oldTrackCodeSet = logisticsTrackingCodeUpdateImportReqList.stream().map(item -> item.getOldLogisticsTrackingCode()).collect(Collectors.toSet());
            if (oldTrackCodeSet.isEmpty() || oldTrackCodeSet.stream().allMatch(item ->item==null)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "请安模板格式填写数据");
            }
            // 境外 物流追踪号  AE 渠道订单号 + 物流追踪号
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
                        req.setErrorMsg("追踪号必须唯一，存在多个相同的追踪号:"+item.getValue().get(0).getNewLogisticsTrackingCode());
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
                            item.setErrorMsg("系统中已存在该物流追踪号：" + (trackCode.contains("-") ? StringUtils.substring(trackCode, trackCode.indexOf("-")) : trackCode));
                            atomicReferenceErrorFlag.set(true);
                        });
                    }
                }
                for (Map.Entry<String, List<LogisticsTrackingCodeUpdateImportReq>> item : mapByUpdateChannelTrack.entrySet()) {
                    List<LogisticsPackageDO> logisticsPackageDOS1 = mapByQueryChannelTrack.get(item.getKey());
                    if (Objects.isNull(logisticsPackageDOS1) || logisticsPackageDOS1.isEmpty()) {
                        item.getValue().get(0).setErrorMsg("在数据库中不能找到该数据，请核实");
                        atomicReferenceErrorFlag.set(true);
                    } else {
                        item.getValue().get(0).setId(logisticsPackageDOS1.get(0).getId());
                    }
                }
            }
            if (!atomicReferenceErrorFlag.get()) {
                // 验证null
                StringBuilder sb = new StringBuilder();
                logisticsTrackingCodeUpdateImportReqList.forEach(logisticsTrackingCodeUpdateImportReq ->{
                    try {
                        sb.setLength(0); // 重新计算
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
                        log.error("AE批量EXCEL换单发成错误,cause {}", e);
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,e.getMessage());
                    }
                });
            }
            // 存在异常
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
            log.error("AE批量EXCEL换单发成错误,cause {}", e);
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
                    // 境外
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
            //轨迹字段脱敏
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
        //发货单物流轨迹，先删除物流轨迹，然后再插入
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
     * 删除包裹单号下所有物流轨迹，插入最新的物流轨迹信息
     *
     * @param logisticsPackageTrackDOList 物流轨迹集合
     * @param packageId                   包裹单号ID
     * @return 是否成功
     * @auth qiuhao
     */
    private Boolean updateLogisticsTrack(List<LogisticsPackageTrackDO> logisticsPackageTrackDOList, Long packageId) {
        //发货单物流轨迹，先删除物流轨迹，然后再插入
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
        //更新物流轨迹信息
        updateLogisticsTrack(logisticsPackageTrackDOList, packageId);
        //更新物流包裹单信息
        updateLogisticsPackage(logisticsPackageDO);
        return true;
    }


    private List<Delivery1688PackageExcel> list1688DeliveryPackage(ExportExcelReq<DeliveryPackageReq> exportExcelReq) {
        DeliveryPackageReq deliveryPackageReq = Optional.ofNullable(exportExcelReq.getData()).orElse(new DeliveryPackageReq());
        if (exportExcelReq.getIdList() != null && exportExcelReq.getIdList().size() != 0)        //选择 id  List
        {
            deliveryPackageReq.setIdList(exportExcelReq.getIdList());
        }

        List<LogisticsPackageDO> logisticsPackageDOList = logisticsPackageMapper.queryLogisticsPackageList(null, deliveryPackageReq);
        List<Delivery1688PackageExcel> delivery1688PackageExcelList = new ArrayList<>();
        logisticsPackageDOList.forEach(logisticsPackageDO -> {
            Delivery1688PackageExcel delivery1688PackageExcel = new Delivery1688PackageExcel();
            delivery1688PackageExcel.setLogisticsOrderNo(logisticsPackageDO.getLogisticsOrderNo());
            delivery1688PackageExcel.setLogisticsWayBillNo(logisticsPackageDO.getLogisticsWayBillNo());
            delivery1688PackageExcel.setIsValid(logisticsPackageDO.getIsValid() == Constant.YES ? "是" : "否");
            delivery1688PackageExcel.setLogisticsTrackingCode(logisticsPackageDO.getLogisticsTrackingCode());
            delivery1688PackageExcel.setLogisticsName(logisticsPackageDO.getLogisticsName());
            delivery1688PackageExcel.setChannelName(logisticsPackageDO.getChannelName());
            delivery1688PackageExcel.setLogisticsArea(logisticsPackageDO.getLogisticsArea());
            delivery1688PackageExcel.setDeliveryStatus(DeliveryPackageDeliveryStatusEnum.getDeliveryPackageDeliveryStatusEnumByCode(logisticsPackageDO.getDeliveryStatus()).getName());
            delivery1688PackageExcel.setLogisticsNode(logisticsPackageDO.getLogisticsNode());
            delivery1688PackageExcel.setSalesOrderId(logisticsPackageDO.getSalesOrderId());
            delivery1688PackageExcel.setErrorInfo(logisticsPackageDO.getErrorInfo());
            delivery1688PackageExcel.setNoticeUser(logisticsPackageDO.getNoticeUser().equals(Constant.YES) ? "是" : "否");
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
        if (exportExcelReq.getIdList() != null && exportExcelReq.getIdList().size() != 0)        //选择 id  List
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
        //查询面单号
        if (StringUtils.isBlank(logisticsPackageDO.getLogisticsLabelUrl())) {
            LogisticPrintLabelResponseDto logisticPrintLabelResponseDto = logisticManager.getLogisticPrintLabel(logisticOrderSearchDto);
            if (logisticPrintLabelResponseDto.isSuccessSign()) {
                expressDto.setTrackingNumber(logisticsPackageDO.getLogisticsTrackingCode());     //专门用于发货的
                expressDto.setLabelUrl(logisticPrintLabelResponseDto.getLogisticsLabel());
                expressDto.setServiceId(WBDepositoryRegisterServiceConfig.getServicerIdFromCode(logisticsPackageDO.getLogisticsCode(), logisticsPackageDO.getLogisticsChannel()));

            } else {
                throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "查询面单失败");
            }
        }
//        //查询追踪号
//        if (StringUtils.isBlank(logisticsPackageDO.getLogisticsTrackingCode())) {
//            logisticOrderSearchDto.setProcessCode(logisticsPackageDO.getLogisticsProcessCode());
//            LogisticOrderDetailResponseDto logisticDetail = logisticManager.getLogisticDetail(logisticOrderSearchDto);
//            if (logisticDetail.isSuccessSign()) {
//                expressDto.setTrackingNumber(logisticDetail.getTrackingNumber());
//            } else {
//                throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "获取追踪号失败");
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
            if (logisticsPackageDO.getType().equals(DeliveryPackageTypeEnum.ABROAD_DELIVERY_PACKAGE.getType())) {        //只有境外发货单才才能更新物流信息
                // 这里添加一个监听，可以跟随这个Job一起运行---》推送“已制单”到仓库
                try {
                    applicationContext.publishEvent(new LogisticPackageAgainPushEvent(logisticsPackageDO));
                    String logisticsTrackingCode = logisticsPackageDO.getLogisticsTrackingCode();       //物流跟踪单号
                    ILogisticManager logisticManager = logisticFactory.getLogisticTypeFromCode(logisticsPackageDO.getLogisticsCode());
                    LogisticOrderSearchDto logisticOrderSearchDto = new LogisticOrderSearchDto();
                    logisticOrderSearchDto.setLogisticsOrderNo(logisticsPackageDO.getLogisticsOrderNo());
                    logisticOrderSearchDto.setProcessCode(logisticsPackageDO.getLogisticsProcessCode());
                    logisticOrderSearchDto.setWayBillNo(logisticsPackageDO.getLogisticsWayBillNo());
                    //如果库里的物流跟踪单号为空，那么需要查询详情的数据
                    LogisticsPackageDO tempLogisticsPackageDO = new LogisticsPackageDO();
                    tempLogisticsPackageDO.setId(logisticsPackageDO.getId());
                    tempLogisticsPackageDO.setSalesOrderId(logisticsPackageDO.getSalesOrderId());
                    tempLogisticsPackageDO.setLogisticsTrackingCode(logisticsTrackingCode);
                    if (StringUtils.isBlank(logisticsTrackingCode)) {
                        LogisticOrderDetailResponseDto logisticOrderDetailResponseDto = logisticManager.getLogisticDetail(logisticOrderSearchDto);
                        //查询结果失败、查询物流跟踪单号为空
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

                        // 判断节点的揽收  @qiuhao修改判断是否揽收需要判断为空
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
                                    log.info("报文中存在物流揽收轨迹信息，没有物流揽收轨迹时间" + JSON.toJSONString(logisticsPackageDOList));
                                }
                            }
                        }
                        if (StringUtils.isNoneBlank(lastNode)) {      //设置最新物流节点
                            tempLogisticsPackageDO.setLogisticsNodeEn(lastNode);
                            List<LogisticsTrackNodeDO> logisticsTrackNodeDOS = doGetLogisticsTrackNodeList(DeliveryPackageTypeEnum.ABROAD_DELIVERY_PACKAGE.getType());
                            Map<String, List<LogisticsTrackNodeDO>> mapByNode = logisticsTrackNodeDOS.stream().collect(Collectors.groupingBy(LogisticsTrackNodeDO::getNode));
                            tempLogisticsPackageDO.setLogisticsNode(Optional.ofNullable(mapByNode.get(lastNode)).orElse(Collections.singletonList(new LogisticsTrackNodeDO())).get(0).getInsideCn());
                        }

                        if (isEnd) {       //如果当前物流为终态
                            isEndToSendMessage = true;
                            tempLogisticsPackageDO.setLogisticsStatus(DeliveryPackageLogisticsStatusEnum.RECEIVED.getCode());       //已收货
                            String receiveGoodTimeStr = logisticTrackResponseDto.getReceiveGoodTime();
                            Date receiveGoodTime = null;
                            if (StringUtils.isNotBlank(receiveGoodTimeStr)) {
                                if (receiveGoodTimeStr.contains("T")) {
                                    receiveGoodTimeStr = receiveGoodTimeStr.replaceAll("'", "").replaceAll("T", " ");
                                }
                                try {
                                    receiveGoodTime = simpleDateFormate.parse(receiveGoodTimeStr);
                                } catch (ParseException e) {
                                    log.error("解析物流轨迹接受日期失败：{}", receiveGoodTimeStr);
                                }
                            }
                            tempLogisticsPackageDO.setReceivingGoodTime(Objects.isNull(receiveGoodTime) ? new Date() : receiveGoodTime);        //设置收货时间
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
                        logisticsPackageTrackMapper.deleteLogisticsPackageTrackByPackageId(logisticsPackageDO.getId());     //删除轨迹数据
                        if (logisticsPackageTrackDOList.size() != 0)
                            logisticsPackageTrackMapper.insertLogisticsPackageTrackBatch(logisticsPackageTrackDOList);     //插入物流轨迹数据
                    }

                    //修改物流数据
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
                    log.error("获取1688物流轨迹失败，查询对象是：{}，失败原因：{}", logisticsPackageDO, e);
                }
            }
        }
    }

    @Autowired
    @Getter
    private RestTemplate restTemplate;

    public void doSegmentRefreshCjDeliveryPackage(List<LogisticsPackageDO> logisticsPackageDOList) {
        //分段调用
        List<List> segment = ListUtil.segment(logisticsPackageDOList, 20);
        for (List list : segment) {
            try {
                //调用物流轨迹更新
                doRefreshCjDeliveryPackage(list);
            }catch (Exception e){
                log.error("获取CJ物流轨迹异常" + e.getMessage());
            }

            //更新物流状态  依赖订单
            try {
                if (list != null) {
                    ArrayList<String> strings = new ArrayList<>();
                    for (Object logisticsPackageDO : list) {
                        LogisticsPackageDO a = (LogisticsPackageDO) logisticsPackageDO;
                        strings.add(a.getChannelCode());
                    }
                    CjChannelOrderGTO.ListCjChanelOrderInfoRequest build = CjChannelOrderGTO.ListCjChanelOrderInfoRequest.newBuilder().addAllChannelOrderId(strings).build();
                    //更新状态
                    CjChannelOrderGTO.ListCjChanelOrderInfoReply listCjChanelOrderInfoReply = cjChannelOrderClient.listCjChannelOrderInfo(build);
                    //成功执行逻辑
                    if (listCjChanelOrderInfoReply != null && listCjChanelOrderInfoReply.getSuccess()) {
                        List<CjChannelOrderGTO.CjChannelOrderInfo> dataList = listCjChanelOrderInfoReply.getDataList();
                        if (dataList != null && dataList.size() > 0) {
                            for (CjChannelOrderGTO.CjChannelOrderInfo cjChannelOrderInfo : dataList) {
                                String orderStatus = cjChannelOrderInfo.getOrderStatus();
                                if (orderStatus != null) {
                                    //如果是已完成和取消状态  修改物流状态为终态不再跟踪
                                    if (orderStatus.equals(CjOrderStatusEnum.DELIVERED.getStatus())) {
                                        updateLogisticsStatus(cjChannelOrderInfo.getChannelOrderId(), DeliveryPackageLogisticsStatusEnum.RECEIVED.getCode());
                                    }
                                    if (orderStatus.equals((CjOrderStatusEnum.CANCELLED.getStatus()))) {
                                        updateLogisticsStatus(cjChannelOrderInfo.getChannelOrderId(), DeliveryPackageLogisticsStatusEnum.CANCEL.getCode());
                                    }
                                }
                            }
                        } else {
                            log.info("调用订单获取订单状态信息空返回:" + dataList);
                        }
                    } else {
                        //异常打印日志信息
                        if (listCjChanelOrderInfoReply != null) {
                            log.error("调用订单获取订单状态信息异常:" + listCjChanelOrderInfoReply.getErrorCode() + listCjChanelOrderInfoReply.getMsg());
                        } else {
                            log.error("调用订单获取订单状态信息异常:" + "null");
                        }
                    }
                }
            } catch (Exception e) {
                log.error("调用订单获取订单信息异常:" + e.getMessage());
            } finally {
                continue;
            }
        }

    }


    public void updateLogisticsStatus(String cjChannelOrderId, Integer logisticsStatus) {
        //30  已收货(已签收)   50 已取消
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
        // 增加发送消息到Mq通知平台
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
                    log.error("获取CJ物流轨迹异常" + e.getMessage());
                }
            }
            if (trackNumber != null && HttpStatus.OK.value() == trackNumber.getCode()) {
                List<CjTrackNumberFindDTO> data = trackNumber.getData();
                if (data != null && data.size() > 0) {
                    //开始业务逻辑
                    for (CjTrackNumberFindDTO cjTrackNumberFindDTO : data) {
                        for (LogisticsPackageDO logisticsPackageDO : logisticsPackageDOList) {
                            if (cjTrackNumberFindDTO != null && logisticsPackageDO != null) {
                                String trackingNumber = cjTrackNumberFindDTO.getTrackingNumber();
                                //如果 物流跟踪号一致，取数据
                                if (trackingNumber != null && trackingNumber.equals(logisticsPackageDO.getLogisticsTrackingCode())) {
                                    List<CjTrackNumberFindDTO.CJLogisticRouteResponse> routes = cjTrackNumberFindDTO.getRoutes();
                                    if (routes != null) {
                                        // 判断是否需要更新
                                        CjTrackNumberFindDTO.CJLogisticRouteResponse cjLogisticRouteResponse = routes.stream().sorted(Comparator.comparing(CjTrackNumberFindDTO.CJLogisticRouteResponse::getAcceptTime).reversed()).findFirst().orElseGet(() -> new CjTrackNumberFindDTO.CJLogisticRouteResponse());
                                        boolean isUpdate = checkRemoteLogisticsTrackIsUpdateByLastEventTime(logisticsPackageDO, cjLogisticRouteResponse.getAcceptTime());
                                        if (!isUpdate) {
                                            continue;
                                        }
                                        trackNoToMqSet.add(trackingNumber);
                                        ArrayList<LogisticsPackageTrackDO> logisticsPackageTrackDOS = new ArrayList<>();
                                        Long id = logisticsPackageDO.getId();
                                        //最大轨迹时间
                                        String maxTrackTime = "0";
                                        for (CjTrackNumberFindDTO.CJLogisticRouteResponse route : routes) {
                                            if (route != null) {
                                                String remark = route.getRemark();
                                                if (StringUtils.isNotEmpty(remark)) {
                                         /*       remark = DesensibilisationUtil.toLowerCase(remark);
                                                remark = DesensibilisationUtil.cleanAddress(remark);
                                                remark = DesensibilisationUtil.cjCityReplace(remark);*/
                                                    //CJ物流状态code码
                                                    Integer cjLogisticsTrackStatus = DesensibilisationUtil.getCJLogisticsTrackStatus(remark);
                                                    LogisticsPackageTrackDO logisticsPackageTrackDO = new LogisticsPackageTrackDO();
                                                    logisticsPackageTrackDO.setPackageId(id);
                                                    logisticsPackageTrackDO.setId(SnowflakeIdUtils.getId());
                                                    //记录轨迹状态
                                                    logisticsPackageTrackDO.setStatus(LogisticTrackCJEnum.getLogisticTrackCJEnum(cjLogisticsTrackStatus).getName());
                                                    //记录轨迹详情
                                                    logisticsPackageTrackDO.setContent(remark);
                                                    logisticsPackageTrackDO.setLocation(route.getAcceptAddress());
                                                    String acceptTime = route.getAcceptTime();
                                                    if (StringUtils.isNotEmpty(acceptTime)) {
                                                        logisticsPackageTrackDO.setEventTime(acceptTime);
                                                        //如果状态是已妥投
                                                        int compareTo = maxTrackTime.compareTo(acceptTime);
                                                        if (compareTo < 0) {
                                                            maxTrackTime = acceptTime;
                                                            if (cjLogisticsTrackStatus == LogisticTrackCJEnum.SIGN.getCode()) {
                                                                logisticsPackageDO.setReceivingGoodTime(DateUtil.parse(acceptTime));
                                                            }
                                                            logisticsPackageDO.setLogisticsNode(LogisticTrackCJEnum.getLogisticTrackCJEnum(cjLogisticsTrackStatus).getName());
                                                            // logisticsPackageDO.set
                                                       /* if (cjLogisticsTrackStatus != null) {
                                                            //设置状态值
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
                //请求异常 返回
                String s = null;
                if (trackNumber != null) {
                    s = JSON.toJSONString(trackNumber);
                }
                log.error("CJ物流轨迹请求异常：{}" + s + "物流轨迹更新失败的跟踪号是：" + JSONArray.toJSONString(strings));
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
                //查询物流状态
                String logisticsChannelStatus = logisticsPackageDO.getLogisticsChannelStatus();
                OpenPlatformDTO<AliexpressTradeDsOrderGetResponse.AeopOrderInfo> openPlatformDTO = aeOpenUtils.orderGet(Long.valueOf(logisticsPackageDO.getChannelOrderId()));
                if (openPlatformDTO.getSuccess() && openPlatformDTO.getResult() != null) {
                    AliexpressTradeDsOrderGetResponse.AeopOrderInfo aeopOrderInfo = openPlatformDTO.getResult();
                    logisticsChannelStatus = aeopOrderInfo.getLogisticsStatus();
                    // AE 可能存在换物流单号
                    List<String> newTrackList = aeopOrderInfo.getLogisticsInfoList().stream().map(AliexpressTradeDsOrderGetResponse.AeopOrderLogisticsInfo::getLogisticsNo).collect(Collectors.toList());
                    if (!newTrackList.contains(logisticsPackageDO.getLogisticsTrackingCode()) && Constant.NO_0.equals(logisticsPackageDO.getHandleTrackingCode())) {
                        // 换单了
                        if (!notRefreshPackage.contains(logisticsPackageDO.getChannelOrderId())) {
                            TransactionCallback transactionCallback = (TransactionStatus status) -> {
                                LambdaQueryWrapper<LogisticsPackageDO> queryPackage = Wrappers.<LogisticsPackageDO>lambdaQuery()
                                        .eq(LogisticsPackageDO::getHandleTrackingCode,Constant.NO_0)
                                        .eq(LogisticsPackageDO::getChannelOrderId, logisticsPackageDO.getChannelOrderId());
                                List<LogisticsPackageDO> logisticsPackageDOS = logisticsPackageMapper.selectList(queryPackage);
                                // 更新
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
                        continue;// 更换单号的下次再查询物流
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
                log.error("获取AE物流轨迹失败，查询对象是：{}，失败原因：{}", logisticsPackageDO, e);
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

            //查询 AE 的物流轨迹数据
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
                    String status = deliveryPackageTrackDto.getStatus();        //最新物流节点
                    maybeReceiveGoodTime = deliveryPackageTrackDto.getEventDate();        //最新物流节点时间
                    for (LogisticsTrackNodeDO logisticsTrackNodeDO : logisticsTrackNodeDOList) {
                        String node = logisticsTrackNodeDO.getNode();
                        if (StringUtils.indexOfIgnoreCase(status, node) != -1) {
                            lastNode = logisticsTrackNodeDO.getInsideEn();
                            break;
                        }
                    }
                }

                // AE 物流终止
                if (StringUtils.indexOfIgnoreCase(lastNode, "Delivered") != -1 || StringUtils.indexOfIgnoreCase(lastNode, "elivered") != -1) {
                    deliveryPackageItemDto.setLogisticsStatus(DeliveryPackageLogisticsStatusEnum.RECEIVED.getCode());
                    deliveryPackageItemDto.setReceivingGoodTime(Objects.isNull(maybeReceiveGoodTime) ? (new Date()) : maybeReceiveGoodTime);
                }
                // AE 出库时间
                Optional<DeliveryPackageDto.DeliveryPackageTrackDto> sendTime = deliveryPackageTrackDtoList.stream().sorted(Comparator.comparing(DeliveryPackageDto.DeliveryPackageTrackDto::getEventDate)).findFirst();
                if (sendTime.isPresent()) {
                    deliveryPackageItemDto.setDeliveryTime(sendTime.get().getEventDate());
                }
                deliveryPackageItemDto.setLogisticsNode(lastNode);
                deliveryPackageItemDto.setTrackList(deliveryPackageTrackDtoList);       //设置物流轨迹数据
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
        //组装发送消息数据
        List<DepositoryPurchaseStatusMsgDto> depositoryPurchaseStatusMsgDtoList = new ArrayList<>();
        List<LogisticsPackageItemDO> tempLogisticsPackageItemDOList = Optional.ofNullable(logisticsPackageItemDOMap.get(logisticsPackageDO.getId())).orElse(new ArrayList<>());
        tempLogisticsPackageItemDOList.forEach(logisticsPackageItemDO -> {
            DepositoryPurchaseStatusMsgDto depositoryPurchaseStatusMsgDto = new DepositoryPurchaseStatusMsgDto();
            depositoryPurchaseStatusMsgDto.setSalesOrderId(logisticsPackageDO.getSalesOrderId());
            depositoryPurchaseStatusMsgDto.setPurchaseId(logisticsPackageItemDO.getPurchaseId());
            depositoryPurchaseStatusMsgDto.setItemId(logisticsPackageItemDO.getItemId());
            depositoryPurchaseStatusMsgDtoList.add(depositoryPurchaseStatusMsgDto);
        });

        //有数据才能发送消息
        if (depositoryPurchaseStatusMsgDtoList.size() != 0) {
            sendMsgUtils.sendPurchaseIntoDepositoryMsg(depositoryPurchaseStatusMsgDtoList, DepositoryPurchaseStatusEnum.CANCELSEND.getStatusCode());        //取消发货
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
            log.info("发送物流变动给平台MQ处理结果是【{}】,返回详情是：{}，发送到mq返回消息是,{}", sendResult.getSendStatus(), sendResult, message);
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
        log.info("防止短时间内多次人工推送仓库,运单号是：{}", logisticsWayBillNo);
        return redissLockUtil.tryLock(logisticsWayBillNo, TimeUnit.SECONDS, -1, 180);
    }

    private void removeOutDepositoryLimit(String logisticsWayBillNo) {
        log.info("防止短时间内多次人工推送仓库-移除锁定,运单号是：{}", logisticsWayBillNo);
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
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "销售单地址异常" + e.getMessage());
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

            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "销售单地址异常 获取到的数据存在空值");
        }
        return addressDto;
    }
}

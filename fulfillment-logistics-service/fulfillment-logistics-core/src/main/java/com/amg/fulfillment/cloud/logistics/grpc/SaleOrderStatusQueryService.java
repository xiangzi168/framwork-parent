package com.amg.fulfillment.cloud.logistics.grpc;

import com.alibaba.fastjson.JSON;
import com.amg.framework.boot.base.exception.GlobalException;
import com.amg.framework.cloud.grpc.utils.GrpcJsonFormatUtils;
import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import com.amg.fulfillment.cloud.logistics.api.enumeration.DeliveryPackageDeliveryStatusEnum;
import com.amg.fulfillment.cloud.logistics.api.enumeration.DeliveryPackageLogisticsStatusEnum;
import com.amg.fulfillment.cloud.logistics.api.enumeration.DepositoryPurchaseStatusEnum;
import com.amg.fulfillment.cloud.logistics.api.enumeration.PurchasePackageWarehousingTypeEnum;
import com.amg.fulfillment.cloud.logistics.api.grpc.SaleOrderStatusQueryGrpc;
import com.amg.fulfillment.cloud.logistics.api.proto.SaleOrderStatusQueryGTO;
import com.amg.fulfillment.cloud.logistics.entity.*;
import com.amg.fulfillment.cloud.logistics.enumeration.BaseLogisticsResponseCodeEnum;
import com.amg.fulfillment.cloud.logistics.mapper.*;
import com.amg.fulfillment.cloud.logistics.model.bo.DepositorySaleOrderDetailBO;
import com.amg.fulfillment.cloud.logistics.model.bo.LogisticsPackageBO;
import com.amg.fulfillment.cloud.purchase.api.client.PurchaseIdInfoSynchronizeServiceGrpcClient;
import com.amg.fulfillment.cloud.purchase.api.enums.ChannelEnum;
import com.amg.fulfillment.cloud.purchase.api.proto.GetPurchaseIdInfoByItemIdRequest;
import com.amg.fulfillment.cloud.purchase.api.proto.PurchaseIdInfo;
import com.amg.fulfillment.cloud.purchase.api.proto.PurchaseIdInfoByItemIdResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
public class SaleOrderStatusQueryService extends SaleOrderStatusQueryGrpc.SaleOrderStatusQueryImplBase {

    @Autowired
    private DepositorySaleOrderDetailMapper depositorySaleOrderDetailMapper;
    @Autowired
    private LogisticsPackageMapper logisticsPackageMapper;
    @Autowired
    private LogisticsPackageItemMapper logisticsPackageItemMapper;
    @Autowired
    private LogisticsPurchasePackageMapper logisticsPurchasePackageMapper;
    @Autowired
    private LogisticsStatusMapper logisticsStatusMapper;
    @Autowired
    private PurchaseIdInfoSynchronizeServiceGrpcClient purchaseIdInfoSynchronizeServiceGrpcClient;


    @Override
    public List<SaleOrderStatusQueryGTO.SaleOrderStatusQueryResponse> querySaleOrderStauts(SaleOrderStatusQueryGTO.SaleOrderStatusQueryRequest request, StreamObserver<SaleOrderStatusQueryGTO.SaleOrderStatusQueryResponseResult> responseObserver) {
        printLog(request);
        List<SaleOrderStatusQueryGTO.SaleOrderStatusQueryResponse> responses = doQuerySaleOrderStatus(request, false);
        return responses;
    }

    @Override
    public List<SaleOrderStatusQueryGTO.SaleOrderStatusQueryResponse> querySaleOrderStautsForReport(SaleOrderStatusQueryGTO.SaleOrderStatusQueryRequest request, StreamObserver<SaleOrderStatusQueryGTO.SaleOrderStatusQueryResponseResult> responseObserver) {
        printLog(request);
        List<SaleOrderStatusQueryGTO.SaleOrderStatusQueryResponse> responses = doQuerySaleOrderStatus(request, true);
        return responses;
    }

    private void printLog(SaleOrderStatusQueryGTO.SaleOrderStatusQueryRequest request) {
        String requestStr = GrpcJsonFormatUtils.toJsonString(request);
        log.info("GRPC--??????????????????????????????????????????????????????????????????????????????JSON??????{}", requestStr);
        if (request.getSaleOrderList().isEmpty()) {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100,"????????????????????????????????????id?????????null");
        }
    }

    private List<SaleOrderStatusQueryGTO.SaleOrderStatusQueryResponse> doQuerySaleOrderStatus(SaleOrderStatusQueryGTO.SaleOrderStatusQueryRequest request, boolean requiredQueryPurchaseInfo){
        List<String> saleOrderList = request.getSaleOrderList().stream().collect(Collectors.toList());
        List<DepositorySaleOrderDetailBO> intoDepositoryList = querySaleOrderIntoDepoistoryStatus(saleOrderList);
        // ?????????????????? ??????itemId ????????????????????????
        queryItemOfTypeFromPurchaseService(intoDepositoryList);
        // ??????????????????
        queryPurchasePackageInfoFor1688(intoDepositoryList, requiredQueryPurchaseInfo);
        List<LogisticsPackageBO> outDepoistoryList = querySaleOrderOutDepoistoryStatus(saleOrderList);
        // ?????????????????????
        Map<String, List<DepositorySaleOrderDetailBO>> saleOderMap = intoDepositoryList.stream().collect(Collectors.groupingBy(DepositorySaleOrderDetailBO::getSaleOrder));
        // ?????????????????????????????????
        HashMap<String, List<LogisticsPackageBO.LogisticsPackageItemBO>> outPackageMap = new HashMap<>();
        outDepoistoryList.stream().forEach(item -> {
            outPackageMap.putIfAbsent(item.getSalesOrderId(), new ArrayList<>());
            outPackageMap.computeIfPresent(item.getSalesOrderId(), (k, v) -> {
                v.addAll(item.getItems());
                return v;
            });
        });
        // ???????????????????????????itemid?????????
        List<DepositorySaleOrderDetailBO> finishReturn = new ArrayList();
        for (Map.Entry<String, List<DepositorySaleOrderDetailBO>> saleOrderEntry : saleOderMap.entrySet()) {
            long count = outPackageMap.entrySet().stream().filter(outPackageEntry -> outPackageEntry.getKey().equals(saleOrderEntry.getKey())).count();
            if (count > 0) {
                outPackageMap.entrySet().stream().forEach(outPackageEntry -> {
                    if (saleOrderEntry.getKey().equals(outPackageEntry.getKey())) {
                        List<DepositorySaleOrderDetailBO> detailBOS = fininshItemIdStatus(saleOrderEntry.getValue(), outPackageEntry.getValue());
                        finishReturn.addAll(detailBOS);
                    }
                });
            } else {
                // ?????????????????????
                saleOrderEntry.getValue().stream().forEach(item -> {
                    item.setDepositoryStatus(item.getStatus());
                    item.setDepositoryStatusStr(item.getStatusStr());
                });
                finishReturn.addAll(saleOrderEntry.getValue());
            }
        }
        // ??????ae?????????
        queryDirectSendTrackCode(finishReturn,outDepoistoryList);
        // ??????GRPC??????
        List<SaleOrderStatusQueryGTO.SaleOrderStatusQueryResponse> responses = GrpcJsonFormatUtils.javaToGrpc(finishReturn, SaleOrderStatusQueryGTO.SaleOrderStatusQueryResponse.class);
        return responses;
    }

    private List<DepositorySaleOrderDetailBO> queryItemOfTypeFromPurchaseService(List<DepositorySaleOrderDetailBO> intoDepositoryList) {
        List<String> itemIds = intoDepositoryList.stream().map(DepositorySaleOrderDetailBO::getItemId).collect(Collectors.toList());
        GetPurchaseIdInfoByItemIdRequest request = GetPurchaseIdInfoByItemIdRequest.newBuilder().addAllCommodityItemId(itemIds).build();
        log.info("??????GRPC?????? ??????itemId ????????????????????????????????????????????????{}",itemIds);
        PurchaseIdInfoByItemIdResult purchaseIdInfoByItemId = purchaseIdInfoSynchronizeServiceGrpcClient.getPurchaseIdInfoByItemId(request);
        log.info("??????GRPC?????? ??????itemId ?????????????????????{}",GrpcJsonFormatUtils.toJsonString(purchaseIdInfoByItemId));
        if (purchaseIdInfoByItemId.getSuccess() && !Objects.isNull(purchaseIdInfoByItemId.getDataList()) && !purchaseIdInfoByItemId.getDataList().isEmpty()) {
            List<PurchaseIdInfo> dataList = purchaseIdInfoByItemId.getDataList();
            Map<String, List<PurchaseIdInfo>> mapByItem = dataList.stream().collect(Collectors.groupingBy(PurchaseIdInfo::getCommodityItemId));
            PurchaseIdInfo purchaseIdInfo = PurchaseIdInfo.newBuilder().build();
            intoDepositoryList.stream().forEach(item ->{
                PurchaseIdInfo info = Optional.ofNullable(mapByItem.get(item.getItemId())).orElseGet(() -> Collections.singletonList(purchaseIdInfo)).get(0);
                item.setType(StringUtils.isNotBlank(info.getChannel()) ? Integer.valueOf(info.getChannel()) : ChannelEnum.CHANNEL_1688.getCode());
            });
        }
        return intoDepositoryList;
    }

    private List<DepositorySaleOrderDetailBO> queryDirectSendTrackCode(List<DepositorySaleOrderDetailBO> finishReturn, List<LogisticsPackageBO> outDepoistoryList) {
        Map<Integer, List<LogisticsPackageBO>> mapByType = outDepoistoryList.stream().collect(Collectors.groupingBy(LogisticsPackageBO::getType));
        for (DepositorySaleOrderDetailBO packageDetailBO : finishReturn) {
            if (packageDetailBO.getType().equals(ChannelEnum.CHANNEL_1688.getCode()) || packageDetailBO.getType().equals(ChannelEnum.CHANNEL_STOCK_UP.getCode())) {
                continue;
            }
            List<LogisticsPackageBO> logisticsPackageBOS = Optional.ofNullable(mapByType.get(packageDetailBO.getType())).orElseGet(() -> Collections.emptyList());
            List<DepositorySaleOrderDetailBO.LogisticsProperty> logisticsProperties = logisticsPackageBOS.stream()
                    .filter(itemForDirectSend -> itemForDirectSend.getSalesOrderId().equals(packageDetailBO.getSaleOrder()))
                    .map(itemForDirectSend -> {
                        //????????????
                        DepositorySaleOrderDetailBO.LogisticsProperty logisticsProperty = new DepositorySaleOrderDetailBO.LogisticsProperty();
                        logisticsProperty.setLogisticsWayBillNo(itemForDirectSend.getLogisticsWayBillNo());
                        logisticsProperty.setLogisticsName(itemForDirectSend.getLogisticsName());
                        logisticsProperty.setLogisticsCode(itemForDirectSend.getLogisticsCode());
                        logisticsProperty.setValid("??????");
                        logisticsProperty.setLogisticsTrackingCode(itemForDirectSend.getLogisticsTrackingCode());
                        logisticsProperty.setLogisticsReceived(Constant.YES);
                        logisticsProperty.setLogisticsNode(itemForDirectSend.getLogisticsNode());
                        return logisticsProperty;
            }).collect(Collectors.toList());
            packageDetailBO.setLogisticsProperties(logisticsProperties);
            if (!logisticsPackageBOS.isEmpty()) {
                packageDetailBO.setReceivingGoodTime(logisticsPackageBOS.get(0).getReceivingGoodTime());
            }
        }
        return finishReturn;
    }


    private List<DepositorySaleOrderDetailBO> fininshItemIdStatus(List<DepositorySaleOrderDetailBO> depositorySaleOrderList, List<LogisticsPackageBO.LogisticsPackageItemBO> logisticsPackageList) {
        // ???itemID ?????????????????????
        Collections.sort(logisticsPackageList, (o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()));
        // ??????
        Set<LogisticsPackageBO.LogisticsPackageItemBO> logisticsPackageSet = new HashSet<>();
        logisticsPackageList.stream().forEach(item -> logisticsPackageSet.add(item));
        // ??????itemId????????????
        depositorySaleOrderList.stream().forEach(item -> {
            item.setStatus(item.getReceiveState());
            item.setStatusStr(DepositoryPurchaseStatusEnum.getEnumByStatusCode(item.getReceiveState()).getStausName());
            // ??????????????????????????????
            item.setDepositoryStatus(item.getStatus());
            item.setDepositoryStatusStr(item.getStatusStr());
            logisticsPackageSet.stream().forEach(outItem -> {
                if (item.getItemId().compareTo(outItem.getItemId()) == 0) {
                    // ????????????
                    DepositorySaleOrderDetailBO.LogisticsProperty logsiticsProperty = new DepositorySaleOrderDetailBO.LogisticsProperty();
                    logsiticsProperty.setLogisticsWayBillNo(outItem.getLogisticsWayBillNo());
                    logsiticsProperty.setLogisticsName(outItem.getLogisticsName());
                    logsiticsProperty.setLogisticsCode(outItem.getLogisticsCode());
                    logsiticsProperty.setLogisticsOrderNo(outItem.getLogisticsOrderNo());
                    logsiticsProperty.setValid(Constant.YES == outItem.getIsValid() ? "??????" : "??????");
                    logsiticsProperty.setLogisticsTrackingCode(outItem.getLogisticsTrackingCode());
                    logsiticsProperty.setLogisticsReceived(outItem.getLogisticsReceived());
                    logsiticsProperty.setLogisticsNode(outItem.getLogisticsNode());

                    item.setStatus(Objects.isNull(outItem.getStatus()) ? item.getStatus() : outItem.getStatus());
                    item.setStatusStr(Objects.isNull(outItem.getStatusStr()) ? item.getStatusStr() : outItem.getStatusStr());
                    item.setType(outItem.getType());
                    item.setDeliveryTime(outItem.getDeliveryTime());
                    item.setReceivingGoodTime(outItem.getReceivingGoodTime());
                    item.setAcceptTime(outItem.getAcceptTime());
                    item.setLogisticsStatus(outItem.getLogisticsStatus());
                    item.setLogisticsStatusStr(DeliveryPackageLogisticsStatusEnum.getDeliveryPackageLogisticsStatusEnumTypeCode(outItem.getLogisticsStatus()).getMsg());
                    item.setDepositoryStatus(outItem.getDeliveryStatus());
                    item.setDepositoryStatusStr(DeliveryPackageDeliveryStatusEnum.getDeliveryPackageDeliveryStatusEnumByCode(outItem.getDeliveryStatus()).getName());
                    item.setLogisticsProperties(Collections.singletonList(logsiticsProperty));
                }
            });
        });
        return depositorySaleOrderList;
    }

    private List<DepositorySaleOrderDetailBO> querySaleOrderIntoDepoistoryStatus(List<String> saleOrderList) {
        LambdaQueryWrapper<DepositorySaleOrderDetailDO> queryWrapper = Wrappers.<DepositorySaleOrderDetailDO>lambdaQuery()
                .select(DepositorySaleOrderDetailDO::getId, DepositorySaleOrderDetailDO::getItemId,DepositorySaleOrderDetailDO::getPurchaseId,DepositorySaleOrderDetailDO::getReceivePurchaseId,
                        DepositorySaleOrderDetailDO::getSaleOrder, DepositorySaleOrderDetailDO::getPurchasePackageId,DepositorySaleOrderDetailDO::getWeight,
                        DepositorySaleOrderDetailDO::getReceiveState,DepositorySaleOrderDetailDO::getCreateTime, DepositorySaleOrderDetailDO::getReceiveTime)
                .in(DepositorySaleOrderDetailDO::getSaleOrder, saleOrderList);
        List<DepositorySaleOrderDetailDO> saleOrderDetailDOS = depositorySaleOrderDetailMapper.selectList(queryWrapper);
        // ???purchaseId??????
//        saleOrderDetailDOS.stream().forEach(item ->{
//            if (StringUtils.isNotBlank(item.getReceivePurchaseId())) {
//                item.setPurchaseId(item.getReceivePurchaseId());
//            }
//        });
        String jsonString = JSON.toJSONString(saleOrderDetailDOS);
        List<DepositorySaleOrderDetailBO> detailBOS = JSON.parseArray(jsonString, DepositorySaleOrderDetailBO.class);
        detailBOS.stream().forEach(item -> {
            item.setStatus(item.getReceiveState());
            item.setStatusStr(PurchasePackageWarehousingTypeEnum.getPurchasePackageWarehousingTypeEnumByType(item.getReceiveState()).getName());
        });
        return detailBOS;
    }

    private List<DepositorySaleOrderDetailBO> queryPurchasePackageInfoFor1688(List<DepositorySaleOrderDetailBO> detailBOS, boolean requiredQueryPurchaseInfo) {
        if (!requiredQueryPurchaseInfo) {
            return detailBOS;
        }
        // ???????????????1688???????????????
        List<DepositorySaleOrderDetailBO> detailHasPurchasePackages = detailBOS.stream().filter(item -> !Objects.isNull(item.getPurchasePackageId())).collect(Collectors.toList());
        if (detailHasPurchasePackages.isEmpty()) {
            return detailBOS;
        }
        List<Long> purchasePackageIds = detailHasPurchasePackages.stream().map(DepositorySaleOrderDetailBO::getPurchasePackageId).collect(Collectors.toList());
        LambdaQueryWrapper<LogisticsPurchasePackageDO> queryWrapper = Wrappers.<LogisticsPurchasePackageDO>lambdaQuery().in(LogisticsPurchasePackageDO::getId, purchasePackageIds);
        List<LogisticsPurchasePackageDO> logisticsPurchasePackageDOS = logisticsPurchasePackageMapper.selectList(queryWrapper);
        // ??????1688 ????????????
        List<String> billNoList = logisticsPurchasePackageDOS.stream().map(LogisticsPurchasePackageDO::getExpressBillNo).collect(Collectors.toList());
        LambdaQueryWrapper<LogisticsStatusDO> wrapperForLogisticStatus = Wrappers.<LogisticsStatusDO>lambdaQuery().in(LogisticsStatusDO::getMailNo, billNoList);
        List<LogisticsStatusDO> logisticsStatusDOS = logisticsStatusMapper.selectList(wrapperForLogisticStatus);
        // id ????????????list?????????1
        Map<Long, List<LogisticsPurchasePackageDO>> mapByPurchasePackageId = logisticsPurchasePackageDOS.stream().collect(Collectors.groupingBy(LogisticsPurchasePackageDO::getId));
        Map<String, List<LogisticsStatusDO>> mapByBillNo = logisticsStatusDOS.stream().collect(Collectors.groupingBy(LogisticsStatusDO::getMailNo));
        // ????????????????????????
        for (DepositorySaleOrderDetailBO detailBO : detailBOS) {
            LogisticsPurchasePackageDO packageDO = Optional.ofNullable(mapByPurchasePackageId.get(detailBO.getPurchasePackageId())).orElseGet(() -> Collections.singletonList(new LogisticsPurchasePackageDO())).get(0);
            LogisticsStatusDO logisticsStatus = Optional.ofNullable(mapByBillNo.get(packageDO.getExpressBillNo())).orElseGet(() -> Collections.singletonList(new LogisticsStatusDO())).get(0);
            DepositorySaleOrderDetailBO.LogisticsPropertyForPurchase logisticsPropertyForPurchase = new DepositorySaleOrderDetailBO.LogisticsPropertyForPurchase();
            logisticsPropertyForPurchase.setPackageNo(packageDO.getPackageNo());
            logisticsPropertyForPurchase.setChannelOrderId(packageDO.getChannelOrderId());
            logisticsPropertyForPurchase.setExpressCode(packageDO.getExpressCode());
            logisticsPropertyForPurchase.setExpressBillNo(packageDO.getExpressBillNo());
            logisticsPropertyForPurchase.setExpressCompanyCode(packageDO.getExpressCompanyCode());
            logisticsPropertyForPurchase.setExpressCompanyName(packageDO.getExpressCompanyName());
            logisticsPropertyForPurchase.setConsignTime(logisticsStatus.getConsignTime());
            logisticsPropertyForPurchase.setSignTime(logisticsStatus.getSignTime());
            logisticsPropertyForPurchase.setAcceptTime(logisticsStatus.getAcceptTime());
            logisticsPropertyForPurchase.setReceivingGoodTime(packageDO.getReceivingGoodTime());
            detailBO.setLogisticsPropertyForPurchases(Collections.singletonList(logisticsPropertyForPurchase));
        }
        return detailBOS;
    }

    private List<LogisticsPackageBO> querySaleOrderOutDepoistoryStatus(List<String> saleOrderList) {
        LambdaQueryWrapper<LogisticsPackageDO> queryWrapper = Wrappers.<LogisticsPackageDO>lambdaQuery()
//                .select(LogisticsPackageDO::getId, LogisticsPackageDO::getSalesOrderId, LogisticsPackageDO::getDeliveryStatus,
//                        LogisticsPackageDO::getLogisticsStatus, LogisticsPackageDO::getIsValid,
//                        LogisticsPackageDO::getDeliveryTime, LogisticsPackageDO::getCreateTime)
//                .eq(LogisticsPackageDO::getType, DeliveryPackageTypeEnum.ABROAD_DELIVERY_PACKAGE.getType())
                .eq(LogisticsPackageDO::getIsValid,Constant.YES)
                .in(LogisticsPackageDO::getSalesOrderId, saleOrderList);
        List<LogisticsPackageDO> logisticsPackageDOS = logisticsPackageMapper.selectList(queryWrapper);
        List<Long> packageList = logisticsPackageDOS.stream().map(LogisticsPackageDO::getId).collect(Collectors.toList());
        if (packageList.isEmpty()) {
            return Collections.emptyList();
        }
        List<LogisticsPackageItemDO> logisticsPackageItemDOs = querySaleOrderOutDepoistoryItem(packageList);
        // ????????????????????????
        String logisticsPackageStr = JSON.toJSONString(logisticsPackageDOS);
        List<LogisticsPackageBO> logisticsPackageBOS = JSON.parseArray(logisticsPackageStr, LogisticsPackageBO.class);
        String logisticsPackageItemStr = JSON.toJSONString(logisticsPackageItemDOs);
        List<LogisticsPackageBO.LogisticsPackageItemBO> logisticsPackageItemBOS = JSON.parseArray(logisticsPackageItemStr, LogisticsPackageBO.LogisticsPackageItemBO.class);
        logisticsPackageBOS.stream().forEach(packageBO -> {
            List<LogisticsPackageBO.LogisticsPackageItemBO> itemDOS = logisticsPackageItemBOS.stream().filter(item -> packageBO.getId().compareTo(item.getPackageId()) == 0).collect(Collectors.toList());
            packageBO.setItems(itemDOS);
            covertPackagePropertyToItemProperty(packageBO);
        });
        // ????????????item???????????????
        return coverStatusToItem(logisticsPackageBOS);
    }

    private void covertPackagePropertyToItemProperty(LogisticsPackageBO packageBO) {
        packageBO.getItems().stream().forEach(item ->{
            item.setDeliveryStatus(packageBO.getDeliveryStatus());
            item.setLogisticsStatus(packageBO.getLogisticsStatus());
            item.setLogisticsName(packageBO.getLogisticsName());
            item.setLogisticsCode(packageBO.getLogisticsCode());
            item.setLogisticsChannel(packageBO.getLogisticsChannel());
            item.setDeliveryTime(packageBO.getDeliveryTime());
            item.setReceivingGoodTime(packageBO.getReceivingGoodTime());
            item.setLogisticsNode(packageBO.getLogisticsNode());
            item.setLogisticsWayBillNo(packageBO.getLogisticsWayBillNo());
            item.setType(packageBO.getType());
            item.setPurchaseChannel(packageBO.getPurchaseChannel());
            item.setSalesOrderId(packageBO.getSalesOrderId());
            item.setIsValid(packageBO.getIsValid());
            item.setLogisticsTrackingCode(packageBO.getLogisticsTrackingCode());
            item.setLogisticsReceived(packageBO.getLogisticsReceived());
            item.setLogisticsOrderNo(packageBO.getLogisticsOrderNo());
            item.setAcceptTime(packageBO.getAcceptTime());
        });
    }

    private List<LogisticsPackageBO> coverStatusToItem(List<LogisticsPackageBO> logisticsPackageBOS) {
        // ???itemid ????????????
        for (LogisticsPackageBO packageBO : logisticsPackageBOS) {
            packageBO.getItems().stream().forEach(item -> {
                if (!Objects.isNull(packageBO.getDeliveryStatus()) && DeliveryPackageDeliveryStatusEnum.INIT.getCode().compareTo(packageBO.getDeliveryStatus()) != 0) {
                    item.setStatus(packageBO.getDeliveryStatus());
                    item.setStatusStr(DeliveryPackageDeliveryStatusEnum.getDeliveryPackageDeliveryStatusEnumByCode(packageBO.getDeliveryStatus()).getName());
                }
//                if (DeliveryPackageLogisticsStatusEnum.RECEIVED.getCode().equals(packageBO.getLogisticsStatus())) {
//                    item.setStatus(packageBO.getLogisticsStatus());
//                    item.setStatusStr(DeliveryPackageLogisticsStatusEnum.getDeliveryPackageLogisticsStatusEnumTypeCode(packageBO.getLogisticsStatus()).getMsg());
//                }
            });
        }
        return logisticsPackageBOS;
    }

    private List<LogisticsPackageItemDO> querySaleOrderOutDepoistoryItem(List<Long> packageList) {
        LambdaQueryWrapper<LogisticsPackageItemDO> queryWrapper = Wrappers.<LogisticsPackageItemDO>lambdaQuery()
                .select(LogisticsPackageItemDO::getId, LogisticsPackageItemDO::getItemId,
                        LogisticsPackageItemDO::getPurchaseId, LogisticsPackageItemDO::getPackageId, LogisticsPackageItemDO::getCreateTime)
                .in(LogisticsPackageItemDO::getPackageId, packageList);
        List<LogisticsPackageItemDO> logisticsPackageItemDOList = logisticsPackageItemMapper.selectList(queryWrapper);
        return logisticsPackageItemDOList;
    }

}

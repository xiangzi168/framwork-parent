package com.amg.fulfillment.cloud.logistics.grpc;

import com.alibaba.fastjson.JSON;
import com.amg.framework.boot.base.exception.GlobalException;
import com.amg.framework.cloud.grpc.utils.GrpcJsonFormatUtils;
import com.amg.fulfillment.cloud.logistics.api.dto.msg.DeliveryPackageMsgDto;
import com.amg.fulfillment.cloud.logistics.api.enumeration.DeliveryPackageLogisticsStatusEnum;
import com.amg.fulfillment.cloud.logistics.api.enumeration.DeliveryPackagePurchaseChannelEnum;
import com.amg.fulfillment.cloud.logistics.api.enumeration.DeliveryPackageTypeEnum;
import com.amg.fulfillment.cloud.logistics.api.grpc.ChannelOrderForDirectSendGrpc;
import com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO;
import com.amg.fulfillment.cloud.logistics.dto.logistic.DeliveryPackageDto;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsPackageDO;
import com.amg.fulfillment.cloud.logistics.enumeration.BaseLogisticsResponseCodeEnum;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsPackageMapper;
import com.amg.fulfillment.cloud.logistics.service.IDeliveryPackageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.protobuf.ProtocolStringList;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@GrpcService
public class ChannelOrderForDirectSend extends ChannelOrderForDirectSendGrpc.ChannelOrderForDirectSendImplBase{

    @Autowired
    private LogisticsPackageMapper logisticsPackageMapper;
    @Autowired
    private IDeliveryPackageService deliveryPackageService;


    @Override
    public List<ChannelOrderForDirectSendGTO.DeliveryPackageForDirectSendDetail> pushDeliveryPack(ChannelOrderForDirectSendGTO.DeliveryPackageForDirectSendRequest request, StreamObserver<ChannelOrderForDirectSendGTO.DeliveryPackageForDirectSendResult> responseObserver) {
        String requestStr = GrpcJsonFormatUtils.toJsonString(request);
        log.info("GRPC--????????????CJ?????????????????????????????????????????????JSON??????{}", requestStr);
        String requestList = GrpcJsonFormatUtils.toJsonString(request.getDeliveryPackageListList());
        int type = request.getType();
        List<DeliveryPackageMsgDto> deliveryPackageMsgDtoList = JSON.parseArray(requestList, DeliveryPackageMsgDto.class);
        List<String> searchWaybillNo = deliveryPackageMsgDtoList.stream().flatMap(item -> item.getLogisticsList().stream().map(logistic -> logistic.getLogisticsWaybillNo())).collect(Collectors.toList());
        LambdaQueryWrapper<LogisticsPackageDO> queryWrapper = Wrappers.<LogisticsPackageDO>lambdaQuery().in(LogisticsPackageDO::getLogisticsWayBillNo, searchWaybillNo);
        List<LogisticsPackageDO> logisticsPackageExisted = logisticsPackageMapper.selectList(queryWrapper);
        List<String> logisticsPackageExistedForWayBillNoList = logisticsPackageExisted.stream().map(LogisticsPackageDO::getLogisticsWayBillNo).collect(Collectors.toList());
        List<DeliveryPackageDto> deliveryPackageDtoList = new ArrayList<>(16);
        deliveryPackageMsgDtoList.forEach(deliveryPackageMsgDto -> {
            DeliveryPackageDto deliveryPackageDto = new DeliveryPackageDto();
            deliveryPackageDto.setType(type);
            // ???????????????????????????????????????????????????
            int channelType = DeliveryPackageTypeEnum.CJ_DELIVERY_PACKAGE.getType() == type ? DeliveryPackagePurchaseChannelEnum.CJ.getType() : DeliveryPackagePurchaseChannelEnum.AE.getType();
            deliveryPackageDto.setPurchaseChannel(channelType);
            List<DeliveryPackageDto.DeliveryPackageItemDto> deliveryPackageItemDtoList = new ArrayList<>();
            List<DeliveryPackageMsgDto.DeliveryPackagelogisticsDto> deliveryPackagelogisticsDtoList = deliveryPackageMsgDto.getLogisticsList();
            // ??????????????????????????????
            deliveryPackagelogisticsDtoList.stream().filter(deliveryPackagelogisticsDto -> !logisticsPackageExistedForWayBillNoList.contains(deliveryPackagelogisticsDto.getLogisticsWaybillNo())).forEach(deliveryPackagelogisticsDto -> {
                DeliveryPackageDto.DeliveryPackageItemDto deliveryPackageItemDto = new DeliveryPackageDto.DeliveryPackageItemDto();
                deliveryPackageItemDto.setSalesOrderId(deliveryPackageMsgDto.getSalesOrderId());
                deliveryPackageItemDto.setChannelOrderId(deliveryPackageMsgDto.getChannelOrderId());
                deliveryPackageItemDto.setLogisticsArea(deliveryPackageMsgDto.getLogisticsArea());
                deliveryPackageItemDto.setLogisticsCode(StringUtils.isNotBlank(deliveryPackagelogisticsDto.getLogisticsCode()) ? deliveryPackagelogisticsDto.getLogisticsChannel() : deliveryPackagelogisticsDto.getLogisticsName());
                deliveryPackageItemDto.setLogisticsName(deliveryPackagelogisticsDto.getLogisticsName());
                deliveryPackageItemDto.setLogisticsWaybillNo(StringUtils.isNotBlank(deliveryPackagelogisticsDto.getLogisticsWaybillNo()) ? deliveryPackagelogisticsDto.getLogisticsWaybillNo() : deliveryPackagelogisticsDto.getLogisticsTrackingCode());
                deliveryPackageItemDto.setLogisticsTrackingCode(deliveryPackagelogisticsDto.getLogisticsTrackingCode());
                deliveryPackageItemDto.setLogisticsChannel(StringUtils.isNotBlank(deliveryPackagelogisticsDto.getLogisticsChannel()) ? deliveryPackagelogisticsDto.getLogisticsChannel() : deliveryPackagelogisticsDto.getLogisticsName());
                deliveryPackageItemDto.setLogisticsChannelStatus(deliveryPackagelogisticsDto.getLogisticsChannelStatus());
                deliveryPackageItemDtoList.add(deliveryPackageItemDto);
            });
            deliveryPackageDto.setList(deliveryPackageItemDtoList);
            deliveryPackageDtoList.add(deliveryPackageDto);
        });
        HashMap<DeliveryPackageDto,Boolean> deliveryPackageMap = new HashMap<DeliveryPackageDto,Boolean>();
        for (DeliveryPackageDto deliveryPackageDto : deliveryPackageDtoList) {
            Boolean aBoolean = false;
            try {
                aBoolean = deliveryPackageService.insertDirectSendPackage(deliveryPackageDto);
                deliveryPackageMap.put(deliveryPackageDto,aBoolean);
            } catch (Exception e) {
               log.error("cj????????????????????????????????????{}", ExceptionUtils.getMessage(e));
                deliveryPackageMap.put(deliveryPackageDto,aBoolean);
            }
        }
        // ?????????????????????
        List<ChannelOrderForDirectSendGTO.DeliveryPackageForDirectSendDetail> result = deliveryPackageMap.entrySet().stream()
                .flatMap(item -> {
                    Stream<ChannelOrderForDirectSendGTO.DeliveryPackageForDirectSendDetail> detailStream = item.getKey().getList().stream().map(logistic -> {
                        return ChannelOrderForDirectSendGTO.DeliveryPackageForDirectSendDetail.newBuilder()
                                .setSalesOrderId(logistic.getSalesOrderId())
                                .setLogisticsTrackingCode(logistic.getLogisticsTrackingCode())
                                .setLogisticsWaybillNo(logistic.getLogisticsWaybillNo())
                                .setSuccess(item.getValue())
                                .build();
                    });
                    return detailStream;
                }).collect(Collectors.toList());
        // ??????????????????????????????
        logisticsPackageExisted.stream().forEach(item ->{
            ChannelOrderForDirectSendGTO.DeliveryPackageForDirectSendDetail existedDetail = ChannelOrderForDirectSendGTO.DeliveryPackageForDirectSendDetail.newBuilder()
                    .setSalesOrderId(item.getSalesOrderId())
                    .setLogisticsTrackingCode(item.getLogisticsTrackingCode())
                    .setLogisticsWaybillNo(item.getLogisticsWayBillNo())
                    .setSuccess(true)
                    .build();
            result.add(existedDetail);
        });
        return result;
    }

    @Override
    public List<ChannelOrderForDirectSendGTO.ChannelOrderQueryForDirectSendDetail> queryChannelOrderForDirectSend(ChannelOrderForDirectSendGTO.ChannelOrderQueryForDirectSendRequest request, StreamObserver<ChannelOrderForDirectSendGTO.ChannelOrderQueryForDirectSendResult> responseObserver) {
        String requestStr = GrpcJsonFormatUtils.toJsonString(request);
        log.info("GRPC--??????????????????AE?????????????????????????????????????????????JSON??????{}", requestStr);
        ProtocolStringList channelOrdersList = request.getChannelOrdersList();
        if (channelOrdersList.isEmpty()) {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100,"????????????????????????null");
        }
        int type = request.getType();
        LambdaQueryWrapper<LogisticsPackageDO> queryWrapper = Wrappers.<LogisticsPackageDO>lambdaQuery()
                .eq(LogisticsPackageDO::getType,type)
                .in(LogisticsPackageDO::getChannelOrderId, channelOrdersList);
        List<LogisticsPackageDO> logisticsPackageDOS = logisticsPackageMapper.selectList(queryWrapper);
        Map<String, List<LogisticsPackageDO>> mapByChannelOrderId = logisticsPackageDOS.stream().collect(Collectors.groupingBy(LogisticsPackageDO::getChannelOrderId));
        // ????????????
        List<ChannelOrderForDirectSendGTO.ChannelOrderQueryForDirectSendDetail> details = mapByChannelOrderId.entrySet().stream()
                .map(entry -> {
                    // ??????????????????
                    List<ChannelOrderForDirectSendGTO.ChannelOrderQueryForDirectSendLogisticsInfo> logistics = entry.getValue().stream().map(logistic -> {
                        return ChannelOrderForDirectSendGTO.ChannelOrderQueryForDirectSendLogisticsInfo.newBuilder()
                                .setSaleOrder(Optional.ofNullable(logistic.getSalesOrderId()).orElseGet(() -> ""))
                                .setLogisticsName(Optional.ofNullable(logistic.getLogisticsName()).orElseGet(() -> ""))
                                .setLogisticsCode(Optional.ofNullable(logistic.getLogisticsCode()).orElseGet(() -> ""))
                                .setLogisticsWayBillNo(Optional.ofNullable(logistic.getLogisticsWayBillNo()).orElseGet(() -> ""))
                                .setLogisticsTrackingCode(Optional.ofNullable(logistic.getLogisticsTrackingCode()).orElseGet(() -> ""))
                                .setLogisticsOrderNo(Optional.ofNullable(logistic.getLogisticsOrderNo()).orElseGet(() -> ""))
                                .setLogisticsStatus(DeliveryPackageLogisticsStatusEnum.getDeliveryPackageLogisticsStatusEnumTypeCode(logistic.getLogisticsStatus()).getMsg())
                                .setLogisticsChannel(Optional.ofNullable(logistic.getLogisticsChannel()).orElseGet(() -> ""))
                                .setLogisticsNodeCn(Optional.ofNullable(logistic.getLogisticsNode()).orElseGet(() -> ""))
                                .setLogisticsNodeEn(Optional.ofNullable(logistic.getLogisticsNodeEn()).orElseGet(() -> ""))
                                .setReceivingGoodTime(logistic.getReceivingGoodTime() != null ? DateFormatUtils.format(logistic.getReceivingGoodTime(), "yyyy-MM-dd HH:mm:ss") : "")
                                .build();
                    }).collect(Collectors.toList());
                    // ??????????????????
                    ChannelOrderForDirectSendGTO.ChannelOrderQueryForDirectSendDetail detail = ChannelOrderForDirectSendGTO.ChannelOrderQueryForDirectSendDetail.newBuilder()
                            .setChannelOrder(entry.getKey())
                            .addAllChannelOrderForDirectSendLogisticsInfos(logistics)
                            .build();
                    return detail;
                }).collect(Collectors.toList());
        return details;
    }
}

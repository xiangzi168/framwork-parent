package com.amg.fulfillment.cloud.logistics.grpc;

import com.alibaba.fastjson.JSON;
import com.amg.framework.cloud.grpc.utils.GrpcJsonFormatUtils;
import com.amg.fulfillment.cloud.logistics.api.dto.msg.DeliveryPackageMsgDto;
import com.amg.fulfillment.cloud.logistics.api.grpc.DeliveryPackageGrpc;
import com.amg.fulfillment.cloud.logistics.api.proto.DeliveryPackageGTO;
import com.amg.fulfillment.cloud.logistics.dto.logistic.DeliveryPackageDto;
import com.amg.fulfillment.cloud.logistics.service.IDeliveryPackageService;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@GrpcService
public class DeliveryPackageService extends DeliveryPackageGrpc.DeliveryPackageImplBase {

    @Autowired
    private IDeliveryPackageService deliveryPackageService;

    @Override
    public DeliveryPackageGTO.DeliveryPackageDetail pushAeLogisticsOrder(DeliveryPackageGTO.DeliveryPackageRequest request, StreamObserver<DeliveryPackageGTO.DeliveryPackageResponseResult> responseObserver) {
        String requestStr = GrpcJsonFormatUtils.toJsonString(request);
        log.info("GRPC--远程调用AE发货包裹单物流接口请求参数转成JSON是：{}", requestStr);
        List<DeliveryPackageDto.DeliveryPackageItemDto> deliveryPackageItemDtoList = new ArrayList<>();
        String requestList = GrpcJsonFormatUtils.toJsonString(request.getDeliveryPackageListList());
        List<DeliveryPackageMsgDto> deliveryPackageMsgDtoList = JSON.parseArray(requestList, DeliveryPackageMsgDto.class);
        deliveryPackageMsgDtoList.forEach(deliveryPackageMsgDto -> {
            List<DeliveryPackageMsgDto.DeliveryPackagelogisticsDto> deliveryPackagelogisticsDtoList = deliveryPackageMsgDto.getLogisticsList();
            deliveryPackagelogisticsDtoList.forEach(deliveryPackagelogisticsDto -> {
                DeliveryPackageDto.DeliveryPackageItemDto deliveryPackageItemDto = new DeliveryPackageDto.DeliveryPackageItemDto();
                BeanUtils.copyProperties(deliveryPackageMsgDto, deliveryPackageItemDto);
                deliveryPackageItemDto.setLogisticsCode(deliveryPackagelogisticsDto.getLogisticsCode());
                deliveryPackageItemDto.setLogisticsTrackingCode(deliveryPackagelogisticsDto.getLogisticsTrackingCode());
                deliveryPackageItemDto.setLogisticsChannelStatus(deliveryPackagelogisticsDto.getLogisticsStatus());
                deliveryPackageItemDtoList.add(deliveryPackageItemDto);
            });
        });
        DeliveryPackageDto deliveryPackageDto = new DeliveryPackageDto();
        deliveryPackageDto.setList(deliveryPackageItemDtoList);
        deliveryPackageService.updateAeDeliveryPackage(deliveryPackageDto);
        DeliveryPackageGTO.DeliveryPackageDetail detail = DeliveryPackageGTO.DeliveryPackageDetail.newBuilder().build();
        return detail;
    }
}

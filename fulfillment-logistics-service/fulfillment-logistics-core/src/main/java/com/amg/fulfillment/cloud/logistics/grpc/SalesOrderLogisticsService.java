package com.amg.fulfillment.cloud.logistics.grpc;

import com.alibaba.fastjson.JSON;
import com.amg.framework.cloud.grpc.utils.GrpcJsonFormatUtils;
import com.amg.fulfillment.cloud.logistics.api.grpc.SalesOrderLogisticsGrpc;
import com.amg.fulfillment.cloud.logistics.api.proto.SalesOrderLogisticsGTO;
import com.amg.fulfillment.cloud.logistics.dto.logistic.SalesOrderLogisticsDto;
import com.amg.fulfillment.cloud.logistics.service.IDeliveryPackageService;
import com.google.protobuf.ProtocolStringList;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.stream.Collectors;
/**
 * Created by Seraph on 2021/5/22
 */

@Slf4j
@GrpcService
public class SalesOrderLogisticsService extends SalesOrderLogisticsGrpc.SalesOrderLogisticsImplBase {

    @Autowired
    private IDeliveryPackageService deliveryPackageService;

    @Override
    public List<SalesOrderLogisticsGTO.SalesOrderLogisticsResponse> getSalesOrderLogistics(SalesOrderLogisticsGTO.SalesOrderLogisticsRequest request,
                                                                                           StreamObserver<SalesOrderLogisticsGTO.SalesOrderLogisticsResponseResult> responseObserver) {

        String requestStr = GrpcJsonFormatUtils.toJsonString(request);
        log.info("GRPC--远程调用 用户销售订单采购信息根据销售订单 id 查询物流信息 接口请求参数转成JSON是：{}", requestStr);

        SalesOrderLogisticsDto salesOrderLogisticsDto = JSON.parseObject(requestStr, SalesOrderLogisticsDto.class);

        List<SalesOrderLogisticsGTO.SalesOrderLogisticsResponse> salesOrderLogisticsResponseList = deliveryPackageService.getSalesOrderLogistics(salesOrderLogisticsDto);
        return salesOrderLogisticsResponseList;
    }

    @Override
    public List<SalesOrderLogisticsGTO.SalesOrderLogisticsListResponse> getSalesOrderLogisticsList(SalesOrderLogisticsGTO.SalesOrderLogisticsRequest request,
                                                                                                   StreamObserver<SalesOrderLogisticsGTO.SalesOrderLogisticsListResponseResult> responseObserver) {
        String requestStr = GrpcJsonFormatUtils.toJsonString(request);
        log.info("GRPC--远程调用 用户销售订单列表根据销售订单 id 查询物流信息 接口请求参数转成JSON是：{}", requestStr);

        SalesOrderLogisticsDto salesOrderLogisticsDto = JSON.parseObject(requestStr, SalesOrderLogisticsDto.class);

        List<SalesOrderLogisticsGTO.SalesOrderLogisticsListResponse> salesOrderLogisticsListResponseList = deliveryPackageService.getSalesOrderLogisticsList(salesOrderLogisticsDto);
        return salesOrderLogisticsListResponseList;
    }

    @Override
    public List<String> getSalesOrderIdByPackageId(SalesOrderLogisticsGTO.SalesOrderPackageIdRequest request,
                                                   StreamObserver<SalesOrderLogisticsGTO.SalesOrderIdResponseResult> responseObserver) {
        String requestStr = GrpcJsonFormatUtils.toJsonString(request);
        log.info("GRPC--远程调用 用户销售订单列表根据 物流单号查询销售订单号列表 接口请求参数转成JSON是：{}", requestStr);

        ProtocolStringList protocolStringList = request.getPackageIdListList();
        List<String> packageIdList = protocolStringList.stream().collect(Collectors.toList());
        List<String> salesOrderIdList = deliveryPackageService.getSalesOrderIdByPackageId(packageIdList);
        return salesOrderIdList;
    }
}

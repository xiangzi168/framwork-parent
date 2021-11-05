package com.amg.fulfillment.cloud.logistics.grpc;

import com.alibaba.fastjson.JSON;
import com.amg.framework.cloud.grpc.utils.GrpcJsonFormatUtils;
import com.amg.fulfillment.cloud.logistics.api.grpc.SaleOrderAssignmentPurchaseIdGrpc;
import com.amg.fulfillment.cloud.logistics.api.proto.SaleOrderAssignmentPurchaseIdGTO;
import com.amg.fulfillment.cloud.logistics.service.IDepositorySaleOrderService;
import io.grpc.stub.StreamObserver;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
public class SaleOrderAssignmentPurchaseIdService extends SaleOrderAssignmentPurchaseIdGrpc.SaleOrderAssignmentPurchaseIdImplBase {

    @Autowired
    private IDepositorySaleOrderService depositorySaleOrderService;

    @Override
    public List<SaleOrderAssignmentPurchaseIdGTO.SaleOrderPurchaseIdResponse> assignmentPurchaseId(SaleOrderAssignmentPurchaseIdGTO.SaleOrderAssignmentPurchaseIdRequest request, StreamObserver<SaleOrderAssignmentPurchaseIdGTO.SaleOrderAssignmentPurchaseIdResult> responseObserver) {
        String requestStr = GrpcJsonFormatUtils.toJsonString(request);
        log.info("GRPC--远程调用推送销售订单分派purchaseId的接口请求参数转成JSON是：{}", requestStr);
        List<SaleOrderAssignmentPurchaseIdGTO.SaleOrder> saleOrderList = request.getSaleOrderList();
        String arrayStr = GrpcJsonFormatUtils.toJsonString(saleOrderList);
        log.info("GRPC--远程调用推送销售订单分派purchaseId的接口请求参数转成JSON数组对象是：{}", arrayStr);
        List<SaleOrderRequest> saleOrderRequestList = JSON.parseArray(arrayStr, SaleOrderRequest.class);
        List<SaleOrderResponse> reponseList = depositorySaleOrderService.assignmentPurchaseId(saleOrderRequestList);
        List<SaleOrderAssignmentPurchaseIdGTO.SaleOrderPurchaseIdResponse> detail = reponseList.stream().map(item -> {
            return SaleOrderAssignmentPurchaseIdGTO.SaleOrderPurchaseIdResponse.newBuilder()
                    .setSaleOrderId(item.getSaleOrderId())
                    .setItemId(item.getItemId())
                    .setPurchaseId(item.getPurchaseId())
                    .setSuccess(item.isSuccess())
                    .build();
        }).collect(Collectors.toList());
        return detail;
    }

    @Data
    public static class SaleOrderRequest{
        private String saleOrderId;
        private String itemId;
        private String purchaseId;
    }
    @Data
    public static class SaleOrderResponse{
        private String saleOrderId;
        private String itemId;
        private String purchaseId;
        private boolean success;
    }
}

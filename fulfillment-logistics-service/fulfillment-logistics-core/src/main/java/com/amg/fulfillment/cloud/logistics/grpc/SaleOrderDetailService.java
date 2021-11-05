package com.amg.fulfillment.cloud.logistics.grpc;

import com.alibaba.fastjson.JSON;
import com.amg.framework.cloud.grpc.utils.GrpcJsonFormatUtils;
import com.amg.fulfillment.cloud.logistics.api.grpc.SaleOrderDetailGrpc;
import com.amg.fulfillment.cloud.logistics.api.proto.SaleOrderDetailGTO;
import com.amg.fulfillment.cloud.logistics.model.req.DepositorySaleOrderReq;
import com.amg.fulfillment.cloud.logistics.model.req.PredictionSaleReq;
import com.amg.fulfillment.cloud.logistics.service.IDepositorySaleOrderService;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Collections;

@Slf4j
@GrpcService
public class SaleOrderDetailService extends SaleOrderDetailGrpc.SaleOrderDetailImplBase {

    @Autowired
    private IDepositorySaleOrderService depositorySaleOrderService;

    @Override
    public SaleOrderDetailGTO.SaleOrderResponseDetail addSaleOrder(SaleOrderDetailGTO.SaleOrderDetailRequest request, StreamObserver<SaleOrderDetailGTO.SaleOrderResponseResult> responseObserver) {
        String requestStr = GrpcJsonFormatUtils.toJsonString(request);
        log.info("GRPC--远程调用推送销售订单的接口请求参数转成JSON是：{}", requestStr);
        DepositorySaleOrderReq depositorySaleOrderReq = JSON.parseObject(requestStr, DepositorySaleOrderReq.class);
        Long id = depositorySaleOrderService.addSale(depositorySaleOrderReq);
        PredictionSaleReq predictionSaleReq = new PredictionSaleReq();
        predictionSaleReq.setIds(Collections.singletonList(id));
        try {
            depositorySaleOrderService.prediction(predictionSaleReq);
        } catch (Exception e) {
            log.error("GRPC--远程调用预报销售订单到仓库请求发生错误：{}", e);
        }
        SaleOrderDetailGTO.SaleOrderResponseDetail detail = SaleOrderDetailGTO.SaleOrderResponseDetail.newBuilder()
                .setSaleOrderId(depositorySaleOrderReq.getSaleOrderId())
                .build();
        return detail;
    }
}

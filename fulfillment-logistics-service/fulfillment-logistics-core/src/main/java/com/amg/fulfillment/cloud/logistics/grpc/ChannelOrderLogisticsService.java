package com.amg.fulfillment.cloud.logistics.grpc;

import com.alibaba.fastjson.JSON;
import com.amg.framework.boot.base.exception.GlobalException;
import com.amg.framework.cloud.grpc.utils.GrpcJsonFormatUtils;
import com.amg.fulfillment.cloud.logistics.api.enumeration.PurchasePackageAddTypeEnum;
import com.amg.fulfillment.cloud.logistics.api.grpc.ChannelOrderLogisticsGrpc;
import com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderLogisticsGTO;
import com.amg.fulfillment.cloud.logistics.dto.logistic.ChannelOrderLogisticsDto;
import com.amg.fulfillment.cloud.logistics.model.req.ManualPredictionReq;
import com.amg.fulfillment.cloud.logistics.service.IDeliveryPackageService;
import com.amg.fulfillment.cloud.logistics.service.impl.PurchasePackageServiceImpl;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Seraph on 2021/5/22
 */

@Slf4j
@GrpcService
public class ChannelOrderLogisticsService extends ChannelOrderLogisticsGrpc.ChannelOrderLogisticsImplBase {

    @Autowired
    private IDeliveryPackageService deliveryPackageService;
    @Autowired
    private PurchasePackageServiceImpl purchasePackageServiceImpl;
    private static final ThreadLocal<Integer> CHANNELORDERLOCAL = new ThreadLocal<>();



    @Override
    public List<ChannelOrderLogisticsGTO.ChannelOrderLogisticsResponse> getChannelOrderLogistics(ChannelOrderLogisticsGTO.ChannelOrderLogisticsRequest request,
                                                                                                 StreamObserver<ChannelOrderLogisticsGTO.ChannelOrderLogisticsResponseResult> responseObserver) {

        String requestStr = GrpcJsonFormatUtils.toJsonString(request);
        log.info("GRPC--远程调用 渠道订单列表根据渠道订单 id 查询物流信息 接口请求参数转成JSON是：{}", requestStr);
        //------调用物流
        ChannelOrderLogisticsDto channelLogisticsDto = JSON.parseObject(requestStr, ChannelOrderLogisticsDto.class);
        List<ChannelOrderLogisticsGTO.ChannelOrderLogisticsResponse> channelOrderLogisticsResponseList = deliveryPackageService.getChannelLogistics(channelLogisticsDto);
        return channelOrderLogisticsResponseList;
    }

    @Override
    public List<String> getChannerOrderIdByPackageId(ChannelOrderLogisticsGTO.ChannelOrderPackageIdRequest request,
                                                     StreamObserver<ChannelOrderLogisticsGTO.ChannerOrderIdResponseResult> responseObserver) {

        String requestStr = GrpcJsonFormatUtils.toJsonString(request);
        List  packageIdList = JSON.parseObject(requestStr, List.class);

        List<String> channelOrderIdList = deliveryPackageService.getChannerOrderIdByPackageId(packageIdList);
        return channelOrderIdList;
    }
    @Override
    public Boolean updateChannelOrderId(ChannelOrderLogisticsGTO.updateChannelOrderIdRequest request,
                                        StreamObserver<ChannelOrderLogisticsGTO.updateChannelOrderIdResult> responseObserver){
        if (request!=null){
            String newChannelOrderId = request.getNewChannelOrderId();
            String oldChannelOrderId = request.getOldChannelOrderId();
            log.info("修改渠道订单号请求进入old:{},new:{}",oldChannelOrderId,newChannelOrderId);
            Boolean aBoolean = purchasePackageServiceImpl.updateChannelOrderId(oldChannelOrderId, newChannelOrderId);
            return aBoolean;
        }else{
          throw new GlobalException("","请求参数为空");
        }
    }

    @Override
    public List<ChannelOrderLogisticsGTO.AddPurchaseOrderResponse> addPurchaseOrder(ChannelOrderLogisticsGTO.AddPurchaseOrderRequest request, StreamObserver<ChannelOrderLogisticsGTO.AddPurchaseOrderResult> responseObserver) {
        String requestStr = GrpcJsonFormatUtils.toJsonString(request);
        ManualPredictionReq manualPredictionReq = JSON.parseObject(requestStr, ManualPredictionReq.class);
        manualPredictionReq.getPredictionDetails().stream().forEach(item ->{
            item.setProductAttribute(JSON.toJSONString(Collections.singletonList(item.getProductAttribute())));
        });
        List<ChannelOrderLogisticsGTO.AddPurchaseOrderResponse> responses = new ArrayList<>();
        log.info("GRPC--远程调用 增加采购订单(鲸吞) 接口请求参数转成JSON是：{}", requestStr);
        String expressBillNo = manualPredictionReq.getExpressBillNo();
        try {
            CHANNELORDERLOCAL.set(PurchasePackageAddTypeEnum.SYSTEM_ADD.getType());
            Boolean aBoolean = purchasePackageServiceImpl.manualPredictionPurchaseId(manualPredictionReq);
            log.info("GRPC--远程调用 增加采购订单(鲸吞) 返回是否成功：{}", aBoolean);
            manualPredictionReq.getPredictionDetails().stream().forEach(item -> {
                ChannelOrderLogisticsGTO.AddPurchaseOrderResponse response = ChannelOrderLogisticsGTO.AddPurchaseOrderResponse.newBuilder()
                        .setExpressBillNo(expressBillNo)
                        .setSalesOrderId(item.getSalesOrderId())
                        .setPurchaseId(item.getPurchaseId())
                        .setItemId(item.getItemId())
                        .setSuccess(true)
                        .build();
                responses.add(response);
            });
        }catch (Exception e){
            log.info("GRPC--远程调用 增加采购订单(鲸吞) 发生异常：{}", e);
            manualPredictionReq.getPredictionDetails().stream().forEach(item -> {
                ChannelOrderLogisticsGTO.AddPurchaseOrderResponse response = ChannelOrderLogisticsGTO.AddPurchaseOrderResponse.newBuilder()
                        .setExpressBillNo(expressBillNo)
                        .setSalesOrderId(item.getSalesOrderId())
                        .setPurchaseId(item.getPurchaseId())
                        .setItemId(item.getItemId())
                        .setSuccess(false)
                        .build();
                responses.add(response);
            });
        }finally {
            CHANNELORDERLOCAL.remove();
        }
        return responses;
    }

    public static Integer getAddType() {
        return CHANNELORDERLOCAL.get();
    }

    @Override
    public List<ChannelOrderLogisticsGTO.UpdatePurchasePackageResponse> updatePurchasePackage(ChannelOrderLogisticsGTO.UpdatePurchasePackageRequest request, StreamObserver<ChannelOrderLogisticsGTO.UpdatePurchasePackageResult> responseObserver) {
        String requestStr = GrpcJsonFormatUtils.toJsonString(request);
        log.info("GRPC--远程调用 根据purchaseId更新采购包裹请求参数转成JSON是：{}", requestStr);
        List<ChannelOrderLogisticsGTO.UpdatePurchasePackageResponse> responses = new ArrayList();
        request.getPurchaseIdList().forEach(purchaseId -> {
            boolean isUpdate = purchasePackageServiceImpl.updatePurchasePackageByPurchaseId(purchaseId);
            ChannelOrderLogisticsGTO.UpdatePurchasePackageResponse response = ChannelOrderLogisticsGTO.UpdatePurchasePackageResponse.newBuilder().setPurchaseId(purchaseId).setSuccess(isUpdate).build();
            responses.add(response);
        });
        return responses;
    }

}

package com.amg.fulfillment.cloud.logistics.grpc;

import com.alibaba.fastjson.JSON;
import com.amg.framework.boot.base.enums.ResponseCodeEnum;
import com.amg.framework.boot.base.exception.GlobalException;
import com.amg.framework.cloud.grpc.utils.GrpcJsonFormatUtils;
import com.amg.fulfillment.cloud.logistics.api.grpc.ManualOutDepositoryGrpc;
import com.amg.fulfillment.cloud.logistics.api.proto.ManualOutDepositoryGTO;
import com.amg.fulfillment.cloud.logistics.dto.logistic.LogisticDispatchResponseDto;
import com.amg.fulfillment.cloud.logistics.dto.logistic.LogisticOrderDto;
import com.amg.fulfillment.cloud.logistics.service.IDeliveryProductService;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Collections;
import java.util.List;
/**
 * @author Tom
 * @date 2021-04-22-17:36
 */
@Slf4j
@GrpcService
public class ManualOutDepositoryService extends ManualOutDepositoryGrpc.ManualOutDepositoryImplBase {

    @Autowired
    private IDeliveryProductService deliveryProductService;

    @Override
    public ManualOutDepositoryGTO.ManualOutDepositoryResponseDetail addOutDepository(ManualOutDepositoryGTO.ManualOutDepositoryRequest request,
                                                                                     StreamObserver<ManualOutDepositoryGTO.ManualOutDepositoryResponseResult> responseObserver) {

        String requestStr = GrpcJsonFormatUtils.toJsonString(request);
        log.info("GRPC--远程调用手动推单出库的接口请求参数转成JSON是：{}", requestStr);
        //------调用物流
        LogisticOrderDto logisticOrderDto = JSON.parseObject(requestStr, LogisticOrderDto.class);
        List<LogisticDispatchResponseDto> logisticDispatchResponseDtoList = deliveryProductService.manualOutDepository(Collections.singletonList(logisticOrderDto));
        if(logisticDispatchResponseDtoList.size() == 0)
        {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "远程调用手动推单出库接口异常");
        }

        LogisticDispatchResponseDto logisticDispatchResponseDto = logisticDispatchResponseDtoList.get(0);

        //返回成功的数据
        ManualOutDepositoryGTO.ManualOutDepositoryResponseDetail manualOutDepositoryResponseDetail = ManualOutDepositoryGTO.ManualOutDepositoryResponseDetail.newBuilder()
                .setStatusCode(logisticDispatchResponseDto.getStatusCode())
                .setErrorMsg(StringUtils.isBlank(logisticDispatchResponseDto.getErrorMsg()) ? "" : logisticDispatchResponseDto.getErrorMsg())
                .setDispatchOrderId(StringUtils.isBlank(logisticDispatchResponseDto.getDispatchOrderId()) ? "" : logisticDispatchResponseDto.getDispatchOrderId())
                .setLogisticOrderId(StringUtils.isBlank(logisticDispatchResponseDto.getLogisticsOrderNo()) ? "" : logisticDispatchResponseDto.getLogisticsOrderNo())
                .setLogisticTrackingNumber(StringUtils.isBlank(logisticDispatchResponseDto.getTrackingNumber()) ? "" : logisticDispatchResponseDto.getTrackingNumber())
                .setWaybillNo(StringUtils.isBlank(logisticDispatchResponseDto.getWaybillNo()) ? "" : logisticDispatchResponseDto.getWaybillNo())
                .build();
        return manualOutDepositoryResponseDetail;
    }

}

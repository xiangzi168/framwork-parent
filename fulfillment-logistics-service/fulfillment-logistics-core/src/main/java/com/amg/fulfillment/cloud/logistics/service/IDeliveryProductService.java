package com.amg.fulfillment.cloud.logistics.service;

import com.amg.fulfillment.cloud.logistics.api.dto.msg.DepositoryProcessMsgDto;
import com.amg.fulfillment.cloud.logistics.dto.logistic.LogisticDispatchResponseDto;
import com.amg.fulfillment.cloud.logistics.dto.logistic.LogisticOrderDto;
import com.amg.fulfillment.cloud.logistics.dto.logistic.LogisticOrderResponseDto;

import java.util.List;

/**
 * Created by Seraph on 2021/5/20
 */
public interface IDeliveryProductService {

    void autoOutDepository(List<LogisticOrderDto> logisticOrderDtoList);

    List<LogisticDispatchResponseDto> manualOutDepository(List<LogisticOrderDto> logisticOrderDtoList);

    void initLogisticsPackage(List<LogisticOrderDto> logisticOrderDtoList);

    void depositoryDeliveryResult(DepositoryProcessMsgDto depositoryProcessMsgDto);

    List<LogisticDispatchResponseDto> voucherPrepared(List<LogisticOrderDto> logisticOrderDtoList);
}

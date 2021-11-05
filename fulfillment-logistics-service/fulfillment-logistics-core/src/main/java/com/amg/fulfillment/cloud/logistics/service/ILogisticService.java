package com.amg.fulfillment.cloud.logistics.service;

import com.amg.fulfillment.cloud.logistics.dto.logistic.*;

public interface ILogisticService {

    /**
     * 查询物流轨迹接口
     * @param logisticOrderSearchDto
     * @return
     */
    LogisticTrackResponseDto getLogisticTrack(LogisticOrderSearchDto logisticOrderSearchDto);

    LogisticOrderResponseDto addLogisticOrder(LogisticOrderDto logisticOrderDto);

    LogisticPrintLabelResponseDto getLogisticLabel(LogisticOrderSearchDto logisticOrderSearchDto);
}

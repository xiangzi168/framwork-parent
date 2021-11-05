package com.amg.fulfillment.cloud.logistics.service.impl;

import com.alibaba.fastjson.JSON;
import com.amg.fulfillment.cloud.logistics.api.util.LogisticsCommonUtils;
import com.amg.fulfillment.cloud.logistics.dto.logistic.*;
import com.amg.fulfillment.cloud.logistics.factory.LogisticFactory;
import com.amg.fulfillment.cloud.logistics.manager.ILogisticManager;
import com.amg.fulfillment.cloud.logistics.service.ILogisticService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Tom
 * @date 2021-04-16-16:07
 */
@Service
@Slf4j
public class LogisticServiceImpl implements ILogisticService {

    @Autowired
    private LogisticsCommonUtils logisticsCommonUtils;
    @Autowired
    private LogisticFactory logisticFactory;


    @Override
    public LogisticTrackResponseDto getLogisticTrack(LogisticOrderSearchDto logisticOrderSearchDto) {
        ILogisticManager logisticManager = logisticFactory.getLogisticTypeFromCode(logisticOrderSearchDto.getLogisticCode());
        LogisticTrackResponseDto logisticTrackResponseDto = logisticManager.getLogisticTrackList(logisticOrderSearchDto);
        return logisticTrackResponseDto;
    }

    @Override
    public LogisticOrderResponseDto addLogisticOrder(LogisticOrderDto logisticOrderDto) {
        ILogisticManager logisticManager = logisticFactory.getLogisticTypeFromCode(logisticOrderDto.getLogisticCode());
        logisticOrderDto.setLogisticOrderNo(logisticsCommonUtils.generateDeliveryPackageNo());     //设置物流单号数据
        LogisticOrderResponseDto responseDto = logisticManager.addLogisticOrder(logisticOrderDto);
        log.info("调用物流公司code是：【{}】，返回调用结果对象是：{}",logisticOrderDto.getLogisticCode(), JSON.toJSONString(responseDto));
        return responseDto;
    }

    @Override
    public LogisticPrintLabelResponseDto getLogisticLabel(LogisticOrderSearchDto logisticOrderSearchDto) {
        ILogisticManager logisticManager = logisticFactory.getLogisticTypeFromCode(logisticOrderSearchDto.getLogisticCode());
        LogisticPrintLabelResponseDto logisticPrintLabelResponseDto = logisticManager.getLogisticPrintLabel(logisticOrderSearchDto);
        return logisticPrintLabelResponseDto;
    }
}

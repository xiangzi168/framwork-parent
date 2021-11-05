package com.amg.fulfillment.cloud.logistics.dto.logistic;

import lombok.Data;

/**
 * Created by Seraph on 2021/5/26
 */

@Data
public class LogisticDispatchResponseDto {

    private Boolean isSuccess;
    private Integer statusCode;
    private String errorMsg;
    private String dispatchOrderId;
    private String logisticsOrderNo;
    private String trackingNumber;
    private String waybillNo;
}

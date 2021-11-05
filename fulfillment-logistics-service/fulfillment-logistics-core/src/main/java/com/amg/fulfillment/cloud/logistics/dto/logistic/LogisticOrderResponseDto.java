package com.amg.fulfillment.cloud.logistics.dto.logistic;

import lombok.Data;

/**
 * @author Tom
 * @date 2021-04-16-15:46
 */
@Data
public class LogisticOrderResponseDto extends AbstractLogisticResponse{

    private String logisticsOrderNo;        //本系统成的订单号
    private String waybillNo;       //运单号
    private String trackingNumber;      //跟踪号
    private String processCode;     //包裹处理号
    private String error;       //错误信息
    private String indexNumber;     //检索号
    private String channelTrackingNum;      //渠道跟踪单号
}

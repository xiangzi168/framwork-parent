package com.amg.fulfillment.cloud.logistics.dto.logistic;

import lombok.Data;

/**
 * Created by Seraph on 2021/5/28
 */
@Data
public class LogisticOrderDetailResponseDto extends AbstractLogisticResponse {

    private String trackingNumber;      //跟踪号
    private String channelTrackingNumber;      //渠道跟踪号

}

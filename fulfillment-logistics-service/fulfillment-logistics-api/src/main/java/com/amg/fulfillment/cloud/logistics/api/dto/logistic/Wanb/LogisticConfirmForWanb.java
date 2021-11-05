package com.amg.fulfillment.cloud.logistics.api.dto.logistic.Wanb;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
@Data
public class LogisticConfirmForWanb extends AbstractResponseWanb<LogisticConfirmForWanb> {

    @JSONField(name = "Data")
    private DetailData data;

    @Data
    public static class DetailData {
        @JSONField(name = "ProcessCode")
        private String processCode;       //客户代码
        @JSONField(name = "ReferenceId")
        private String referenceId;       //客户订单号
        @JSONField(name = "TrackingNumber")
        private String trackingNumber;       //客户订单号
    }
}

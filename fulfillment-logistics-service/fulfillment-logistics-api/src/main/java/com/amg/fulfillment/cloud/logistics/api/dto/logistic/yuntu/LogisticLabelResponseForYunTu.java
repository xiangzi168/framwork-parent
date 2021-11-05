package com.amg.fulfillment.cloud.logistics.api.dto.logistic.yuntu;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Seraph on 2021/4/29
 */

@Data
public class LogisticLabelResponseForYunTu extends AbstractResponseForYunTu<LogisticLabelResponseForYunTu> {

    @JSONField(name = "Item")
    private List<OrderLabelPrint> item;

    @Data
    public static class OrderLabelPrint implements Serializable
    {
        @JSONField(name = "Url")
        private String url;
        @JSONField(name = "OrderInfos")
        private List<OrderInfo> orderInfos;

        @Data
        public static class OrderInfo implements Serializable
        {
            @JSONField(name = "CustomerOrderNumber")
            private String customerOrderNumber;
            @JSONField(name = "Error")
            private String error;
            @JSONField(name = "Code")
            private String code;
        }
    }
}

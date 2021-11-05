package com.amg.fulfillment.cloud.logistics.api.dto.logistic.px4;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Seraph on 2021/4/29
 */

@Data
public class LogisticLabelResponseFor4PX extends AbstractResponseFor4PX<LogisticLabelResponseFor4PX> {

    private LabelDetail data;

    @Data
    public static class LabelDetail implements Serializable
    {
        @JSONField(name = "label_barcode")
        private String labelBarcode;
        @JSONField(name = "child_label_barcode")
        private List<String> childLabelBarcode;
        @JSONField(name = "label_url_info")
        private LabelUrlDetail labelUrlInfo;

        @Data
        public static class LabelUrlDetail implements Serializable
        {
            @JSONField(name = "logistics_label")
            private String logisticsLabel;
            @JSONField(name = "custom_label")
            private String customLabel;
            @JSONField(name = "package_label")
            private String packageLabel;
            @JSONField(name = "invoice_label")
            private String invoiceLabel;
        }
    }
}

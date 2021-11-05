package com.amg.fulfillment.cloud.logistics.api.dto.logistic.px4;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * Created by Seraph on 2021/5/19
 */

@Data
public class LogisticProductResponseFor4PX extends AbstractResponseFor4PX<LogisticOrderResponseFor4PX> {

    private List<ProductDetail> data;

    @Data
    public static class ProductDetail {
        @JSONField(name = "logistics_product_code")
        private String logisticsProductCode;
        @JSONField(name = "logistics_product_name_cn")
        private String logisticsProductNameCn;
        @JSONField(name = "logistics_product_name_en")
        private String logisticsProductNameEn;
        @JSONField(name = "transport_mode")
        private String transportMode;
        @JSONField(name = "with_battery")
        private String withBattery;
        @JSONField(name = "order_track")
        private String orderTrack;
        @JSONField(name = "billing_volume_weight")
        private String billingVolumeWeight;
        @JSONField(name = "surface_mail")
        private String surfaceMail;
    }
}

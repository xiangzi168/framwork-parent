package com.amg.fulfillment.cloud.logistics.api.dto.logistic.px4;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * Created by Seraph on 2021/5/19
 */

@Data
public class LogisticProductSearchFor4PX {

    @JSONField(name = "transport_mode")
    private String transportMode;
    @JSONField(name = "source_country_code")
    private String sourceCountryCode;
    @JSONField(name = "source_warehouse_code")
    private String sourceWarehouseCode;
    @JSONField(name = "dest_country_code")
    private String destCountryCode;
}

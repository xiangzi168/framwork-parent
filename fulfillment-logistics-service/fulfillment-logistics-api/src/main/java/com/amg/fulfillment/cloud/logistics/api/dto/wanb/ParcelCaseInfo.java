package com.amg.fulfillment.cloud.logistics.api.dto.wanb;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by Seraph on 2021/5/28
 */

@Data
public class ParcelCaseInfo {

    @JSONField(name = "Code")
    private String code;        //箱号
    @JSONField(name = "WeightInKg")
    private BigDecimal weightInKg;      //箱子重量
    @JSONField(name = "Volume")
    private CubeSize volume;        //体积
}

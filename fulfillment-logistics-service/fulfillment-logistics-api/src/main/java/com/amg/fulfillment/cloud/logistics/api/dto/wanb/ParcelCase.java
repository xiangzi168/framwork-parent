package com.amg.fulfillment.cloud.logistics.api.dto.wanb;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by Seraph on 2021/5/28
 */

@Data
public class ParcelCase {

    @JSONField(name = "CustomerCaseNo")
    private String customerCaseNo;      //客户箱号
    @JSONField(name = "CaseCode")
    private String caseCode;      //内部箱号
    @JSONField(name = "TrackingNumber")
    private String trackingNumber;      //箱跟踪号
    @JSONField(name = "WeightInKg")
    private BigDecimal weightInKg;      //预报重
    @JSONField(name = "CheckWeightInKg")
    private BigDecimal checkWeightInKg;      //核重
    @JSONField(name = "Size")
    private CubeSize size;      //预报尺寸
    @JSONField(name = "CheckSize")
    private CubeSize checkSize;      //核验尺寸
}

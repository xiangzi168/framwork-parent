package com.amg.fulfillment.cloud.logistics.api.dto.wanb;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * Created by Seraph on 2021/5/28
 */

@Data
public class ParcelShippingMethod {

    @JSONField(name = "Code")
    private String code;        //服务代码
    @JSONField(name = "Name")
    private String name;        //服务名称
    @JSONField(name = "IsTracking")
    private String isTracking;        //是否挂号
    @JSONField(name = "IsVolumeWeight")
    private String isVolumeWeight;        //是否计泡
    @JSONField(name = "MaxVolumeWeightInCm")
    private String maxVolumeWeightInCm;        //最大体积重量限制
    @JSONField(name = "MaxWeightInKg")
    private String maxWeightInKg;        //最大重量限制(KG)
    @JSONField(name = "Region")
    private String region;        //可到达区域
    @JSONField(name = "Description")
    private String description;        //服务描述
}

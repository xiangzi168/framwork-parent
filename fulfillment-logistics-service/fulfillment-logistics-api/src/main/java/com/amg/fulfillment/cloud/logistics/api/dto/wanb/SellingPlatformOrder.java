package com.amg.fulfillment.cloud.logistics.api.dto.wanb;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * Created by Seraph on 2021/5/28
 */
@Data
public class SellingPlatformOrder {

    @JSONField(name = "SellingPlatform")
    private String sellingPlatform;     //销售平台官网首页链接
    @JSONField(name = "OrderId")
    private String orderId;     //销售平台订单号
}

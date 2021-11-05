package com.amg.fulfillment.cloud.logistics.model.vo;

import lombok.Data;

@Data
public class PurchasePackageLogisticsInfoVO {
    private String salesOrderId;
    private String itemId;
    private String purchaseId;
    // 包裹单号
    private String packageNo;
    // 物流code
    private String expressCompanyCode;
    private String expressCompanyName;
    // 运单号
    private String expressBillNo;
    // 运单状态
    private String expressStatus;
}

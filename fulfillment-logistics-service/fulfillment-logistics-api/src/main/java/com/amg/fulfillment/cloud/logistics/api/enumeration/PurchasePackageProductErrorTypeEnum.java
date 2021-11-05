package com.amg.fulfillment.cloud.logistics.api.enumeration;

import lombok.Getter;

/**
 * Created by Seraph on 2021/5/11
 */

@Getter
public enum PurchasePackageProductErrorTypeEnum {

    NULL("Null", "NULL"),
    NONE("None", "无异常"),
    QUANTITY("Quantity", "未收到货"),
    VARIANT("Variant", "货不对版，如颜色、尺寸不对等"),
    QUALITY("Quality", "质量问题，破损等等"),
    HANDLE("Handle", "已处理异常问题（自定义的状态）"),
    ;

    private String type;
    private String msg;

    PurchasePackageProductErrorTypeEnum(String type, String msg)
    {
        this.type = type;
        this.msg = msg;
    }

    public static PurchasePackageProductErrorTypeEnum getPurchasePackageProductErrorTypeEnumByType(String errorType)
    {
        PurchasePackageProductErrorTypeEnum[] purchasePackageProductErrorTypeEnumArr = PurchasePackageProductErrorTypeEnum.values();
        for (PurchasePackageProductErrorTypeEnum purchasePackageProductErrorTypeEnum : purchasePackageProductErrorTypeEnumArr) {
            if(purchasePackageProductErrorTypeEnum.getType().equals(errorType))
            {
                return purchasePackageProductErrorTypeEnum;
            }
        }
        return PurchasePackageProductErrorTypeEnum.NULL;
    }
}

package com.amg.fulfillment.cloud.logistics.api.enumeration;

import lombok.Getter;

/**
 * Created by Seraph on 2021/5/12
 */

@Getter
public enum PurchasePackageProductErrorHandleTypeEnum {

    NULL(0, "NULL"),
    PENDING_ERROR(1, "异常待处理"),
    REPURCHASED(2, "已重新采购"),
    DIRECT_WAREHOUSING(3, "已直接入库"),
    VOID_PROCESSING(4, "己作废处理")
    ;

    private Integer type;
    private String msg;

    PurchasePackageProductErrorHandleTypeEnum(Integer type, String msg)
    {
        this.type = type;
        this.msg = msg;
    }

    public static PurchasePackageProductErrorHandleTypeEnum getPurchasePackageProductErrorHandleTypeEnumByType(Integer type)
    {
        PurchasePackageProductErrorHandleTypeEnum[] purchasePackageProductErrorHandleTypeEnumArr = PurchasePackageProductErrorHandleTypeEnum.values();
        for(PurchasePackageProductErrorHandleTypeEnum purchasePackageProductErrorHandleTypeEnum : purchasePackageProductErrorHandleTypeEnumArr)
        {
            if(purchasePackageProductErrorHandleTypeEnum.getType().equals(type))
            {
                return purchasePackageProductErrorHandleTypeEnum;
            }
        }
        return PurchasePackageProductErrorHandleTypeEnum.NULL;
    }
}

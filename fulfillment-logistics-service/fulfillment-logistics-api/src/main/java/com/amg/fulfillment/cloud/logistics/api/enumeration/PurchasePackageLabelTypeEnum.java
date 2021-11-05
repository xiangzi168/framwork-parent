package com.amg.fulfillment.cloud.logistics.api.enumeration;

import lombok.Getter;

/**
 * Created by Seraph on 2021/5/12
 */

@Getter
public enum PurchasePackageLabelTypeEnum {

    NONE_ERROR(0, "无异常"),
    PENDING_ERROR(1, "异常待处理"),
    HANDLED_ERROR(2, "异常已处理"),
    ;

    private Integer type;
    private String msg;

    PurchasePackageLabelTypeEnum(Integer type, String msg)
    {
        this.type = type;
        this.msg = msg;
    }

    public static PurchasePackageLabelTypeEnum getPurchasePackageLabelTypeEnumByType(Integer type)
    {
        PurchasePackageLabelTypeEnum[] purchasePackageLabelTypeEnumArr = PurchasePackageLabelTypeEnum.values();
        for(PurchasePackageLabelTypeEnum purchasePackageLabelTypeEnum : purchasePackageLabelTypeEnumArr)
        {
            if(purchasePackageLabelTypeEnum.getType().equals(type))
            {
                return purchasePackageLabelTypeEnum;
            }
        }
        return PurchasePackageLabelTypeEnum.NONE_ERROR;
    }
}

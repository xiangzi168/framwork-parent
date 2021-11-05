package com.amg.fulfillment.cloud.logistics.api.enumeration;

import lombok.Getter;

/**
 * Created by Seraph on 2021/5/12
 */

@Getter
public enum PurchasePackagePredictionTypeEnum {

    NO(0, "未预报"),
    YES(1, "已预报"),
    NULL(-1, "异常"),
    ;

    private Integer type;
    private String msg;

    PurchasePackagePredictionTypeEnum(Integer type, String msg)
    {
        this.type = type;
        this.msg = msg;
    }

    public static PurchasePackagePredictionTypeEnum getPurchasePackagePredictionTypeEnumByType(Integer type)
    {
        PurchasePackagePredictionTypeEnum[] purchasePackagePredictionTypeEnumArr = PurchasePackagePredictionTypeEnum.values();
        for (PurchasePackagePredictionTypeEnum purchasePackagePredictionTypeEnum : purchasePackagePredictionTypeEnumArr) {
            if(purchasePackagePredictionTypeEnum.getType().equals(type))
            {
                return purchasePackagePredictionTypeEnum;
            }
        }
        return PurchasePackagePredictionTypeEnum.NULL;
    }
}

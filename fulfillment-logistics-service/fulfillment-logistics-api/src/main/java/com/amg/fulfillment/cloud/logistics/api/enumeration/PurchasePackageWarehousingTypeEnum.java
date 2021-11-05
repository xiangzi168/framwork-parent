package com.amg.fulfillment.cloud.logistics.api.enumeration;

import lombok.Getter;

/**
 * Created by Seraph on 2021/5/22
 */

@Getter
public enum PurchasePackageWarehousingTypeEnum {
    NO(10, "待入库"),
    YES(22, "已入库"),
    NULL(-1, "异常")
    ;

    private Integer type;
    private String name;

    PurchasePackageWarehousingTypeEnum(Integer type, String name)
    {
        this.type = type;
        this.name = name;
    }

    public static PurchasePackageWarehousingTypeEnum getPurchasePackageWarehousingTypeEnumByType(Integer type)
    {
        PurchasePackageWarehousingTypeEnum[] purchasePackageWarehousingTypeEnumArr = PurchasePackageWarehousingTypeEnum.values();
        for (PurchasePackageWarehousingTypeEnum purchasePackageWarehousingTypeEnum : purchasePackageWarehousingTypeEnumArr) {
            if(purchasePackageWarehousingTypeEnum.getType().equals(type))
            {
                return purchasePackageWarehousingTypeEnum;
            }
        }
        return PurchasePackageWarehousingTypeEnum.NULL;
    }
}

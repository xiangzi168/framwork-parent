package com.amg.fulfillment.cloud.logistics.api.enumeration;

import lombok.Getter;

/**
 * Created by Seraph on 2021/5/14
 */

@Getter
public enum DeliveryPackageTypeEnum {

    ABROAD_DELIVERY_PACKAGE(1, "境外发货单", "1688、PDD、TAOBAO"),
    AE_DELIVERY_PACKAGE(2, "AE 发货单", "AE"),
    CJ_DELIVERY_PACKAGE(4, "CJ 发货单", "CJ")
    ;

    private Integer type;
    private String msg;
    private String channel;

    DeliveryPackageTypeEnum(Integer type, String msg, String channel)
    {
        this.type = type;
        this.msg = msg;
        this.channel = channel;
    }

    public static DeliveryPackageTypeEnum getDeliveryPackageTypeEnumByType(Integer type)
    {
        DeliveryPackageTypeEnum[] deliveryPackageTypeEnumArr = DeliveryPackageTypeEnum.values();
        for (DeliveryPackageTypeEnum deliveryPackageTypeEnum : deliveryPackageTypeEnumArr) {
            if(deliveryPackageTypeEnum.getType().equals(type))
            {
                return deliveryPackageTypeEnum;
            }
        }
        return null;
    }
}

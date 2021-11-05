package com.amg.fulfillment.cloud.logistics.api.enumeration;

import lombok.Getter;

/**
 * Created by Seraph on 2021/6/5
 */

@Getter
public enum DeliveryPackagePurchaseChannelEnum {

    ALIBABA(1, "1688", "1688"),
    AE(2, "AE", "AE"),
    STOCK(3, "STOCK", "备货"),
    CJ(4, "CJ", "CJ"),
    PDD(5, "PDD","PDD"),
    TAOBAO(6, "TAOBAO","淘宝"),
    ;

    private Integer type;
    private String msg;
    private String channel;

    DeliveryPackagePurchaseChannelEnum(Integer type, String msg, String channel)
    {
        this.type = type;
        this.msg = msg;
        this.channel = channel;
    }

    public static DeliveryPackagePurchaseChannelEnum getDeliveryPackagePurchaseChannelEnumByType(Integer type)
    {
        DeliveryPackagePurchaseChannelEnum[] deliveryPackagePurchaseChannelEnumArr = DeliveryPackagePurchaseChannelEnum.values();
        for (DeliveryPackagePurchaseChannelEnum deliveryPackagePurchaseChannelEnum : deliveryPackagePurchaseChannelEnumArr) {
            if(deliveryPackagePurchaseChannelEnum.getType().equals(type))
            {
                return deliveryPackagePurchaseChannelEnum;
            }
        }
        return null;
    }
}

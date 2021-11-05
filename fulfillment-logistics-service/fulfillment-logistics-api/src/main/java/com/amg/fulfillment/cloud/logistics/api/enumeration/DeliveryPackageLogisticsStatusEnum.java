package com.amg.fulfillment.cloud.logistics.api.enumeration;

import lombok.Getter;

/**
 * Created by Seraph on 2021/5/20
 */

@Getter
public enum

DeliveryPackageLogisticsStatusEnum {

    INIT(0, "初始状态"),
    CREATED(10, "已创建物流单"),
//    INDELIVERY(15, "揽收中(发货中)"),
    DELIVERED(20, "已揽收(已发货)"),
    RECEIVED(30, "已收货（签收）"),
    CANCEL(50, "已取消"),
    ERROR(99, "创建物流单异常"),
    NULL(-1, "异常状态"),
    ;

    DeliveryPackageLogisticsStatusEnum(Integer code, String msg)
    {
        this.code = code;
        this.msg = msg;
    }

    private Integer code;
    private String msg;

    public static DeliveryPackageLogisticsStatusEnum getDeliveryPackageLogisticsStatusEnumTypeCode(Integer code)
    {
        DeliveryPackageLogisticsStatusEnum[] deliveryPackageLogisticsStatusEnumArr = DeliveryPackageLogisticsStatusEnum.values();
        for (DeliveryPackageLogisticsStatusEnum deliveryPackageLogisticsStatusEnum : deliveryPackageLogisticsStatusEnumArr)
        {
            if(deliveryPackageLogisticsStatusEnum.getCode().compareTo(code)==0)
            {
                return deliveryPackageLogisticsStatusEnum;
            }
        }
        return DeliveryPackageLogisticsStatusEnum.NULL;
    }
}

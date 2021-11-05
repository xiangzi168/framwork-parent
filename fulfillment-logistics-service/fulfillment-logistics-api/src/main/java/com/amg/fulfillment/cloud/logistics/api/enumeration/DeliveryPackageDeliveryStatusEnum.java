package com.amg.fulfillment.cloud.logistics.api.enumeration;

import lombok.Getter;

/**
 * Created by Seraph on 2021/5/28
 */

@Getter
public enum DeliveryPackageDeliveryStatusEnum {

    INIT(0, "初始状态"),
    PREPARATION(30, "已制单"),
    PREPARATIONFAIL(31, "制单失败"),
    SENDING(40, "发货中"),
    SENDED(41, "已发货"),
    CANCELSEND(42, "取消发货"),
    FAILSENDED(43, "发货失败"),
    FAILPUSH(45, "推送失败"),
    NULL(-1, "异常");

    private Integer code;
    private String name;

    DeliveryPackageDeliveryStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static DeliveryPackageDeliveryStatusEnum getDeliveryPackageDeliveryStatusEnumByCode(Integer code) {
        DeliveryPackageDeliveryStatusEnum[] deliveryPackageDeliveryStatusEnumArr = DeliveryPackageDeliveryStatusEnum.values();
        for (DeliveryPackageDeliveryStatusEnum deliveryPackageDeliveryStatusEnum : deliveryPackageDeliveryStatusEnumArr) {
            if (deliveryPackageDeliveryStatusEnum.getCode().compareTo(code) == 0) {
                return deliveryPackageDeliveryStatusEnum;
            }
        }
        return DeliveryPackageDeliveryStatusEnum.NULL;
    }
}

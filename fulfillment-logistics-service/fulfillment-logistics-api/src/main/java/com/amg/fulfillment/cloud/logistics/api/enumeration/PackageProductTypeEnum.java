package com.amg.fulfillment.cloud.logistics.api.enumeration;

import lombok.Getter;

/**
 * Created by Seraph on 2021/5/13
 */

@Getter
public enum PackageProductTypeEnum {
    PURCHASE_PACKAGE(1, "采购包裹单"),
    DELIVERY_PACKAGE(2, "发货包裹单")
    ;

    private Integer type;
    private String msg;
    PackageProductTypeEnum(Integer type, String msg)
    {
        this.type = type;
        this.msg = msg;
    }
}

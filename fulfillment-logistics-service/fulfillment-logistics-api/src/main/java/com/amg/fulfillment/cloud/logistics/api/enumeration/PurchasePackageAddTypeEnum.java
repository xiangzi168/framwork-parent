package com.amg.fulfillment.cloud.logistics.api.enumeration;

import lombok.Getter;

@Getter
public enum PurchasePackageAddTypeEnum {
    SYSTEM_ADD(1, "程序添加"),
    MANUAL_ADD(2, "人工添加")
            ;

    private Integer type;
    private String msg;

    PurchasePackageAddTypeEnum(Integer type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public static PurchasePackageAddTypeEnum getPurchasePackageAddTypeEnum(Integer type) {

        PurchasePackageAddTypeEnum[] values = PurchasePackageAddTypeEnum.values();
        for (PurchasePackageAddTypeEnum purchasePackageAddTypeEnum: values){
            if(purchasePackageAddTypeEnum.getType().equals(type)){
                return purchasePackageAddTypeEnum;
            }
        }
        return null;
    }
}

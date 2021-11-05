package com.amg.fulfillment.cloud.logistics.api.enumeration;

import lombok.Getter;

import java.util.Arrays;

/**
 * Created by Seraph on 2021/5/19
 */

@Getter
public enum  WBWithBatteryTypeEnum {

    NOBattery(1, "不带电","NOBattery"),
    WithBattery(2, "带电","WithBattery"),
    Battery(3, "纯电池","Battery");


    private Integer id;
    private String name;
    private String code;

    WBWithBatteryTypeEnum(Integer id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    public static String getCode(Integer id) {
        return Arrays.stream(WBWithBatteryTypeEnum.values()).filter(item ->item.equals(id)).findFirst().orElse(NOBattery).getCode();
    }
}

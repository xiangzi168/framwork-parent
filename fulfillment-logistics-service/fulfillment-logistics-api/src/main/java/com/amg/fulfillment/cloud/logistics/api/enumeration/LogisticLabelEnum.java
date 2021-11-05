package com.amg.fulfillment.cloud.logistics.api.enumeration;

import lombok.Getter;

/**
 * Created by Seraph on 2021/8/31
 */

@Getter
public enum LogisticLabelEnum {
    activity1(1, "活动商品A手表"),
    activity2(2, "活动商品B耳环"),
    activity3(3, "活动商品C项链");

    private int code;
    private String msg;

    LogisticLabelEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static String getLogisticLabelEnum(Integer code) {
        for (LogisticLabelEnum value : LogisticLabelEnum.values()) {
            if (value.getCode()==code) {
                return value.getMsg();
            }
        }
        throw new RuntimeException("未知的活动标签类型【code】码是："+code);
    }

    public static Integer getLogisticLabelCode(String msg) {
        for (LogisticLabelEnum value : LogisticLabelEnum.values()) {
            if (value.getMsg().equals(msg)) {
                return value.getCode();
            }
        }
        throw new RuntimeException("未知的活动标签类型【msg】码是："+msg);
    }

}

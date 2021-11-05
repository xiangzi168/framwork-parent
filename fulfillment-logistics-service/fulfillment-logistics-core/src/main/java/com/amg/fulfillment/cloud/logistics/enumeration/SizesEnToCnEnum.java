package com.amg.fulfillment.cloud.logistics.enumeration;

import lombok.Getter;

import java.util.Arrays;

/**
 * 尺码表英文-》中文
 */
@Getter
public enum SizesEnToCnEnum {
    DEFAULT("default", "default"),
    LENGTH("length", "衣长"),
    BUST("bust", "女性胸围"),
    SHOULDER_WIDTH("shoulderwidth", "肩宽"),
    WAIST("waist", "腰围"),
    HIP("hip", "臀围"),
    SLEEVE("sleeve", "袖长"),
    CHEST("chest", "男性胸围"),
    FOOT_LENGTH("footlength", "脚长");
    private String en;
    private String zh;

    SizesEnToCnEnum(String en, String zh) {
        this.en = en;
        this.zh = zh;
    }

    public static SizesEnToCnEnum getSizesEnToCnEnumByEn(String en) {
        String en_new = en.replaceAll(" ", "").toLowerCase();
        return Arrays.stream(SizesEnToCnEnum.values()).filter(item -> item.getEn().equals(en_new))
                .findFirst().orElse(DEFAULT);
    }
}

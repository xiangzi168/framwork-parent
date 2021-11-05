package com.amg.fulfillment.cloud.logistics.enumeration;

import lombok.Getter;

/**
 * @author Tom
 * @date 2021-05-24-19:57
 */
@Getter
public enum LogisticMatchRuleTypeEnum {
    COUNTRY("country", "订单目的地为指定国家"),
    PROVINCE("province", "订单目的地为指定州省"),
    CITY("city", "订单目的地为指定城市"),
    POSTCODE("postcode", "订单目的地为指定邮编"),
    CATEGORY("category", "订单内含商品为指定类目"),
    PRODUCTID("productId", "订单内含商品为指定商品ID"),
    LABEL("label", "订单内商品为指定标签"),
    LABEL_All("labelAll", "订单内所有商品都为指定标签"),
    WEIGHT("weight", "订单内商品为指定重量段"),
    AMOUNT("amount", "订单的付款金额为指定的范围"),
    PRICE("price", "订单内单个商品价格在指定范围内"),
    ;
    private String code;
    private String name;

    LogisticMatchRuleTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}

package com.amg.fulfillment.cloud.logistics.api.enumeration;

import lombok.Getter;

/**
 * @author Tom
 * @date 2021/4/12  18:08
 * 仓库类型
 */
@Getter
public enum DepositoryTypeEnum {
    TEST(1,"测试类" ),
    WANB(2,"万邦")
    ;
    private Integer code;
    private String name;
    DepositoryTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getDepositoryTypeFromCode(Integer code) {
        for (DepositoryTypeEnum value : DepositoryTypeEnum.values()) {
            if (value.getCode().equals(code)) {
                return value.getName();
            }
        }
        throw new RuntimeException("系统未配置此仓库【code】码是："+code);
    }
}

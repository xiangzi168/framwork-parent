package com.amg.fulfillment.cloud.logistics.enumeration;

import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import com.amg.fulfillment.cloud.logistics.manager.TestDepositoryManagerImpl;
import com.amg.fulfillment.cloud.logistics.manager.WanBDepositoryManagerImpl;
import lombok.Getter;

/**
 * @author Tom
 * @date 2021/4/12  18:08
 * 仓库类型
 */
@Getter
public enum DepositoryTypeEnum {


    TEST(1,"测试类", Constant.LOGISTIC_TEST, TestDepositoryManagerImpl.class),
    WANB(2,"万邦", Constant.DEPOSITORY_WB, WanBDepositoryManagerImpl.class)
    ;
    private Integer id;
    private String name;
    private String code;
    private Class serviceClass;

    DepositoryTypeEnum(Integer id, String name, String code, Class serviceClass) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.serviceClass = serviceClass;
    }

    public static Class getDepositoryTypeFromCode(String code) {
        for (DepositoryTypeEnum value : DepositoryTypeEnum.values()) {
            if (value.getCode().equals(code)) {
                return value.getServiceClass();
            }
        }
        throw new RuntimeException("系统未配置此仓库【code】码是："+code);
    }
}

package com.amg.fulfillment.cloud.logistics.enumeration;

import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import com.amg.fulfillment.cloud.logistics.manager.*;
import com.amg.fulfillment.cloud.logistics.manager.WanBLogisticManagerImpl;
import lombok.Getter;

/**
 * @author Tom
 * @date 2021-04-16-15:52
 * 物流公司枚举
 */
@Getter
public enum LogisticTypeEnum {
    WANB(1,"万邦",Constant.DEPOSITORY_WB, WanBLogisticManagerImpl.class),
    YUNTU(2,"云途",Constant.LOGISTIC_YUNTU, YunTuLogisitcManagerImpl.class),
    YANWEN(3,"燕文",Constant.LOGISTIC_YANWEN, YanWenLogisticManagerImpl.class),
    PX4(4,"递四方",Constant.LOGISTIC_PX4, PX4LogisticManagerImpl.class),
    TEST(5,"物流测试",Constant.LOGISTIC_TEST, TestLogisticManagerImpl.class)
    ;


    private Integer id;
    private String name;
    private String code;
    private Class serviceClass;

    LogisticTypeEnum(Integer id, String name, String code, Class serviceClass) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.serviceClass = serviceClass;
    }

    public static LogisticTypeEnum getLogisticTypeEnumByCode(String code)
    {
        LogisticTypeEnum[] logisticTypeEnumArr = LogisticTypeEnum.values();
        for(LogisticTypeEnum logisticTypeEnum : logisticTypeEnumArr)
        {
            if(logisticTypeEnum.getCode().equals(code))
            {
                return logisticTypeEnum;
            }
        }
        return null;
    }

    public static Class getLogisticTypeFromCode(String code) {
        for (LogisticTypeEnum value : LogisticTypeEnum.values()) {
            if (value.getCode().equals(code)) {
                return value.getServiceClass();
            }
        }
        throw new RuntimeException("系统未配置此物流【code】码是："+code);
    }
}

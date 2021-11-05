package com.amg.fulfillment.cloud.logistics.api.enumeration;

import lombok.Getter;

/**
 * Created by Seraph on 2021/5/24
 */

@Getter
public enum OperationalTypeEnum {
    ADD("add", "添加"),
    UPDATE("udpate", "修改"),
    NULL("null", "异常数据"),

    ;
    OperationalTypeEnum(String type, String msg)
    {
        this.type = type;
        this.msg = msg;
    }
    private String type;
    private String msg;


    public static OperationalTypeEnum getOperationalTypeEnumByType(String type)
    {
        OperationalTypeEnum[] operationalTypeEnumArr = OperationalTypeEnum.values();
        for (OperationalTypeEnum operationalTypeEnum : operationalTypeEnumArr) {
            if(operationalTypeEnum.getType().equals(type))
            {
                return operationalTypeEnum;
            }
        }
        return OperationalTypeEnum.NULL;
    }
}

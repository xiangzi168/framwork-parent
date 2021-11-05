package com.amg.fulfillment.cloud.logistics.api.enumeration;

import lombok.Getter;

/**
 * @ClassName LogisticsStatusEnum
 * @Description 物流状态枚举类
 * @Author qh
 * @Date 2021/7/28 11:38
 **/
@Getter
public enum LogisticsStatusFor1688Enum {
    CONSIGN("CONSIGN", "已发货",1),
    ACCEPT("ACCEPT", "已揽收",2),
    TRANSPORT("TRANSPORT", "运输中",3),
    DELIVERING("DELIVERING", "已派送",4),
    SIGN("SIGN", "已签收",5);



    private String type;
    private String name;
    private Integer code;

    LogisticsStatusFor1688Enum(String type, String name, Integer code) {
        this.type = type;
        this.name = name;
        this.code = code;
    }

    public static LogisticsStatusFor1688Enum getLogisticsStatusEnum(Integer code) {

        LogisticsStatusFor1688Enum[] values = LogisticsStatusFor1688Enum.values();
        for (LogisticsStatusFor1688Enum logisticsStatusFor1688Enum: values){
            if(logisticsStatusFor1688Enum.getCode().equals(code)){
                return logisticsStatusFor1688Enum;
            }
        }
        return null;
    }
    public static LogisticsStatusFor1688Enum[] getLogisticsStatusEnums() {
        LogisticsStatusFor1688Enum[] values = LogisticsStatusFor1688Enum.values();
        return values;
    }
}

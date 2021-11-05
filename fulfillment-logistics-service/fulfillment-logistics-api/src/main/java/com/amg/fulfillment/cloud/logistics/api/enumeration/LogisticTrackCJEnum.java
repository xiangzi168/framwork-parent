package com.amg.fulfillment.cloud.logistics.api.enumeration;

import io.swagger.models.auth.In;
import lombok.Getter;

/**
 * @ClassName LogisticTrackCJEnum
 * @Description CJ物流轨迹状态枚举类
 * @Author 35112
 * @Date 2021/8/12 11:00
 **/
@Getter
public enum LogisticTrackCJEnum {
    SIGN(1, "已妥投",30),
    TAKE(2, "到达待取",20),
    CANCELLED(3, "商家取消发货",50),
    RETURNED(4, "包裹退回",50),
    UNKNOWN(5, "未知状态",20);
    private Integer code;
    private String name;
    private Integer logisticStatus;
    LogisticTrackCJEnum(Integer code,String name,Integer logisticStatus) {
        this.code = code;
        this.name = name;
        this.logisticStatus=logisticStatus;
    }
    public static LogisticTrackCJEnum getLogisticTrackCJEnum(Integer code) {

        LogisticTrackCJEnum[] values = LogisticTrackCJEnum.values();
        for (LogisticTrackCJEnum logisticTrackCJEnum: values){
            if(logisticTrackCJEnum.getCode().equals(code)){
                return logisticTrackCJEnum;
            }
        }
        return null;
    }
}

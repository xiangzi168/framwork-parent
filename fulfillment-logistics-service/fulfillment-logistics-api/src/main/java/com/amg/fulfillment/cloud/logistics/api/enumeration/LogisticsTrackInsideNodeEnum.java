package com.amg.fulfillment.cloud.logistics.api.enumeration;

import com.amg.fulfillment.cloud.logistics.api.proto.LogisticsTrackDetailGTO;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum LogisticsTrackInsideNodeEnum {
    //物流商揽收
    HABROAD_CARRIER_RECEIVED(1,"CARRIER_RECEIVED","物流商揽收", LogisticsTrackDetailGTO.InsideNodeEnum.ABROAD_CARRIER_RECEIVED),
    //物流商出库
    ABROAD_DEPARTED_FROMLC(1,"DEPARTED_FROMLC","物流商出库",LogisticsTrackDetailGTO.InsideNodeEnum.ABROAD_DEPARTED_FROMLC),
    //国内退回
    ABROAD_DOMESTIC_RETURN(1,"DOMESTIC_RETURN","国内退回",LogisticsTrackDetailGTO.InsideNodeEnum.ABROAD_DOMESTIC_RETURN),
    //清关异常
    ABROAD_CUSTOMS_ABNORMAL(1,"CUSTOMS_ABNORMAL","清关异常",LogisticsTrackDetailGTO.InsideNodeEnum.ABROAD_CUSTOMS_ABNORMAL),
    //到达待取
    ABROAD_PICK_UP(1,"PICK_UP","到达待取",LogisticsTrackDetailGTO.InsideNodeEnum.ABROAD_PICK_UP),
    //投递失败
    ABROAD_UNDELIVERED(1,"UNDELIVERED","投递失败",LogisticsTrackDetailGTO.InsideNodeEnum.ABROAD_UNDELIVERED),
    //包裹丢失
    ABROAD_PARCEL_LOST(1,"PARCEL_LOST","包裹丢失",LogisticsTrackDetailGTO.InsideNodeEnum.ABROAD_PARCEL_LOST),
    //成功签收
    ABROAD_DELIVERED(1,"DELIVERED","成功签收",LogisticsTrackDetailGTO.InsideNodeEnum.ABROAD_DELIVERED),
    //海外退回
    ABROAD_RETURNED(1,"RETURNED","海外退回",LogisticsTrackDetailGTO.InsideNodeEnum.ABROAD_RETURNED),
    //可能异常
    ABROAD_ALERT(1,"ALERT","可能异常",LogisticsTrackDetailGTO.InsideNodeEnum.ABROAD_ALERT),
    //转运延误
    ABROAD_TRANSIT_DELAY(1,"TRANSIT_DELAY","转运延误",LogisticsTrackDetailGTO.InsideNodeEnum.ABROAD_TRANSIT_DELAY),

    // 妥投
    AE_DELIVERED(2,"delivered","妥投",LogisticsTrackDetailGTO.InsideNodeEnum.AE_DELIVERED),
    // 到达待取
    AE_ARRIVE(2,"arrive","到达待取",LogisticsTrackDetailGTO.InsideNodeEnum.AE_ARRIVE),
    // 商家取消发货
    AE_CANNCEL(2,"canncel","商家取消发货",LogisticsTrackDetailGTO.InsideNodeEnum.AE_CANNCEL),
    // 包裹退回
    AE_BACK(2,"back","包裹退回",LogisticsTrackDetailGTO.InsideNodeEnum.AE_BACK),
    DEFAULT(-1,"default","未定义",LogisticsTrackDetailGTO.InsideNodeEnum.DEFAULT)
    ;
    private Integer type;
    private String nodeEn;
    private String desc;
    private LogisticsTrackDetailGTO.InsideNodeEnum platformEnum;

    LogisticsTrackInsideNodeEnum(Integer type, String nodeEn, String desc,  LogisticsTrackDetailGTO.InsideNodeEnum platformEnum) {
        this.type = type;
        this.nodeEn = nodeEn;
        this.desc = desc;
        this.platformEnum = platformEnum;
    }

    public static LogisticsTrackInsideNodeEnum getInsideNodeEnumByNodeEn(String nodeEn){
        return Arrays.stream(LogisticsTrackInsideNodeEnum.values()).filter(item ->item.getNodeEn().equals(nodeEn)).findFirst().orElse(DEFAULT);
    }
}

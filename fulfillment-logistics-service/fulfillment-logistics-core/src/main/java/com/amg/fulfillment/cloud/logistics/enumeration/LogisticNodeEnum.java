package com.amg.fulfillment.cloud.logistics.enumeration;

import lombok.Getter;

@Getter
public enum LogisticNodeEnum {
    DEFAULT("WAITINGFORRECEIVE","物流商未揽收"),
    ABROAD_CARRIER_RECEIVED("CARRIER_RECEIVED","境外物流商揽收"),
    ABROAD_DOMESTIC_RETURN("DOMESTIC_RETURN","境外国内退回"),
    ABROAD_TRANSIT_DELAY("TRANSIT_DELAY","境外转运延误"),
    ABROAD_CUSTOMS_ABNORMAL("CUSTOMS_ABNORMAL","境外清关异常"),
    ABROAD_PICK_UP("PICK_UP","境外到达待取"),
    ABROAD_UNDELIVERED("UNDELIVERED","境外投递失败"),
    ABROAD_ALERT("ALERT","境外可能异常"),
    ABROAD_RETURNED("RETURNED","境外海外退回"),
    AE_DELIVERED("delivered","AE妥投"),
    AE_ARRIVE("arrive","AE到达待取"),
    AE_CANNCEL("canncel","AE商家取消发货"),
    AE_BACK("back","AE包裹退回")
    ;

    private String nodeEn;
    private String nodeCh;

    LogisticNodeEnum(String nodeEn, String nodeCh) {
        this.nodeEn = nodeEn;
        this.nodeCh = nodeCh;
    }
}

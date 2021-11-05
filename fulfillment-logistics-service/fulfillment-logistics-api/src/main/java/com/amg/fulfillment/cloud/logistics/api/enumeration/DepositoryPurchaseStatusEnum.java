package com.amg.fulfillment.cloud.logistics.api.enumeration;

import lombok.Getter;

/**
 * @author Tom
 * @date 2021-05-22-15:42
 */

@Getter
public enum DepositoryPurchaseStatusEnum  {

    WAITINGWAREHOUSING(10,"待入库"),
    HANLDINGEXCEPTION(20,"待处理"),
    HANDLEDEXCEPTION(21,"已处理"),
    WAREHOUSING(22,"已入库"),
    PREPARATION(30,"已制单"),
    PREPARATIONFAIL(31,"制单失败"),
    WAITINGPREPARATION(32,"待制单"),
    SENDING(40,"发货中"),
    SENDED(41,"已发货"),
    CANCELSEND(42,"取消发货"),
    FAILSENDED(43,"发货失败"),
    RECEIVED(44, "已收货"),
    FAILPUSH(45, "推送失败"),
    NULL(-1, "异常");
    ;
    private Integer statusCode;
    private String stausName;

    DepositoryPurchaseStatusEnum(Integer statusCode, String stausName) {
        this.statusCode = statusCode;
        this.stausName = stausName;
    }

    public static DepositoryPurchaseStatusEnum getEnumByStatusCode(Integer statusCode){
        for (DepositoryPurchaseStatusEnum depositoryPurchaseStatusEnum : DepositoryPurchaseStatusEnum.values()) {
            if(depositoryPurchaseStatusEnum.getStatusCode().equals(statusCode))
                return depositoryPurchaseStatusEnum;
        }
        return NULL;
    }
}

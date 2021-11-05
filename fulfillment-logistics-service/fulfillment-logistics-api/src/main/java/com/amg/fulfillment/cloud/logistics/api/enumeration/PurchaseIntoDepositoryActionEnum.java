package com.amg.fulfillment.cloud.logistics.api.enumeration;

import lombok.Getter;

/**
 * Created by Seraph on 2021/5/21
 */

@Getter
public enum PurchaseIntoDepositoryActionEnum {
    //采购动作
    PURCHASE_RECEIVEFINISH("ReceiveFinishm", "回传数据并且收货完结"),
    PURCHASE_RECEIVE("Receive", "仅回传当前批次数据，采购单未未完结"),
    PURCHASE_FINISH("Finish", "收货完成"),

    //发货动作
    DELIVER_VOUCHER_PREPARED("DELIVER_VOUCHER_PREPARED", "已制单"),
    DELIVER_VOUCHER_PREPARATION_FAILED("DELIVER_VOUCHER_PREPARATION_FAILED", "制单失败"),
    DELIVER_ON_THE_WAY("DELIVER_ON_THE_WAY", "发货中"),
    DELIVER_FINISH("DELIVER_FINISH", "已发货"),
    DELIVER_CANCEL("DELIVER_CANCEL", "取消发货")
    ;

    PurchaseIntoDepositoryActionEnum(String action, String msg)
    {
        this.action = action;
        this.msg = msg;
    }

    private String action;
    private String msg;
}

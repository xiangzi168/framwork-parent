package com.amg.fulfillment.cloud.logistics.dto.depository;

import lombok.Data;
import lombok.Getter;

/**
 * @author Tom
 * @date 2021-04-23-19:00
 */
@Data
public class DepositoryMessage<T>{
    private DepositoryMessageType type;
    private T msgBody;

    @Getter
    public static enum DepositoryMessageType{
        WB_PACK("WB_PACK","万邦仓库出库打包消息通知"),
        WB_PROCESS("WB_PROCESS","万邦仓库出库订单处理结果通知"),
        WB_PURCHASE_ARRIVE("WB_PURCHASE_ARRIVE","万邦仓库采购订单快递到仓信息通知"),
        WB_PURCHASE_IN("WB_PURCHASE_IN","万邦仓库采购订单入库信息通知")
        ;
        private String code;
        private String name;

        DepositoryMessageType(String code, String name) {
            this.code = code;
            this.name = name;
        }
    }
}

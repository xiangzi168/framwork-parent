package com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

/**
 * @author Tom
 * @date 2021-04-14-18:46
 */
@ApiModel("出库订单响应对象")
@Data
public class OutDepositoryOrderResponseForWanB extends AbstractResponseMsg<OutDepositoryOrderResponseForWanB> {

    @ApiModelProperty(name = "出库订单详情")
    private OutOrderDetailForWanB Data;

    public OutOrderDetailForWanB getData() {
        if (Objects.isNull(Data)) {
            Data = new OutOrderDetailForWanB();
        }
        return Data;
    }
}

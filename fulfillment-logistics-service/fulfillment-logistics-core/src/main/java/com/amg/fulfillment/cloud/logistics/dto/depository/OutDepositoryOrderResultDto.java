package com.amg.fulfillment.cloud.logistics.dto.depository;

import com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb.OutOrderDetailForWanB;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Tom
 * @date 2021-04-15-10:16
 */
@ApiModel("出库订单对象")
@Data
public class OutDepositoryOrderResultDto {
    @ApiModelProperty(name = "仓库代码")
    private String depositoryCode;
    @ApiModelProperty(name = "万邦出库订单详情")
    private OutOrderDetailForWanB wbData;
}

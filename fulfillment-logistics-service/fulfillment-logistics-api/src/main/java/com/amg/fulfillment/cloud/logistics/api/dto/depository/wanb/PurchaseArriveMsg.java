package com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Tom
 * @date 2021-04-23-18:03
 */
@Data
public class PurchaseArriveMsg {

    @ApiModelProperty("快递单号")
    @JsonProperty(value = "ExpressId")
    private String ExpressId;
    @ApiModelProperty("入仓时间")
    @JsonProperty(value = "ArriveOn")
    private String ArriveOn;
}

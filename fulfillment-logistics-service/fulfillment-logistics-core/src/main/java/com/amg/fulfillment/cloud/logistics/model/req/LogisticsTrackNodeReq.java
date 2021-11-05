package com.amg.fulfillment.cloud.logistics.model.req;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Seraph on 2021/6/1
 */

@Data
@ApiModel(value = "LogisticsTrackNodeReq")
public class LogisticsTrackNodeReq {

    @ApiModelProperty(value = "包裹类型  1 境外发货包裹单      2 AE 发货包裹 单")
    private Integer type = 1;
    @ApiModelProperty(value = "物流商 code")
    private String logisticsCode;
}

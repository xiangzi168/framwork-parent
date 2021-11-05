package com.amg.fulfillment.cloud.logistics.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "DepositoryDataStatisticsVO")
public class LogisticsDataStatisticsForAEVO {

    @ApiModelProperty("AE妥投")
    private int delivered;
    @ApiModelProperty("AE到达待取")
    private int arrive;
    @ApiModelProperty("AE商家取消发货")
    private int canncel;
    @ApiModelProperty("AE包裹退回")
    private int back;
}

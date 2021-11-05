package com.amg.fulfillment.cloud.logistics.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "DepositoryDataStatisticsVO")
public class DepositoryDataStatisticsVO {
    @ApiModelProperty("未预报仓库包裹")
    private int notPrediction;
    @ApiModelProperty("异常待处理")
    private int notExceptionHandlded;
    @ApiModelProperty("推送失败")
    private int pushFail;
    @ApiModelProperty("发货失败")
    private int sendFail;
    @ApiModelProperty("取消发货")
    private int cancelSend;
    @ApiModelProperty("发货中")
    private int sending;
    @ApiModelProperty("制单失败")
    private int preparationFail;
    @ApiModelProperty("已制单")
    private int preparation;

}

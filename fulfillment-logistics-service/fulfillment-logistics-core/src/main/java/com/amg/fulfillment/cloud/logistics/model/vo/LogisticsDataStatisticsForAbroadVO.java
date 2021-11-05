package com.amg.fulfillment.cloud.logistics.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "DepositoryDataStatisticsVO")
public class LogisticsDataStatisticsForAbroadVO {
    @ApiModelProperty("物流商未揽收")
    private int notReceive;
    @ApiModelProperty("物流商揽收")
    private int carrierReceived;
    @ApiModelProperty("国内退回")
    private int domesticReturn;
    @ApiModelProperty("转运延误")
    private int transitDelay;
    @ApiModelProperty("清关异常")
    private int customsAbnormal;
    @ApiModelProperty("到达待取")
    private int pickUp;
    @ApiModelProperty("投递失败")
    private int undelivered;
    @ApiModelProperty("可能异常")
    private int alert;
    @ApiModelProperty("海外退回")
    private int returned;
    @ApiModelProperty("到达待取-未通知用户统计")
    private int pickUpNotNotice;
    @ApiModelProperty("海外退回-未通知用户统计")
    private int domesticReturnNotNotice;
}

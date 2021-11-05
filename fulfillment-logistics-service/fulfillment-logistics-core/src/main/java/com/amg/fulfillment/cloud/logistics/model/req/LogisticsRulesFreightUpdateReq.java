package com.amg.fulfillment.cloud.logistics.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by Seraph on 2021/5/25
 */

@Data
@ApiModel(value = "LogisticsRulesFreightUpdateReq")
public class LogisticsRulesFreightUpdateReq {

    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "规则名称")
    private String name;
    @ApiModelProperty(value = "物流商 code")
    private String logisticsCode;
    @ApiModelProperty(value = "物流渠道 code")
    private String channelCode;
    @ApiModelProperty(value = "国家")
    private String country;
    @ApiModelProperty(value = "国家简写")
    private String countryAbbre;
    @ApiModelProperty(value = "起结重量 (g)")
    private BigDecimal startWeight;
    @ApiModelProperty(value = "截止重是 （g）")
    private BigDecimal endWeight;
    @ApiModelProperty(value = "挂号费（元）")
    private BigDecimal pegistrationMoney;
    @ApiModelProperty(value = "单位价格（g/元）")
    private BigDecimal unitPrice;
    @ApiModelProperty(value = "时效最早天数")
    private Integer earliestPrescriptionDays;
    @ApiModelProperty(value = "时效最晚天数")
    private Integer latestPrescriptionDays;
    @ApiModelProperty(value = "是否禁用")
    private Integer isDisable;
}

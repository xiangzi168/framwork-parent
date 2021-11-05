package com.amg.fulfillment.cloud.logistics.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Seraph on 2021/5/25
 */

@Data
@ApiModel("LogisticsRulesFreightReq")
public class LogisticsRulesFreightReq extends BaseReq{

    @ApiModelProperty(value = "规则名称")
    private String name;
    @ApiModelProperty(value = "物流商 code")
    private String logisticsCode;
    @ApiModelProperty(value = "渠道 code")
    private String channelCode;
    @ApiModelProperty(value = "物流商 模糊摸索")
    private String logisticsProvider;
    @ApiModelProperty(value = "渠道 模糊摸索")
    private String logisticsChannel;
    @ApiModelProperty(value = "国家 模糊摸索")
    private String country;
    @ApiModelProperty(value = "是否禁用")
    private Integer isDisable;
}

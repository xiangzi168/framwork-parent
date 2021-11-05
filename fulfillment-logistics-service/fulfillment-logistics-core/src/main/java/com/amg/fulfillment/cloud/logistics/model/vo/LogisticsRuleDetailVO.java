package com.amg.fulfillment.cloud.logistics.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@ApiModel("返回物流匹配规则详情")
@Data
@ToString
public class LogisticsRuleDetailVO {

    private Long id;

    @ApiModelProperty(value = "规则名称")
    private String name;

    @ApiModelProperty(value = "物流code")
    private String logisticsCode;

    @ApiModelProperty(value = "物流名称")
    private String logisticsName;

    @ApiModelProperty(value = "物流渠道 code")
    private String channelCode;

    @ApiModelProperty(value = "物流渠道名称")
    private String channelName;

    @ApiModelProperty(value = "外部优先级序号")
    private Integer level;

    @ApiModelProperty(value = "规则内容")
    private String content;

    @ApiModelProperty(value = "是否禁用   0 否  1 是")
    private Integer isDisable;

    @ApiModelProperty(value = "操作行为")
    private String operationalBehavior;

}

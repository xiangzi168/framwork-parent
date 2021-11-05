package com.amg.fulfillment.cloud.logistics.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;
import lombok.ToString;

/**
 * @author Tom
 * @date 2021-05-24-18:19
 */
@ApiModel("查询物流规则")
@Data
@ToString(callSuper = true)
public class LogisticsRuleSearchReq extends BaseReq {

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

    @ApiModelProperty(value = "是否禁用   0 否  1 是")
    private Integer isDisable;

}

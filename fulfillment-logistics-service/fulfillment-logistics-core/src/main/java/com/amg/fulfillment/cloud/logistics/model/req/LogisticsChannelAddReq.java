package com.amg.fulfillment.cloud.logistics.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by Seraph on 2021/5/24
 */

@Data
@ApiModel(value = "LogisticsChannelAddReq")
public class LogisticsChannelAddReq {

    @NotNull(message = "物流商 code 不能为空")
    @ApiModelProperty(value = "物流商 code")
    private String logisticsCode;
    @NotNull(message = "物流渠道 code 不能为空")
    @Size(max =  30)
    @ApiModelProperty(value = "物流渠道 code")
    private String channelCode;
    @NotNull(message = "物流渠道名称 不能为空")
    @Size(max =  30)
    @ApiModelProperty(value = "物流渠道名称")
    private String channelName;
    @ApiModelProperty(value = "是否禁用")
    private Integer isDisable;
}

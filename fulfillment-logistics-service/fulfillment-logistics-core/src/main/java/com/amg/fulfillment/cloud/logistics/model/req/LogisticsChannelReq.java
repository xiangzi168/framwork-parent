package com.amg.fulfillment.cloud.logistics.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by Seraph on 2021/5/24
 */

@Data
public class LogisticsChannelReq extends BaseReq implements Serializable {

    @ApiModelProperty(value = "物流商 模糊查询")
    private String logisticsProvider;
    @ApiModelProperty(value = "物流渠道 code 模糊查询")
    private String logisticsChannel;

    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "物流商 code")
    private String logisticsCode;
    @ApiModelProperty(value = "物流商名称")
    private String logisticsName;
    @ApiModelProperty(value = "物流渠道 code")
    private String channelCode;
    @ApiModelProperty(value = "物流渠道名称")
    private String channelName;
    @ApiModelProperty(value = "是否禁用    1 是    0 否")
    private Integer isDisable;
}

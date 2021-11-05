package com.amg.fulfillment.cloud.logistics.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by Seraph on 2021/5/25
 */

@Data
@ApiModel("LogisticsProviderVO")
public class LogisticsProviderVO {

    @ApiModelProperty("是否推荐")
    private Boolean recommendFlag = Boolean.FALSE;
    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "物流商 code")
    private String logisticsCode;
    @ApiModelProperty(value = "物流商 名称")
    private String logisticsName;
    @ApiModelProperty(value = "物流商 描述")
    private String logisticsDesc;
    @ApiModelProperty(value = "物流商 所关联的物流渠道")
    private List<LogisticsChannelVO> channelList;
}

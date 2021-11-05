package com.amg.fulfillment.cloud.logistics.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Seraph on 2021/6/1
 */

@Data
@ApiModel(value = "LogisticsTrackNodeVO")
public class LogisticsTrackNodeVO {

    @ApiModelProperty(value = "物流商")
    private String logisticsCode;
    @ApiModelProperty(value = "节点名称中文")
    private String nameEn;
    @ApiModelProperty(value = "节点名称英文")
    private String nameCn;
}

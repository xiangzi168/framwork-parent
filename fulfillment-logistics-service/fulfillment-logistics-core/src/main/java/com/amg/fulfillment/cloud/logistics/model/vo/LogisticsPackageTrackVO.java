package com.amg.fulfillment.cloud.logistics.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Seraph on 2021/5/13
 */

@Data
@ApiModel("LogisticsPackageTrackVO")
public class LogisticsPackageTrackVO {

    @ApiModelProperty(value = "节点名称")
    private String node;
    @ApiModelProperty(value = "中文释义")
    private String nodeDesc;
    @ApiModelProperty(value = "内部节点中文")
    private String insideCn;
    @ApiModelProperty(value = "内部节点英文")
    private String insideEn;
    @ApiModelProperty(value = "物流轨迹")
    private String content;
    @ApiModelProperty(value = "时区")
    private String timeZone;
    @ApiModelProperty(value = "时间")
    private String trackTime;

}

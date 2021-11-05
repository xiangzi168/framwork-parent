package com.amg.fulfillment.cloud.logistics.dto.logistic;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author Tom
 * @date 2021-04-20-9:54
 */
@Data
@Builder
public class TrackPointDto {

    @ApiModelProperty(name = "关键节点状态")
    private String status;
    @ApiModelProperty(name = "轨迹内容")
    private String content;
    @ApiModelProperty(name = "地点")
    private String location;
    @ApiModelProperty(name = "时区")
    private String timeZone;
    @ApiModelProperty(name = "轨迹时间")
    private String eventTime;
}

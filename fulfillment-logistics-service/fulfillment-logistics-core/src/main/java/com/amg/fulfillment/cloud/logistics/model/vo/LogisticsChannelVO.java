package com.amg.fulfillment.cloud.logistics.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created by Seraph on 2021/5/24
 */

@Data
@ApiModel(value = "LogisticsChannelVO")
public class LogisticsChannelVO {

    @ApiModelProperty("是否推荐")
    private Boolean recommendFlag = Boolean.FALSE;
    @ApiModelProperty(value = "物流渠道 id")
    private Long id;
    @ApiModelProperty(value = "物流商名称")
    private String logisticsName;
    @ApiModelProperty(value = "物流商 code")
    private String logisticsCode;
    @ApiModelProperty(value = "物流渠道名称")
    private String channelName;
    @ApiModelProperty(value = "物流渠道 code")
    private String channelCode;
    @ApiModelProperty(value = "是否禁用")
    private Integer isDisable;
    @ApiModelProperty(value = "操作记录")
    private String operationRecord;
    @ApiModelProperty(value = "最后修改时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;
}

package com.amg.fulfillment.cloud.logistics.api.dto.msg;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("物流变动通知")
public class LogisticsChangeMsgDto {
    @ApiModelProperty("物流商单号")
    private String logisticsWayBillNo;
    @ApiModelProperty("跟踪号")
    private String logisticsTrackingCode;
    @ApiModelProperty("包裹类型 1 境外发货单  2 AE 发货单")
    private Integer type;
    @ApiModelProperty("通知时间 -")
    private Date noticeTime;
}

package com.amg.fulfillment.cloud.logistics.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
@Data
@ApiModel(value = "LogisticsTrackingCodeUpdateReq")
public class LogisticsTrackingCodeUpdateReq {
    @NotNull(message = "物流标签商品 id 不能为空")
    @ApiModelProperty(value = "物流标签商品 id",required = true)
    private Long id;
    @NotNull(message = "类型不能为空")
    @ApiModelProperty(value = "类型: 1 境外 2 ae 4 cj ",required = true)
    private Integer type;
    @NotBlank(message = "渠道订单号不能为空")
    @ApiModelProperty(value = "渠道订单号",required = true)
    private String channelOrderId;
    @NotBlank(message = "新的物流轨迹号不能为空")
    @ApiModelProperty(value = "新的物流轨迹号",required = true)
    private String newLogisticsTrackingCode;
    @ApiModelProperty(value = "旧的物流轨迹号")
    private String oldLogisticsTrackingCode;
    @ApiModelProperty(value = "新的物流运单号")
    private String newLogisticsWaybillNo;
    @ApiModelProperty(value = "旧的物运单号")
    private String oldLogisticsWaybillNo;
}

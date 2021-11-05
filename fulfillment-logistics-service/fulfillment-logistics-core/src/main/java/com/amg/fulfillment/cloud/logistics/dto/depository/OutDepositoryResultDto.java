package com.amg.fulfillment.cloud.logistics.dto.depository;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Tom
 * @date 2021-04-12-10:49
 */
@ApiModel("出库结果对象")
@Data
public class OutDepositoryResultDto {
    @ApiModelProperty(value = "仓库码")
    private String deposityCode;
    @ApiModelProperty(value = "成功标志")
    private boolean successSign;
    @ApiModelProperty(value = "出库订单号")
    private String refId;
    @ApiModelProperty(value = "出库状态 Created: 已创建 Packed: 已打包Shipped: 已出库Cancelled: 已取消Failed: 出货失败")
    private String outStatus;
    @ApiModelProperty(value = "创建出库时间")
    private String createOutDate;

    @ApiModelProperty(value = "错误消息")
    private String errorMsg;
    @ApiModelProperty(value = "错误码")
    private String errorCode;
    @ApiModelProperty(value = "系统错误消息")
    private String systemErrorMsg;

    @ApiModelProperty(value = "申请取消时间")
    private String cancellRequestOn;
    @ApiModelProperty(value = "取消处理时间")
    private String cancellResponseOn;
    @ApiModelProperty(value = "取消状态 Created: 已创建 Packed: 已打包Shipped: 已出库Cancelled: 已取消Failed: 出货失败")
    private String cancellStatus;
    @ApiModelProperty(value = "申请返回消息")
    private String cancellMessage;

    private Object sourceData;
}

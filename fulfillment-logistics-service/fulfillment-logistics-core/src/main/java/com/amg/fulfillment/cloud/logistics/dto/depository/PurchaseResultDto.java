package com.amg.fulfillment.cloud.logistics.dto.depository;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Tom
 * @date 2021-04-15-15:06
 */
@ApiModel("采购结果对象")
@Data
public class PurchaseResultDto {
    @ApiModelProperty(name = "成功标记")
    private Boolean successSign;
    @ApiModelProperty(name = "采购号")
    private String purchaseNo;
    @ApiModelProperty(name = "客户代码")
    private String accountNo;
    @ApiModelProperty(name = "状态")
    private String status;
    @ApiModelProperty(name = "本系统仓库编码")
    private String deposityCode;
    @ApiModelProperty(name = "第三方仓库具体代码")
    private String warehouseCode;
    @ApiModelProperty(name = "采购原因")
    private String purchaseReason;
    @ApiModelProperty(name = "创建时间")
    private String createDate;
    @ApiModelProperty(name = "采购消息")
    private String purchaseMsg;
    @ApiModelProperty(name = "错误消息")
    private String errorMsg;
    @ApiModelProperty(name = "错误码")
    private String errorCode;
    @ApiModelProperty(name = "系统错误消息")
    private String systemErrorMsg;

}

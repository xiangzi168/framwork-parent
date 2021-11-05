package com.amg.fulfillment.cloud.logistics.dto.depository;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Seraph on 2021/5/27
 */

@Data
public abstract class AbstractDepositoryResponse {

    @ApiModelProperty(name = "仓库码")
    protected String deposityCode;
    @ApiModelProperty(name = "成功标志")
    protected Boolean successSign;
    @ApiModelProperty(name = "错误消息")
    protected String errorMsg;
    @ApiModelProperty(name = "错误码")
    protected String errorCode;
    @ApiModelProperty(name = "系统错误消息")
    protected String systemErrorMsg;
}

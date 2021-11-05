package com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Tom
 * @date 2021-04-23-18:51
 */
@Data
public class ResposeMsgToWanB {
    @ApiModelProperty("")
    private String Data;
    @ApiModelProperty("")
    private boolean Succeeded;
    @ApiModelProperty("")
    private Error Error;

    @Data
    public static class Error {
        private String Code;
        private String Message;
    }

    public static ResposeMsgToWanB success() {
        ResposeMsgToWanB resposeMsgToWanB = new ResposeMsgToWanB();
        resposeMsgToWanB.setSucceeded(Boolean.TRUE);
        return resposeMsgToWanB;
    }

    public static ResposeMsgToWanB fail(String code, String message) {
        ResposeMsgToWanB.Error error = new Error();
        error.setCode(code);
        if (StrUtil.isBlank(message)) {
            message = "系统出现异常，请第三方重发消息";
        }
        error.setMessage(message);
        ResposeMsgToWanB resposeMsgToWanB = new ResposeMsgToWanB();
        resposeMsgToWanB.setSucceeded(Boolean.FALSE);
        resposeMsgToWanB.setError(error);
        return resposeMsgToWanB;
    }


}

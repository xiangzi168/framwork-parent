package com.amg.fulfillment.cloud.logistics.enumeration;

import lombok.Getter;

@Getter
public enum ExceptionEnum {
    REQUEST_PARAM_VALIDAT_EXCEPTION("900001","请求参数验证失败"),
    LOGISTIC_REPONSE_EXCEPTION("900100","第三方物流返回下单错误异常"),
    RETURN_CODE_100603("100603","调用外部接口异常:")
    ;
    private String code;
    private String msg;

    ExceptionEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}

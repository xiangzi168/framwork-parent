package com.amg.fulfillment.cloud.logistics.enumeration;

import com.amg.framework.boot.base.enums.ResponseCodeEnumSupport;

/**
 * Created by Seraph on 2021/5/24
 */
public enum BaseLogisticsResponseCodeEnum implements ResponseCodeEnumSupport {

    RESPONSE_CODE_200100("200100","参数异常"),
    RESPONSE_CODE_200101("200101","操作异常"),
    ;

    private String code;
    private String msg;

    BaseLogisticsResponseCodeEnum(String code, String msg)
    {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }

}

package com.amg.framework.boot.base.enums;


/**
 * 系统基础响应码
 */
public enum ResponseCodeEnum implements ResponseCodeEnumSupport {

    /**
     * 定义返回码
     * */

    RETURN_CODE_100001("100001","用户名或密码错误"),
    RETURN_CODE_100400("100400","请求参数错误"),
    RETURN_CODE_100401("100401","权限不足"),
    RETURN_CODE_100403("100403","拒绝访问"),
    RETURN_CODE_100404("100404","请求资源不存在"),
    RETURN_CODE_100406("100406","未登录"),

    RETURN_CODE_100200("100200","请求成功"),
    RETURN_CODE_100500("100500","系统异常");


    private String code;
    private String msg;

    private ResponseCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}

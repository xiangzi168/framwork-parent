package com.amg.framework.boot.canal.enums;

/**
 * @author lyc
 * @date 2020/9/27 11:21
 * @describe
 */
public enum EventTypeEnum {
    INSERT(1,"INSERT"),UPDATE_BEFORE(2,"UPDATE_BEFORE"),UPDATE_AFTER(3,"UPDATE_AFTER"),DELETE(4,"DELETE");
    private int code;
    private String msg;

    EventTypeEnum(int code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}

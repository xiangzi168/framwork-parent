package com.amg.framework.boot.base.exception;


import com.amg.framework.boot.base.enums.ResponseCodeEnumSupport;

/**
 * 全局异常类
 * */
public class GlobalException extends RuntimeException {

    /**
     * 错误编码
     */
    private String errorCode;

    /**
     * 错误信息是否API翻译
     */
    private boolean translate = true;


    /**
     * 构造一个基本异常.
     *
     * @param errorCode
     *            错误编码
     * @param message
     *            信息描述
     */
    public GlobalException(String errorCode, String message) {
        this(errorCode, message, true);
    }

    public GlobalException(ResponseCodeEnumSupport responseCodeEnum) {
        this(responseCodeEnum.getCode(), responseCodeEnum.getMsg(), true);
    }

    public GlobalException(ResponseCodeEnumSupport responseCodeEnum, String msg) {
        this(responseCodeEnum.getCode(), msg, true);
    }

    /**
     * 构造一个基本异常.
     *
     * @param errorCode
     *            错误编码
     * @param message
     *            信息描述
     */
    public GlobalException(String errorCode, String message, Throwable cause) {
        this(errorCode, message, cause, true);
    }

    /**
     * 构造一个基本异常.
     *
     * @param errorCode
     *            错误编码
     * @param message
     *            信息描述
     * @param translate
     *            消息是否API翻译
     */
    public GlobalException(String errorCode, String message, boolean translate) {
        super(message);
        this.setErrorCode(errorCode);
        this.setTranslate(translate);
    }

    /**
     * 构造一个基本异常.
     *
     * @param errorCode
     *            错误编码
     * @param message
     *            信息描述
     */
    public GlobalException(String errorCode, String message, Throwable cause, boolean translate) {
        super(message, cause);
        this.setErrorCode(errorCode);
        this.setTranslate(translate);
    }

    /**
     * 构造一个基本异常.
     *
     * @param message
     *            信息描述
     * @param cause
     *            根异常类（可以存入任何异常）
     */
    public GlobalException(String message, Throwable cause){
        super(message, cause);
    }

    public String getErrorCode(){
        return errorCode;
    }

    public void setErrorCode(String errorCode){
        this.errorCode = errorCode;
    }

    public boolean getTranslate() {
        return translate;
    }

    public void setTranslate(boolean translate) {
        this.translate = translate;
    }
}

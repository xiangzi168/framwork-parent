package com.amg.framework.boot.base.model;


import com.amg.framework.boot.base.enums.ResponseCodeEnum;
import org.slf4j.MDC;

import java.io.Serializable;


/**
 * 返回消息实体类
 * */
public final class Result<T> implements Serializable {

	private static final long serialVersionUID = 8895746140709342879L;

	private String requestId; // 请求id

	private boolean success; // 是否成功

	private String errorCode; // 错误代码

	private String msg; // 返回信息

	private T data; // 封装返回数据


	private Result() {}

	private Result(T object) {
		this(ResponseCodeEnum.RETURN_CODE_100200, object);
	}

	private Result(ResponseCodeEnum responseCodeEnum, T object) {
		this(responseCodeEnum.getCode(), responseCodeEnum.getMsg(), object);
	}

	public Result(ResponseCodeEnum responseCodeEnum, String msg){
		this(responseCodeEnum.getCode(), msg, null);
	}

	public Result(ResponseCodeEnum responseCodeEnum){
		this(responseCodeEnum.getCode(), responseCodeEnum.getMsg(), null);
	}

	public Result(String errorCode, String msg){
		this(errorCode, msg, null);
	}

	private Result(String errorCode, String msg, T data) {
		this.requestId = MDC.get("requestId");
		this.errorCode = errorCode;
		this.msg = msg;
		this.data = data;
		if (ResponseCodeEnum.RETURN_CODE_100200.getCode().equals(this.errorCode)) {
			this.success = true;
		} else {
			this.success = false;
		}
	}

	public static <T> Result<T> create(T t) {
		return new Result(t);
	}

	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}

}
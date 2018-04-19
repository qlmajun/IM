package com.pft.communicate.common.dto;

import java.io.Serializable;

/****
 * rest接口返回统一数据封装
 * 
 * @author majun@12301.cc
 *
 */
public class ServiceResultDTO implements Serializable {

	private static final long serialVersionUID = 6409941669580057914L;

	/** 成功 **/
	public static final int SUCCESS = 200;

	/** 状态 **/
	private int code;

	/** 消息 **/
	private String msg;

	/** 数据 **/
	private Object data;

	private ServiceResultDTO() {

	}

	private ServiceResultDTO(int code, String msg, Object date) {
		this.code = code;
		this.msg = msg;
		this.data = date;
	}

	public static ServiceResultDTO success() {
		return success(null);
	}

	public static ServiceResultDTO success(Object data) {
		return new ServiceResultDTO(SUCCESS, "success", data);
	}

	public static ServiceResultDTO error(int code, String msg) {
		return new ServiceResultDTO(code, msg, null);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "ServiceResultDTO [code=" + code + ", msg=" + msg + ", data=" + data + "]";
	}
}

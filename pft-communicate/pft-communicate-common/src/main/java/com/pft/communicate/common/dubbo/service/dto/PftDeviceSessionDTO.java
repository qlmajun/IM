package com.pft.communicate.common.dubbo.service.dto;

import java.io.Serializable;

/****
 * 票付通会话传输信息存储
 * 
 * @author majun@12301.cc
 *
 */
public class PftDeviceSessionDTO implements Serializable {

	private static final long serialVersionUID = 5875531121601022983L;

	/** 设备Id **/
	private String deviceId;

	/** 客户端设备登入地址 **/
	private String clientAddress;

	/** 登入时间戳 **/
	private long startTimeStamp;

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getClientAddress() {
		return clientAddress;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public long getStartTimeStamp() {
		return startTimeStamp;
	}

	public void setStartTimeStamp(long startTimeStamp) {
		this.startTimeStamp = startTimeStamp;
	}

	@Override
	public String toString() {
		return "PftDeviceSessionDTO [deviceId=" + deviceId + ", clientAddress=" + clientAddress + ", startTimeStamp=" + startTimeStamp + "]";
	}
}

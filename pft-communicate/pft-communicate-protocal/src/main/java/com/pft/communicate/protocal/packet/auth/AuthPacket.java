package com.pft.communicate.protocal.packet.auth;

import com.pft.communicate.protocal.packet.BasicPftPacket;

/****
 * 认证报文
 * 
 * @author majun@12301.cc
 *
 */
public class AuthPacket extends BasicPftPacket {

	private static final long serialVersionUID = -6755558001029699562L;

	/** 设备Id **/
	private String deviceId;

	public AuthPacket() {
		super();
	}

	public AuthPacket(String deviceId) {
		super();
		this.deviceId = deviceId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((deviceId == null) ? 0 : deviceId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AuthPacket other = (AuthPacket) obj;
		if (deviceId == null) {
			if (other.deviceId != null)
				return false;
		} else if (!deviceId.equals(other.deviceId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AuthPacket [deviceId=" + deviceId + ", id=" + id + ", from=" + from + ", to=" + to + "]";
	}
}

package com.pft.communicate.protocal.packet.presence;

import com.pft.communicate.protocal.packet.BasicPftPacket;

/****
 * 上线报文
 * 
 * @author majun@12301.cc
 *
 */
public class PresencePacket extends BasicPftPacket {

	private static final long serialVersionUID = -6661981843844971097L;

	/** 设备Id **/
	private String deviceId;

	/** 状态 **/
	private Status status;

	public enum Status {

		/***
		 * 在线
		 */
		available,

		/***
		 * 离线
		 */
		unavailable;
	}

	public PresencePacket() {
		super();
	}

	public PresencePacket(String deviceId, Status status) {
		super();
		this.deviceId = deviceId;
		this.status = status;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((deviceId == null) ? 0 : deviceId.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		PresencePacket other = (PresencePacket) obj;
		if (deviceId == null) {
			if (other.deviceId != null)
				return false;
		} else if (!deviceId.equals(other.deviceId))
			return false;
		if (status != other.status)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PresencePacket [deviceId=" + deviceId + ", status=" + status + ", id=" + id + ", from=" + from + ", to=" + to + "]";
	}
}

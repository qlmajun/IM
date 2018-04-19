package com.pft.communication.server.session.impl;

import com.pft.communicate.protocal.packet.PFTPacket;
import com.pft.communication.server.session.PftDeviceSession;
import com.pft.communication.server.session.PresenceStatus;

/****
 * 设备连接的session对象操作接口实现
 * 
 * @author majun@12301.cc
 *
 */
public class PftDeviceSessionImpl implements PftDeviceSession {

	private static final long serialVersionUID = 7821611929184950967L;

	/** 设备在线状态信息 **/
	private PresenceStatus presenceStatus = new PresenceStatus();

	/** 设备Id **/
	private String deviceId;

	/** 开始时间戳 **/
	private long startTimestamp;

	public PftDeviceSessionImpl(String deviceId, long startTimestamp) {
		this.deviceId = deviceId;
		this.startTimestamp = startTimestamp < 1 ? System.currentTimeMillis() : startTimestamp;
	}

	@Override
	public String getDeviceId() {
		return deviceId;
	}

	@Override
	public PresenceStatus getPresence() {
		return presenceStatus;
	}

	@Override
	public void setPresence(PresenceStatus presence) {
		this.presenceStatus =  presence;
		persistent();
	}

	@Override
	public long getStartTimestamp() {
		return startTimestamp;
	}

	@Override
	public boolean close(boolean notifySessionManager, boolean sendStreamEndPacket) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean close(boolean notifySessionManager) {
		return close(notifySessionManager, true);
	}

	@Override
	public boolean deliver(PFTPacket packet) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void persistent() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getClientAddress() {
		return "127.0.0.1";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((deviceId == null) ? 0 : deviceId.hashCode());
		result = prime * result + ((presenceStatus == null) ? 0 : presenceStatus.hashCode());
		result = prime * result + (int) (startTimestamp ^ (startTimestamp >>> 32));
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
		PftDeviceSessionImpl other = (PftDeviceSessionImpl) obj;
		if (deviceId == null) {
			if (other.deviceId != null)
				return false;
		} else if (!deviceId.equals(other.deviceId))
			return false;
		if (presenceStatus == null) {
			if (other.presenceStatus != null)
				return false;
		} else if (!presenceStatus.equals(other.presenceStatus))
			return false;
		if (startTimestamp != other.startTimestamp)
			return false;
		return true;
	}
}

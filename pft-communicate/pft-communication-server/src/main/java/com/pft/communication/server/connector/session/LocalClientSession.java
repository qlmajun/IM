package com.pft.communication.server.connector.session;

import java.util.HashMap;
import java.util.Map;

import com.pft.communicate.protocal.packet.PFTPacket;
import com.pft.communication.server.connector.connection.ClientConnection;

public class LocalClientSession implements Session {

	private static final long serialVersionUID = -3089814401163091614L;

	protected static final String CHARSET = "UTF-8";

	protected String deviceName;

	protected SessionStatus status = SessionStatus.CONNECTED;

	protected ClientConnection conn;

	private long startDate;

	private long lastActiveDate;

	private final Map<String, Object> sessionData = new HashMap<String, Object>();

	private boolean initialized = false;

	public LocalClientSession(String deviceName, ClientConnection connection) {
		conn = connection;
		this.deviceName = deviceName;
		startDate = System.currentTimeMillis();
		conn.init(this);
		initialized = true;
	}

	public boolean isInitialized() {
		return initialized;
	}

	@Override
	public String getDeviceId() {
		return deviceName;
	}

	@Override
	public long getCreationDate() {
		return startDate;
	}

	@Override
	public long getLastActiveDate() {
		return lastActiveDate;
	}

	public void setSessionData(String key, Object value) {
		synchronized (sessionData) {
			sessionData.put(key, value);
		}
	}

	public Object getSessionData(String key) {
		synchronized (sessionData) {
			return sessionData.get(key);
		}
	}

	public void removeSessionData(String key) {
		synchronized (sessionData) {
			sessionData.remove(key);
		}
	}

	@Override
	public void closeOnFlush() {
		if (conn != null) {
			conn.closeOnFlush();
		}
	}

	@Override
	public void closeNow() {
		if (conn != null) {
			conn.closeNow();
		}
	}

	@Override
	public boolean isClosing() {
		if (getSessionData(Session.__CLOSING__) != null) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isClosed() {
		return conn.isClosed();
	}

	public ClientConnection getConnection() {
		return conn;
	}

	@Override
	public void deliver(PFTPacket packet) {

		if (packet == null) {
			return;
		}

		// TODO packet handler

		conn.deliver(packet);
	}

	@Override
	public SessionStatus getStatus() {
		return status;
	}

	@Override
	public void setStatus(SessionStatus status) {
		this.status = status;
	}
}

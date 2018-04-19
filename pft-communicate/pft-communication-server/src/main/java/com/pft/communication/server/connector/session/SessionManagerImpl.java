package com.pft.communication.server.connector.session;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pft.communicate.core.module.Module;
import com.pft.communicate.core.module.lifecyle.exception.LifeCycleException;
import com.pft.communication.server.PftCommuctionServerStarter;
import com.pft.communication.server.session.PftDeviceSessionManager;
import com.pft.communication.server.session.impl.PftDeviceSessionManagerImpl;

public class SessionManagerImpl extends Module implements SessionManager {

	private static final Logger logger = LoggerFactory.getLogger(SessionManagerImpl.class);

	/** key:deviceId,value:对应的ClientSession **/
	private Map<String, LocalClientSession> deviceSessions = new ConcurrentHashMap<String, LocalClientSession>(5000);

	private PftDeviceSessionManager pftDeviceSessionManager;

	@Override
	protected void doInit() throws LifeCycleException {

	}

	@Override
	protected void doStart() throws LifeCycleException {
		pftDeviceSessionManager = PftCommuctionServerStarter.getInstance().getModule(PftDeviceSessionManagerImpl.class);
	}

	@Override
	public boolean addSession(LocalClientSession session) {

		if (getLifeCycleState().ordinal() >= LifeCycleState.before_stop.ordinal()) {
			return false;
		}

		String deviceId = session.getDeviceId();

		// 获取旧的会话
		LocalClientSession oldSession = deviceSessions.get(deviceId);

		// check local session conflict，session不一样的时候进行冲突控制,剔除旧的session
		if (oldSession != null && !oldSession.equals(session)) {
			// TODO 发送冲突报文
			oldSession.closeOnFlush();
		}

		deviceSessions.put(deviceId, session);

		// add to remote session and check remote session conflict
		pftDeviceSessionManager.createClientSession(deviceId);

		return true;
	}

	@Override
	public Collection<LocalClientSession> getSessions() {
		return deviceSessions.values();
	}

	@Override
	public LocalClientSession getClientSession(String deviceId) {
		return deviceSessions.get(deviceId);
	}

	@Override
	public boolean removeSession(String deviceId, long timestamp) {

		if (logger.isDebugEnabled()) {
			logger.debug("session {} removed", deviceId);
		}

		LocalClientSession localClientSession = deviceSessions.get(deviceId);

		if (localClientSession == null) {
			return false;
		}

		deviceSessions.remove(deviceId);

		return destoryClientSession(localClientSession, timestamp);

	}

	private boolean destoryClientSession(LocalClientSession remove, long timestamp) {

		if (remove.isClosing()) {
			return true;
		}

		String deviceId = remove.getDeviceId();

		// 无效的操作, session创建于关闭之后
		if (remove != null && remove.getCreationDate() > timestamp) {
			deviceSessions.put(deviceId, remove);
			return false;
		}

		// remove from remote
		pftDeviceSessionManager.destoryClientSession(deviceId);

		logger.info("close client session with deviceId {}", deviceId);

		// close socket
		remove.setSessionData(Session.__CLOSING__, true);

		remove.closeOnFlush();

		return true;
	}

	@Override
	protected void doStop() throws LifeCycleException {
	}

	@Override
	public Set<String> getSessionDeviceIds() {
		return deviceSessions.keySet();
	}

}

package com.pft.communication.server.session.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pft.communicate.cache.redis.cache.impl.JedisHashCache;
import com.pft.communicate.core.cache.RedisCacheManager;
import com.pft.communicate.core.module.Module;
import com.pft.communicate.core.module.lifecyle.exception.LifeCycleException;
import com.pft.communication.server.PftCommuctionServerStarter;
import com.pft.communication.server.session.PftDeviceSession;
import com.pft.communication.server.session.PftDeviceSessionManager;

/****
 * 设备会话管理接口实现
 * 
 * @author majun@12301.cc
 *
 */
public class PftDeviceSessionManagerImpl extends Module implements PftDeviceSessionManager {

	private static final Logger logger = LoggerFactory.getLogger(PftDeviceSessionManagerImpl.class);

	/** 设备会话缓存：key:deviceId,value:PftDeviceSession **/
	private JedisHashCache<String, PftDeviceSession> clientSessionCache;

	@Override
	protected void doInit() throws LifeCycleException {

		RedisCacheManager redisCacheManager = PftCommuctionServerStarter.getInstance().getModule(RedisCacheManager.class);

		clientSessionCache = redisCacheManager.getCacheFactory().createSimpleHashCache("pft_device_session");

	}

	@Override
	protected void doStart() throws LifeCycleException {

	}

	@Override
	protected void doStop() throws LifeCycleException {

	}

	@Override
	public boolean createClientSession(String deviceId) {

		PftDeviceSession oldSession = getClientSession(deviceId);

		if (oldSession != null) {
			oldSession.close(true);
		}

		PftDeviceSession session = new PftDeviceSessionImpl(deviceId, System.currentTimeMillis());

		clientSessionCache.put(deviceId, session);

		return true;
	}

	@Override
	public boolean destoryClientSession(String deviceId) {

		PftDeviceSession clientSession = getClientSession(deviceId);

		if (clientSession == null) {
			logger.warn("session not  exists with deviceId  when destory with server session", deviceId);
			return false;
		}

		return clientSessionCache.remove(deviceId);
	}

	@Override
	public void persistent(PftDeviceSession session) {

		String deviceId = session.getDeviceId();

		clientSessionCache.put(deviceId, session);
	}

	@Override
	public boolean isClientSessionExists(String deviceId) {

		if (StringUtils.isEmpty(deviceId)) {
			return false;
		}

		return clientSessionCache.exists(deviceId);
	}

	@Override
	public PftDeviceSession getClientSession(String deviceId) {
		return clientSessionCache.get(deviceId);
	}

	@Override
	public boolean isDeviceAvailable(String deviceId) {
		return clientSessionCache.exists(deviceId);
	}

}

package com.pft.communication.server.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.pft.communicate.protocal.packet.auth.AuthPacket;
import com.pft.communication.server.PftCommuctionServerStarter;
import com.pft.communication.server.connector.session.LocalClientSession;
import com.pft.communication.server.connector.session.SessionManager;

/****
 * 认证报文处理器
 * 
 * @author majun@12301.cc
 *
 */
public class AuthPacketHandler extends PacketHandler<AuthPacket> {

	private static final Logger logger = LoggerFactory.getLogger(AuthPacketHandler.class);

	private SessionManager sessionManager = PftCommuctionServerStarter.getInstance().getModule(SessionManager.class);

	@Override
	public boolean process(LocalClientSession clientSession, AuthPacket packet) {

		logger.info("receive auth packet : " + JSON.toJSONString(packet, true));

		String deviceId = packet.getDeviceId();

		// 保存session
		sessionManager.addSession(clientSession);

		reply(clientSession, new AuthPacket(deviceId));

		return true;
	}

}

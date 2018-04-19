package com.pft.communication.server.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.pft.communicate.protocal.packet.presence.PresencePacket;
import com.pft.communication.server.connector.session.LocalClientSession;

/****
 * 上线报文处理器
 * 
 * @author majun@12301.cc
 *
 */
public class PresencePacketHandler extends PacketHandler<PresencePacket> {

	private static final Logger logger = LoggerFactory.getLogger(PresencePacketHandler.class);

	@Override
	public boolean process(LocalClientSession clientSession, PresencePacket packet) {

		logger.info("receive presencePacket :" + JSON.toJSONString(packet, true));

		reply(clientSession, packet);

		return true;
	}

}

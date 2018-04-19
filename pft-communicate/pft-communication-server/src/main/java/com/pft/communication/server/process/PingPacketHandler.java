package com.pft.communication.server.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.pft.communicate.protocal.packet.ping.PingPacket;
import com.pft.communication.server.connector.session.LocalClientSession;

/****
 * 心跳报文处理类
 * 
 * @author majun@12301.cc
 *
 */
public class PingPacketHandler extends PacketHandler<PingPacket> {

	private static final Logger logger = LoggerFactory.getLogger(PingPacketHandler.class);

	@Override
	public boolean process(LocalClientSession clientSession, PingPacket packet) {

		logger.info("receive ping packet:" + JSON.toJSONString(packet, true));

		reply(clientSession, packet);

		return true;
	}

}

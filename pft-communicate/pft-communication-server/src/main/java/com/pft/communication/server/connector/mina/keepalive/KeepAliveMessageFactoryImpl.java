package com.pft.communication.server.connector.mina.keepalive;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.pft.communicate.protocal.OpcodeMapper;
import com.pft.communicate.protocal.packet.Header;
import com.pft.communicate.protocal.packet.PFTPacket;
import com.pft.communicate.protocal.packet.Header.OpCode;
import com.pft.communicate.protocal.packet.ping.PingPacket;
import com.pft.communicate.protocal.parser.ParserFactory;

/****
 * 心跳工厂实现，被动型心跳机制服务器在接受到客户端连接以后被动接受心跳请求，当在规定时间内没有收到客户端心跳请求时 将客户端连接关闭
 * 
 * @author majun@12301.cc
 *
 */
public class KeepAliveMessageFactoryImpl implements KeepAliveMessageFactory {

	private static final Logger logger = LoggerFactory.getLogger(KeepAliveMessageFactoryImpl.class);

	@Override
	public boolean isRequest(IoSession session, Object message) {

		if (message instanceof Byte) {

			byte[] packetByte = (byte[]) message;

			// 获取OpCode值
			OpCode opcode = Header.getOpcodeFromPacket(packetByte);

			logger.info(" server is request :" + OpcodeMapper.getPacketByOpCode(opcode.bytes()).getName());

			if (OpCode.PING.equals(opcode)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isResponse(IoSession session, Object message) {
		// 由于被动型心跳机制，没有请求当然也就不关注反馈 因此直接返回false
		return false;
	}

	@Override
	public Object getRequest(IoSession session) {
		// 被动型心跳机制无请求 因此直接返回null
		return null;
	}

	@Override
	public Object getResponse(IoSession session, Object request) {

		// 根据心跳请求request 反回一个心跳反馈消息
		PingPacket pingPacket = new PingPacket();

		logger.info("server response packet is :" + JSON.toJSONString(pingPacket, true));

		return ParserFactory.getDefaultEncoder().encode((PFTPacket) pingPacket);
	}

}

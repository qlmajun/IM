package com.pft.communication.client.keepalive;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;

import com.pft.communicate.protocal.packet.Header;
import com.pft.communicate.protocal.packet.Header.OpCode;
import com.pft.communicate.protocal.packet.PFTPacket;
import com.pft.communicate.protocal.packet.ping.PingPacket;
import com.pft.communicate.protocal.parser.ParserFactory;

/****
 * 客户端心跳工厂实现，客户端会定时发送心跳请求（注意定时时间必须小于，服务器端的IDLE监控时间）,同时需要监听心跳反馈，以此来判断是否与服务器丢失连接。对于服务器的心跳请求不给与反馈
 * 
 * @author majun@12301.cc
 *
 */
public class ClientKeepAliveMessageFactoryImpl implements KeepAliveMessageFactory {

	@Override
	public boolean isRequest(IoSession session, Object message) {
		// 服务器不会给客户端发送请求包，因此不关注请求包，直接返回false
		return false;
	}

	@Override
	public boolean isResponse(IoSession session, Object message) {

		// 客户端关注请求反馈，因此判断mesaage是否是反馈包
		byte[] packetByte = (byte[]) message;

		// 获取OpCode值
		OpCode opcode = Header.getOpcodeFromPacket(packetByte);

		if (OpCode.PING.equals(opcode)) {
			return true;
		}

		return false;
	}

	@Override
	public Object getRequest(IoSession session) {
		// 根据心跳请求request 反回一个心跳反馈消息
		PingPacket pingPacket = new PingPacket();
		return ParserFactory.getDefaultEncoder().encode((PFTPacket) pingPacket);
	}

	@Override
	public Object getResponse(IoSession session, Object request) {
		// 服务器不会给客户端发送心跳请求，客户端当然也不用反馈 该方法返回null
		return null;
	}

}

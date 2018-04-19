package com.pft.communication.client.handler;

import java.net.InetSocketAddress;
import java.util.Arrays;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.SocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.pft.communicate.protocal.packet.Header;
import com.pft.communicate.protocal.packet.Header.OpCode;
import com.pft.communicate.protocal.packet.auth.AuthPacket;
import com.pft.communicate.protocal.packet.error.ErrorPacket;
import com.pft.communicate.protocal.packet.message.MessagePacket;
import com.pft.communicate.protocal.packet.message.MessageReceiptsPacket;
import com.pft.communicate.protocal.packet.ping.PingPacket;
import com.pft.communicate.protocal.packet.presence.PresencePacket;
import com.pft.communicate.protocal.parser.ParserFactory;

public class ClientHandler implements IoHandler {

	private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

	private SocketConnector connector;

	private String host;

	private int port = 5222;

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		logger.info("客户端创建session连接!");
		System.out.println("客户端创建session连接");
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		logger.info("客户端开始session连接!");
		System.out.println("客户端开始session连接!");
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {

		logger.info("客户端session连接断开!");

		System.out.println("客户端session连接断开!");

		while (true) {

			Thread.sleep(3000);

			// 这里是异步操作 连接后立即返回
			ConnectFuture future = connector.connect(new InetSocketAddress(host, port));

			// 等待连接创建完成
			future.awaitUninterruptibly();

			session = future.getSession();

			if (session.isConnected()) {
				logger.info("======>>>session 重连成功");
				break;
			}

			logger.warn("session 断线重连中......");

		}
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		logger.info("客户端session空闲!");
		System.out.println("客户端session空闲!");
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		logger.info("程序出现异常" + cause);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {

		byte[] packetByte = (byte[]) message;

		// 获取OpCode值
		OpCode opcode = Header.getOpcodeFromPacket(packetByte);

		if (OpCode.AUTH.equals(opcode)) {

			// 解析成JumpPacket
			AuthPacket authPacket = (AuthPacket) ParserFactory.getDefaultDecoder().decode(packetByte);

			if (authPacket == null) {
				logger.error("invalid auth packet with bytes " + Arrays.toString(packetByte));
			}

			System.out.println(authPacket.toString());

			String deviceId = authPacket.getDeviceId();

			// 发送上线报文
			PresencePacket presencePacket = new PresencePacket(deviceId, PresencePacket.Status.available);

			session.write(presencePacket);
		}

		else if (OpCode.PRESENCE.equals(opcode)) {

			PresencePacket presencePacket = (PresencePacket) ParserFactory.getDefaultDecoder().decode(packetByte);

			if (presencePacket == null) {
				logger.error("invalid presence packet with bytes " + Arrays.toString(packetByte));
			}

			System.out.println(presencePacket.toString());
		}

		else if (OpCode.MESSAGERECEIPTS.equals(opcode)) {

			MessageReceiptsPacket messageReceipts = (MessageReceiptsPacket) ParserFactory.getDefaultDecoder().decode(packetByte);

			if (messageReceipts == null) {
				logger.error("invalid message receipts  packet with bytes " + Arrays.toString(packetByte));
			}

			System.out.println(messageReceipts.toString());
		}

		else if (OpCode.MESSAGE.equals(opcode)) {
			MessagePacket messagePacket = (MessagePacket) ParserFactory.getDefaultDecoder().decode(packetByte);
			System.out.println("clinet receive message packet :" + JSONObject.toJSONString(messagePacket, true));
		}

		else if (OpCode.PING.equals(opcode)) {
			PingPacket pingPacket = (PingPacket) ParserFactory.getDefaultDecoder().decode(packetByte);
			System.out.println("clinet receive ping packet :" + JSONObject.toJSONString(pingPacket, true));
		}

		if (OpCode.ERROR.equals(opcode)) {
			ErrorPacket errorPacket = (ErrorPacket) ParserFactory.getDefaultDecoder().decode(packetByte);
			System.out.println(errorPacket.toString());
		}
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {

	}

	@Override
	public void inputClosed(IoSession session) throws Exception {
		// TODO Auto-generated method stub

	}

}

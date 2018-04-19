package com.pft.communication.server.connector.mina;

import java.io.IOException;
import java.util.Arrays;

import javax.net.ssl.SSLHandshakeException;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pft.communicate.protocal.packet.Header;
import com.pft.communicate.protocal.packet.Header.OpCode;
import com.pft.communicate.protocal.packet.PFTPacket;
import com.pft.communicate.protocal.packet.auth.AuthPacket;
import com.pft.communicate.protocal.parser.ParserFactory;
import com.pft.communication.server.PftCommuctionServerStarter;
import com.pft.communication.server.common.exception.PftPacketIllegalStateException;
import com.pft.communication.server.connector.session.ClientSocketConnection;
import com.pft.communication.server.connector.session.LocalClientSession;
import com.pft.communication.server.connector.session.SessionManager;
import com.pft.communication.server.connector.session.SessionManagerImpl;
import com.pft.communication.server.module.PacketDispatcherManager;

public class MinaConnectionEventHandler extends IoHandlerAdapter {

	private Logger logger = LoggerFactory.getLogger(MinaConnectionEventHandler.class);

	public static final String SESSSION_STATUS_INIT = "_INIT";
	public static final String ATTR_TCP_CHANNEL = "_channel";

	public static final String SESSION = "__SESSION__";
	public static final String CONNECTION = "__CONNECTION__";

	private SessionManager sessionManager = PftCommuctionServerStarter.getInstance().getModule(SessionManagerImpl.class);

	private PacketDispatcherManager packetDispatcherManager = PftCommuctionServerStarter.getInstance().getModule(PacketDispatcherManager.class);

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		super.sessionCreated(session);
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		session.setAttribute(SESSSION_STATUS_INIT, Boolean.TRUE);

		// 添加到preauth监控
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		super.sessionClosed(session);

		LocalClientSession clientSession = getClientSessionFromIoSession(session);

		if (clientSession == null) {
			return;
		}

		long peroid = System.currentTimeMillis() - session.getCreationTime();
		logger.info("session closed {} with connect peroid {}s", clientSession.getDeviceId(), peroid / 1000);

		// cache of session already cleaned
		if (clientSession.isClosing()) {
			return;
		}

		// cache of session exists, need to remove from local and remote
		sessionManager.removeSession(clientSession.getDeviceId(), System.currentTimeMillis());
	}

	private LocalClientSession getClientSessionFromIoSession(IoSession session) {
		Object localClientSession = session.getAttribute(MinaConnectionEventHandler.SESSION);
		if (localClientSession == null) {
			return null;
		}

		return (LocalClientSession) localClientSession;
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {

		long peroid = System.currentTimeMillis() - session.getCreationTime();

		if (peroid < 24 * 60 * 60 * 1000) {
			return;
		}

		LocalClientSession clientSession = getClientSessionFromIoSession(session);

		if (clientSession == null) {
			logger.warn("session idle to be closed, but session is Null");
			return;
		}

		logger.info("session idle to be closed {} with connect peroid {}s", clientSession.getDeviceId(), peroid / 1000);
		session.closeNow();
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {

		Object attribute = session.getAttribute(MinaConnectionEventHandler.SESSION);
		String address = "";
		if (attribute != null) {
			address = ((LocalClientSession) attribute).getDeviceId();
		}

		if (cause instanceof IOException) {
			logger.warn("IOException caught when process in mina iosession {}, {}", address, cause.getMessage());
		} else if (cause instanceof org.apache.mina.filter.codec.ProtocolDecoderException) {
			logger.warn("ProtocolDecoderException caught when process in mina iosession {}, {}", address, cause.getMessage());
		} else if (cause instanceof SSLHandshakeException) {
			logger.warn("SSLHandshakeException caught when process in mina iosession, {}", address, cause.getMessage());
		} else if (cause instanceof IllegalArgumentException) {
			logger.warn("IllegalArgumentException caught when process in mina iosession {}, {}", address, cause.getMessage());
		} else {
			logger.error("Exception caught when process in mina iosession {}", address, cause);
		}

		// if (cause instanceof InvalidJumpPacketException) {
		// session.write(new StreamErrorPacket(40001, "invalid packet, Only Jump
		// Packet supported"));
		// }

		session.closeOnFlush();
	}

	@Override
	public void messageReceived(IoSession ioSession, Object message) throws Exception {
		byte[] messageByte = (byte[]) message;
		// 处理消息字节数组
		handleMessageByte(messageByte, ioSession);
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		super.messageSent(session, message);
	}

	/****
	 * 处理字节数组消息，包含:13字节的消息头和消息体
	 *
	 * @param messageByte
	 */
	private void handleMessageByte(byte[] packetByte, IoSession ioSession) {

		// 获取OpCode值
		OpCode opcode = Header.getOpcodeFromPacket(packetByte);

		if (OpCode.AUTH.equals(opcode)) {

			// 解析成JumpPacket
			AuthPacket authPacket = (AuthPacket) ParserFactory.getDefaultDecoder().decode(packetByte);

			if (authPacket == null) {
				logger.error("invalid auth packet with bytes " + Arrays.toString(packetByte));
				throw new PftPacketIllegalStateException("auth packet invalid, packet error");
			}

			handleAuthPacket(authPacket, ioSession);
		}

		LocalClientSession localClientSession = (LocalClientSession) ioSession.getAttribute(MinaConnectionEventHandler.SESSION);

		if (localClientSession == null) {
			logger.warn("Invalid Packet, can not process packet before authentication packet received");
			ioSession.closeNow();
			return;
		}

		PFTPacket packet = ParserFactory.getDefaultDecoder().decode(packetByte);

		packetDispatcherManager.process(localClientSession, packet);
	}

	/***
	 * 处理认证报文
	 *
	 * @param authPacket
	 * @param session
	 */
	private void handleAuthPacket(AuthPacket authPacket, IoSession ioSession) {

		if (ioSession.getAttribute(MinaConnectionEventHandler.SESSSION_STATUS_INIT) == null) {
			throw new PftPacketIllegalStateException("duplicate auth packet");
		}

		String deviceId = authPacket.getDeviceId();

		ClientSocketConnection connnection = new ClientSocketConnection(ioSession);

		LocalClientSession localClientSession = new LocalClientSession(deviceId, connnection);

		ioSession.setAttribute(MinaConnectionEventHandler.CONNECTION, connnection);

		ioSession.setAttribute(MinaConnectionEventHandler.SESSION, localClientSession);
	}

}

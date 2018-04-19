package com.pft.communication.server.process;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.pft.communicate.protocal.packet.error.ErrorPacket;
import com.pft.communicate.protocal.packet.message.MessagePacket;
import com.pft.communicate.protocal.packet.message.MessageReceiptsPacket;
import com.pft.communication.server.PftCommuctionServerStarter;
import com.pft.communication.server.connector.session.LocalClientSession;
import com.pft.communication.server.connector.session.Session;
import com.pft.communication.server.connector.session.SessionManager;

/****
 * 消息报文处理器
 * 
 * @author majun@12301.cc
 *
 */
public class MessagePacketHandler extends PacketHandler<MessagePacket> {

	private static final Logger logger = LoggerFactory.getLogger(MessagePacketHandler.class);

	private SessionManager sessionManager = PftCommuctionServerStarter.getInstance().getModule(SessionManager.class);

	@Override
	public boolean process(LocalClientSession clientSession, MessagePacket packet) {

		// 使用服务器端的时间替换客户端的时间
		long currentTime = System.currentTimeMillis();

		packet.setDateLine(currentTime);

		logger.info("server receive message packet : {}", JSON.toJSONString(packet, true));

		// 转发报文
		boolean success = handlerMessage(clientSession, packet);

		// 发送失败
		if (!success) {
			return false;
		}

		if (!packet.isReceipts()) {
			return true;
		}

		// 发送消息回执报文
		MessageReceiptsPacket receipts = new MessageReceiptsPacket(packet.getId());

		receipts.setDateline(currentTime);

		clientSession.deliver(receipts);

		return true;
	}

	/****
	 * 转发消息
	 * 
	 * @param clientSession
	 *            客户端session
	 * @param messagePacket
	 *            消息报文
	 * @return
	 */
	private boolean handlerMessage(LocalClientSession clientSession, MessagePacket messagePacket) {

		String to = messagePacket.getTo();

		if (StringUtils.isEmpty(to)) {
			logger.warn("can  not send message , receive device id  is empty");
			ErrorPacket errorPacket = new ErrorPacket(messagePacket.getId(), 40301, "can not send message has an empty to field");
			clientSession.deliver(errorPacket);
			return false;
		}

		Session session = sessionManager.getClientSession(to);

		if (session == null) {
			logger.warn("can not send message , not fund  session ");
			ErrorPacket errorPacket = new ErrorPacket(messagePacket.getId(), 40301, "can not send message , not fund  session");
			clientSession.deliver(errorPacket);
			return false;
		}

		session.deliver(messagePacket);

		return true;
	}

}

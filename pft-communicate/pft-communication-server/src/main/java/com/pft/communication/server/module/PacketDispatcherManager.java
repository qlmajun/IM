package com.pft.communication.server.module;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.pft.communicate.core.module.Module;
import com.pft.communicate.core.module.lifecyle.exception.LifeCycleException;
import com.pft.communicate.protocal.packet.PFTPacket;
import com.pft.communicate.protocal.packet.auth.AuthPacket;
import com.pft.communicate.protocal.packet.message.MessagePacket;
import com.pft.communicate.protocal.packet.ping.PingPacket;
import com.pft.communicate.protocal.packet.presence.PresencePacket;
import com.pft.communication.server.connector.session.LocalClientSession;
import com.pft.communication.server.process.AuthPacketHandler;
import com.pft.communication.server.process.MessagePacketHandler;
import com.pft.communication.server.process.PacketHandler;
import com.pft.communication.server.process.PingPacketHandler;
import com.pft.communication.server.process.PresencePacketHandler;

/***
 * 处理接收客户端发来的报文
 * 
 * @author majun@12301.cc
 *
 */
public class PacketDispatcherManager extends Module {

	private Logger logger = LoggerFactory.getLogger(PacketDispatcherManager.class);

	/** 报文处理映射关系 **/
	private Map<Class<? extends PFTPacket>, PacketHandler<?>> packetHandlerMappers = null;

	@Override
	protected void doInit() throws LifeCycleException {
		packetHandlerMappers = new ConcurrentHashMap<Class<? extends PFTPacket>, PacketHandler<?>>();
	}

	@Override
	protected void doStart() throws LifeCycleException {
		registerPacketClazz();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean process(LocalClientSession clientSession, PFTPacket packet) {

		Class<? extends PFTPacket> clazz = packet.getClass();

		if (packetHandlerMappers.containsKey(clazz)) {
			PacketHandler packetHandler = packetHandlerMappers.get(clazz);
			return packetHandler.process(clientSession, packet);
		}

		logger.error("unsupport packet {} with content:\n {}", packet.getClass().getName(), JSON.toJSONString(packet, true));

		return false;
	}

	private void registerPacketClazz() {
		packetHandlerMappers.put(PingPacket.class, new PingPacketHandler());
		packetHandlerMappers.put(AuthPacket.class, new AuthPacketHandler());
		packetHandlerMappers.put(PresencePacket.class, new PresencePacketHandler());
		packetHandlerMappers.put(MessagePacket.class, new MessagePacketHandler());
	}

	@Override
	protected void doStop() throws LifeCycleException {
		logger.info("====PacketDispatcherManager==>>doStop.......");
	}

}

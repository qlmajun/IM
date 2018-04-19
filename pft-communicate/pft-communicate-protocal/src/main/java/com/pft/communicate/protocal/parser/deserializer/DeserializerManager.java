package com.pft.communicate.protocal.parser.deserializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pft.communicate.protocal.packet.Header.OpCode;
import com.pft.communicate.protocal.packet.PFTPacket;
import com.pft.communicate.protocal.packet.auth.AuthPacket;
import com.pft.communicate.protocal.packet.error.ErrorPacket;
import com.pft.communicate.protocal.packet.message.MessagePacket;
import com.pft.communicate.protocal.packet.message.MessageReceiptsPacket;
import com.pft.communicate.protocal.packet.presence.PresencePacket;
import com.pft.communicate.protocal.parser.deserializer.common.CommonDeserializerManager;

/****
 * 反序列化管理类
 * 
 * @author majun@12301.cc
 *
 */
public class DeserializerManager {

	private static Map<OpCode, Deserializer> deserializers = new HashMap<OpCode, Deserializer>();

	private static DeserializerManager instance = new DeserializerManager();

	private CommonDeserializerManager manager = CommonDeserializerManager.getInstance();

	public static DeserializerManager getInstance() {
		return instance;
	}

	private DeserializerManager() {

		registerDeserializer(PingDeserializer.class);

		List<Class<? extends PFTPacket>> packetClasses = new ArrayList<Class<? extends PFTPacket>>();

		packetClasses.add(AuthPacket.class);

		packetClasses.add(PresencePacket.class);

		packetClasses.add(MessagePacket.class);

		packetClasses.add(MessageReceiptsPacket.class);

		packetClasses.add(ErrorPacket.class);

		for (Class<? extends PFTPacket> clazz : packetClasses) {
			registerDeserializer(new DeserializerAdapter(clazz));
		}

	}

	/**
	 * 根据opcode返回对应的反序列化对象
	 *
	 * @param opcode
	 * @return
	 */
	public Deserializer getDeserializer(OpCode opcode) {
		return deserializers.get(opcode);
	}

	/**
	 * 注册反序列化处理类，处理类需要实现Deserializer接口，拥有自己的opCode
	 *
	 * @param deserializer
	 * @return 如果是新增处理类，返回true, 如果替换了已有的opcode对应的opcode，返回false.
	 */
	public boolean registerDeserializer(Class<? extends Deserializer> clazz) {
		try {
			return registerDeserializer(clazz.newInstance());
		} catch (InstantiationException e) {
			throw new IllegalArgumentException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * 注册反序列化处理类，处理类需要实现Deserializer接口，拥有自己的opCode
	 *
	 * @param deserializer
	 * @return 如果是新增处理类，返回true, 如果替换了已有的opcode对应的opcode，返回false.
	 */
	public boolean registerDeserializer(Deserializer deserializer) {

		if (deserializer.getOpCode() != null) {
			boolean conflict = deserializers.put(deserializer.getOpCode(), deserializer) != null;
			return !conflict;
		}
		throw new IllegalArgumentException("invalid op code");
	}

	/**
	 * 注销反序列化处理类
	 *
	 * @param opcode
	 * @return 注销成功，返回true, 对应opcode的序列化类不存在，则返回false.
	 */
	public boolean unregisterDeserializer(OpCode opcode) {
		throw new IllegalArgumentException("invalid op code");
	}

	public CommonDeserializerManager getCommonDeserializerManager() {
		return manager;
	}

}

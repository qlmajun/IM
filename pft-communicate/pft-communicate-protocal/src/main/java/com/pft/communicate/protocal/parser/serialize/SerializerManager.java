package com.pft.communicate.protocal.parser.serialize;

import java.util.HashMap;
import java.util.Map;

import com.pft.communicate.protocal.packet.PFTPacket;
import com.pft.communicate.protocal.packet.ping.PingPacket;

/****
 * 序列化管理类
 * 
 * @author majun@12301.cc
 *
 */
public class SerializerManager {

	private static Map<Class<? extends PFTPacket>, Serializer> serializers;

	private static SerializerManager instance = new SerializerManager();

	private static Serializer defaultSerializer = new DefaultSerializer();

	private SerializerManager() {
		serializers = new HashMap<Class<? extends PFTPacket>, Serializer>();

		registerSerializer(PingPacket.class, PingSerializer.class);
	}

	public static SerializerManager getInstance() {
		return instance;
	}

	/***
	 * 获取报文对应的序列化，如果没有注册使用默认的序列化
	 * 
	 * @param packet
	 * @return
	 */
	public static Serializer getSerializer(PFTPacket packet) {
		Serializer serializer = serializers.get(packet.getClass());
		return serializer == null ? defaultSerializer : serializer;
	}

	/**
	 * 注册反序列化处理类，处理类需要实现Deserializer接口，拥有自己的opCode
	 * 
	 * @param deserializer
	 * @return 如果是新增处理类，返回true, 如果替换了已有的opcode对应的opcode，返回false.
	 */
	public boolean registerSerializer(Class<? extends PFTPacket> clazz, Class<? extends Serializer> serializerClazz) {
		try {
			return registerSerializer(clazz, serializerClazz.newInstance());
		} catch (InstantiationException e) {
			throw new IllegalArgumentException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * 注册反序列化处理类，处理类需要实现Deserializer接口，拥有自己的opCode
	 * 
	 * @param serializer
	 * @return 如果是新增处理类，返回true, 如果替换了已有的opcode对应的opcode，返回false.
	 */
	public boolean registerSerializer(Class<? extends PFTPacket> clazz, Serializer serializer) {
		if (clazz != null) {
			return serializers.put(clazz, serializer) == null;
		}
		throw new IllegalArgumentException("invalid op code");
	}

	/**
	 * 注销反序列化处理类
	 * 
	 * @param opcode
	 * @return 注销成功，返回true, 对应opcode的序列化类不存在，则返回false.
	 */
	public boolean unregisterSerializer(byte[] opcode) {
		if (opcode != null && opcode.length == 2) {
			return serializers.remove(opcode) == null;
		}
		throw new IllegalArgumentException("invalid op code");
	}

}

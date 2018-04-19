package com.pft.communicate.protocal.parser;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ParserFactory {

	private static Map<Class<? extends PacketDecoder>, PacketDecoder> decoders = new ConcurrentHashMap<Class<? extends PacketDecoder>, PacketDecoder>();
	private static Map<Class<? extends PacketEncoder>, PacketEncoder> encoders = new ConcurrentHashMap<Class<? extends PacketEncoder>, PacketEncoder>();

	/**
	 * 获取指定类型的packet解析对象。 如果工厂中没有创建过，则会尝试创建, 创建失败返回null；如果有则直接返回parser对象
	 *
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends PacketDecoder> T getDecoder(Class<T> clazz) {
		if (decoders.get(clazz) == null) {
			try {
				T parser = clazz.newInstance();
				decoders.put(clazz, parser);
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
		}
		return (T) decoders.get(clazz);
	}

	/**
	 * 获取指定类型的packet解析对象。 如果工厂中没有创建过，则会尝试创建, 创建失败返回null；如果有则直接返回parser对象
	 *
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends PacketEncoder> T getEncoder(Class<T> clazz) {
		if (encoders.get(clazz) == null) {
			try {
				T parser = clazz.newInstance();
				encoders.put(clazz, parser);
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
		}
		return (T) encoders.get(clazz);
	}

	public static PacketDecoder getDefaultDecoder() {
		return getDecoder(DefaultPacketDecoder.class);
	}

	public static PacketEncoder getDefaultEncoder() {
		return getEncoder(DefaultPacketEncoder.class);
	}

}

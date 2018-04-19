package com.pft.communicate.protocal.parser.deserializer.common;


public class CommonDeserializerManager {

	private CommonDeserializer deserializer;

	private static CommonDeserializerManager instance = new CommonDeserializerManager();

	public static CommonDeserializerManager getInstance() {
		return instance;
	}

	public void setDefaultCommonDeserializer(CommonDeserializer deserializer) {
		this.deserializer = deserializer;
	}

	public CommonDeserializer getCommonDeserializer() {
		if (deserializer == null) {
			deserializer = new FastJSONDeserializer();
		}
		return deserializer;
	}

}

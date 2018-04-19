package com.pft.communicate.protocal.parser.deserializer.common;

import com.alibaba.fastjson.JSON;
import com.pft.communicate.protocal.packet.PFTPacket;

public class FastJSONDeserializer implements CommonDeserializer{

	@Override
	public <T extends PFTPacket> T deserialize(String body, Class<T> clazz) {
		return JSON.parseObject(body, clazz);
	}
}

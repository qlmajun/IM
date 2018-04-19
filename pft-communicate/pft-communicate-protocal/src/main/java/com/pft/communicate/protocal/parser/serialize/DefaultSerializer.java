package com.pft.communicate.protocal.parser.serialize;

import com.alibaba.fastjson.JSON;
import com.pft.communicate.protocal.packet.PFTPacket;

/****
 * 默认序列化实现
 * 
 * @author majun@12301.cc
 *
 */
public class DefaultSerializer implements Serializer {

	@Override
	public byte[] serialize(PFTPacket packet) {
		return JSON.toJSONString(packet).getBytes();
	}

}

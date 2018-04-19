package com.pft.communicate.protocal.parser.deserializer.common;

import com.pft.communicate.protocal.packet.PFTPacket;

/****
 * 通用的反序列化
 * 
 * @author majun@12301.cc
 *
 */
public interface CommonDeserializer {

	public <T extends PFTPacket> T deserialize(String body, Class<T> clazz);

}

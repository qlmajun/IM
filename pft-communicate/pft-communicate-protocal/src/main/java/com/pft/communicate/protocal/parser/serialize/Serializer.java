package com.pft.communicate.protocal.parser.serialize;

import com.pft.communicate.protocal.packet.PFTPacket;

/****
 * 序列化接口声明
 * 
 * @author majun@12301.cc
 *
 */
public interface Serializer {

	byte[] serialize(PFTPacket packet);
}

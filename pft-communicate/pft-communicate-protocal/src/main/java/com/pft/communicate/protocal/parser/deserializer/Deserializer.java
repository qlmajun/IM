package com.pft.communicate.protocal.parser.deserializer;

import com.pft.communicate.protocal.packet.Header.OpCode;
import com.pft.communicate.protocal.packet.PFTPacket;

/***
 * 反序列化接口声明
 * 
 * @author majun@12301.cc
 *
 */
public interface Deserializer {

	/***
	 * 反序列换报文
	 * 
	 * @param body
	 * @return
	 */
	PFTPacket deserialize(String body);

	/**
	 * @return 该解码类对应解码的OPCODE
	 */
	OpCode getOpCode();

}

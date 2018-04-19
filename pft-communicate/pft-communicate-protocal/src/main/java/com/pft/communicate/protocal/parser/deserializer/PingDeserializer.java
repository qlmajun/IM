package com.pft.communicate.protocal.parser.deserializer;

import com.pft.communicate.protocal.packet.Header.OpCode;
import com.pft.communicate.protocal.packet.PFTPacket;
import com.pft.communicate.protocal.packet.ping.PingPacket;

/****
 * ping报文反序列化
 * 
 * @author majun@12301.cc
 *
 */
public class PingDeserializer implements Deserializer {

	@Override
	public PFTPacket deserialize(String body) {
		return PingPacket.instance();
	}

	@Override
	public OpCode getOpCode() {
		return OpCode.PING;
	}

}

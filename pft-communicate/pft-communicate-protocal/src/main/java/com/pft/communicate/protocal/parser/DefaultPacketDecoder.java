package com.pft.communicate.protocal.parser;

import com.pft.communicate.protocal.packet.Header;
import com.pft.communicate.protocal.packet.Header.OpCode;
import com.pft.communicate.protocal.packet.PFTPacket;
import com.pft.communicate.protocal.parser.deserializer.Deserializer;
import com.pft.communicate.protocal.parser.deserializer.DeserializerManager;

/****
 * 报文默认解码器
 * 
 * @author majun@12301.cc
 *
 */
public class DefaultPacketDecoder implements PacketDecoder {

	@Override
	public PFTPacket decode(byte[] packet) {
		return decode(Header.getHeaderBytes(packet), Header.getBodyBytes(packet));
	}

	@Override
	public PFTPacket decode(byte[] header, byte[] body) {

		OpCode opcode = Header.getOpcodeFromPacket(header);

		return decodePacket(opcode, body);
	}

	private PFTPacket decodePacket(OpCode opcode, byte[] body) {
		String bodyStr = null;
		if (body != null && body.length != 0) {
			bodyStr = new String(body);
		}
		return decode(opcode, bodyStr);
	}

	protected PFTPacket decode(OpCode opcode, String body) {
		Deserializer deserializer = DeserializerManager.getInstance().getDeserializer(opcode);
		if (deserializer == null) {
			return null;
		}
		return deserializer.deserialize(body);
	}

}

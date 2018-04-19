package com.pft.communicate.protocal.parser;

import com.pft.communicate.protocal.OpcodeMapper;
import com.pft.communicate.protocal.packet.Header;
import com.pft.communicate.protocal.packet.PFTPacket;
import com.pft.communicate.protocal.parser.serialize.SerializerManager;

/***
 * 报文默认编码器
 * 
 * @author majun@12301.cc
 *
 */
public class DefaultPacketEncoder implements PacketEncoder {

	@Override
	public byte[] encode(PFTPacket packet) {
		return encode(0, packet);
	}

	@Override
	public byte[] encode(int sequenceId, PFTPacket packet) {

		if (packet == null) {
			throw new IllegalArgumentException("packet can not be null.");
		}

		byte[] packetBytes = SerializerManager.getSerializer(packet).serialize(packet);

		int length = packetBytes == null ? 0 : packetBytes.length;

		byte[] opCode = OpcodeMapper.getOpCodeBytesByPacket(packet);

		byte[] headerBytes = Header.buildHeader(opCode, length, 0);

		return BytesUtil.arraycat(headerBytes, packetBytes);
	}

}

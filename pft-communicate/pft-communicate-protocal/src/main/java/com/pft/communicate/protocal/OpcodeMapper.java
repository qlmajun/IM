package com.pft.communicate.protocal;

import java.util.HashMap;
import java.util.Map;

import com.pft.communicate.protocal.packet.Header;
import com.pft.communicate.protocal.packet.Header.OpCode;
import com.pft.communicate.protocal.packet.auth.AuthPacket;
import com.pft.communicate.protocal.packet.error.ErrorPacket;
import com.pft.communicate.protocal.packet.message.MessagePacket;
import com.pft.communicate.protocal.packet.message.MessageReceiptsPacket;
import com.pft.communicate.protocal.packet.PFTPacket;
import com.pft.communicate.protocal.packet.ping.PingPacket;
import com.pft.communicate.protocal.packet.presence.PresencePacket;

/***
 * opCode和报文的映射配置
 * 
 * @author majun@12301.cc
 *
 */
public class OpcodeMapper {

	private final Map<Header.OpCode, Class<? extends PFTPacket>> opCodePacketMapper = new HashMap<Header.OpCode, Class<? extends PFTPacket>>();

	private final Map<Class<? extends PFTPacket>, Header.OpCode> packetOpcodeMapper = new HashMap<Class<? extends PFTPacket>, Header.OpCode>();

	private static OpcodeMapper instance = new OpcodeMapper();

	public static OpcodeMapper getInstance() {
		return instance;
	}

	private OpcodeMapper() {

		add(Header.OpCode.PING, PingPacket.class);

		add(Header.OpCode.AUTH, AuthPacket.class);

		add(Header.OpCode.PRESENCE, PresencePacket.class);

		add(Header.OpCode.MESSAGE, MessagePacket.class);

		add(Header.OpCode.MESSAGERECEIPTS, MessageReceiptsPacket.class);

		add(Header.OpCode.ERROR, ErrorPacket.class);

	}

	private void add(Header.OpCode opcode, Class<? extends PFTPacket> clazz) {

		if (opCodePacketMapper.put(opcode, clazz) != null) {
			throw new IllegalArgumentException("duplicate opcode find when add packet class" + clazz.getCanonicalName());
		}

		if (packetOpcodeMapper.put(clazz, opcode) != null) {
			throw new IllegalArgumentException("duplicate class find when add opcode" + clazz.getCanonicalName());
		}

	}

	public static Class<? extends PFTPacket> getPacketByOpCode(byte[] opcode) {
		return getPacketByOpCode(OpCode.fromBytes(opcode));
	}

	public static Class<? extends PFTPacket> getPacketByOpCode(OpCode opcode) {
		return instance.opCodePacketMapper.get(opcode);
	}

	public static byte[] getOpCodeBytesByPacket(PFTPacket packet) {
		return getOpCodeByPacket(packet.getClass()).bytes();
	}

	public static OpCode getOpCodeByPacket(PFTPacket packet) {
		return getOpCodeByPacket(packet.getClass());
	}

	public static OpCode getOpCodeByPacket(Class<? extends PFTPacket> clazz) {
		return instance.packetOpcodeMapper.get(clazz);
	}
}

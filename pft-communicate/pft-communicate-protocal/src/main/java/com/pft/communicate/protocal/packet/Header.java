package com.pft.communicate.protocal.packet;

import java.util.Arrays;

import com.pft.communicate.protocal.parser.BytesUtil;

/****
 * 报文头
 * 
 * @author majun@12301.cc
 *
 */
public class Header {

	/** 协议的版本号 **/
	public static final byte[] PROTOCAL_VERSION = { 0x01, 0x00 };

	/** 协议头的字节数 **/
	public static final int HEADER_SIZE = 13;

	private static final byte[] OP_AUTH = { 0x00, 0x01 };

	private static final byte[] OP_PING = { 0x00, 0x02 };

	private static final byte[] OP_PRESENCE = { 0x00, 0x03 };

	private static final byte[] OP_CLOSE = { 0x00, 0x04 };

	private static final byte[] OP_MESSAGE = { 0x10, 0x10 };

	private static final byte[] OP_MESSAGERECEIPTS = { 0x10, 0x01 };

	private static final byte[] OP_ERROR = { 0x40, 0x00 };

	public enum OpCode {

		AUTH(Header.OP_AUTH),

		PING(Header.OP_PING),

		PRESENCE(Header.OP_PRESENCE),

		CLOSE(Header.OP_CLOSE),

		MESSAGE(Header.OP_MESSAGE),

		MESSAGERECEIPTS(Header.OP_MESSAGERECEIPTS),

		ERROR(Header.OP_ERROR);

		private byte[] code;

		private OpCode(byte[] code) {
			this.code = code;
		}

		public static OpCode fromBytes(byte[] bytes) {
			if (bytes == null || bytes.length != 2) {
				return null;
			}

			for (OpCode opCode : OpCode.values()) {
				if (opCode.bytes()[0] == bytes[0] && opCode.bytes()[1] == bytes[1]) {
					return opCode;
				}
			}
			return null;
		}

		public byte[] bytes() {
			return code;
		}
	}

	public static boolean isProtocalHeader(final byte[] packet) {
		if (packet != null && packet.length >= HEADER_SIZE && getOpcodeFromPacket(packet) != null && (packet[0] == 0x00 || (packet[0] >> 7 & 0x01) != 0x00)) {
			return true;
		}
		return false;
	}

	public static byte[] buildHeader(byte[] opCode, int packetSize, int seqId) {
		byte[] bytes = new byte[HEADER_SIZE];

		BytesUtil.fillBytesWithArray(bytes, 1, 2, opCode);

		if (packetSize != 0) {
			BytesUtil.fillBytesWithArray(bytes, 3, 6, BytesUtil.intTobyteArray(packetSize));
		}

		// version
		BytesUtil.fillBytesWithArray(bytes, 7, 8, PROTOCAL_VERSION);

		if (seqId != 0) {
			BytesUtil.fillBytesWithArray(bytes, 9, 12, BytesUtil.intTobyteArray(seqId));
		}
		return bytes;
	}

	public static OpCode getOpcodeFromPacket(byte[] byteArray) {
		return OpCode.fromBytes(Arrays.copyOfRange(byteArray, 1, 3));
	}

	public static byte[] getOpcodeBytesFromPacket(byte[] byteArray) {
		return Arrays.copyOfRange(byteArray, 1, 3);
	}

	public static int getPacketSize(byte[] byteArray) {
		return BytesUtil.bytesArrayToInt(byteArray, 3);
	}

	public static byte[] getHeaderBytes(byte[] packet) {
		return Arrays.copyOfRange(packet, 0, Header.HEADER_SIZE);
	}

	public static byte[] getBodyBytes(byte[] packet) {
		return Arrays.copyOfRange(packet, Header.HEADER_SIZE, packet.length);
	}

	public static byte[] getVersion(byte[] byteArray) {
		return Arrays.copyOfRange(byteArray, 7, 9);
	}

	public static byte[] getSequenceId(byte[] byteArray) {
		return Arrays.copyOfRange(byteArray, 9, 13);
	}

}

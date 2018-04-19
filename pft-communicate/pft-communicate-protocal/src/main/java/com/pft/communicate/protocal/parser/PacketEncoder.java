package com.pft.communicate.protocal.parser;

import com.pft.communicate.protocal.packet.PFTPacket;

/****
 * 报文编码操作接口声明
 * 
 * @author majun@12301.cc
 *
 */
public interface PacketEncoder {

	/**
	 * 对指定报文进行编码
	 * 
	 * @param packet
	 * @return
	 */
	byte[] encode(PFTPacket packet);

	/**
	 * 对指定的报文进行编码
	 * 
	 * @param sequenceId
	 *            序列号
	 * @param packet
	 * @return
	 */
	byte[] encode(int sequenceId, PFTPacket packet);
}

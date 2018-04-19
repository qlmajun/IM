package com.pft.communicate.protocal.parser;

import com.pft.communicate.protocal.packet.PFTPacket;

/****
 * 报文解码操作接口声明
 * 
 * @author majun@12301.cc
 *
 */
public interface PacketDecoder {

	/**
	 * 对报文进行解码，获取报文的类型和报文的正文 会验证报文的长度是否正确，即头部存储的字节数和body实际的字节数
	 *
	 * @param packet
	 *            完整报文的字节数组
	 * @return
	 */
	public PFTPacket decode(byte[] packet);

	/**
	 * 这里不会验证报文的长度
	 *
	 * @param header
	 *            报文的头， 共13个字节
	 * @param body
	 *            报文主体， json字符串的字节数组
	 * @return
	 */
	public PFTPacket decode(byte[] header, byte[] body);

}

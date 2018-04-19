package com.pft.communicate.protocal.packet.ping;

import com.pft.communicate.protocal.packet.EmptyPacket;
import com.pft.communicate.protocal.packet.PFTPacket;

/***
 * 心跳报文
 * 
 * @author majun@12301.cc
 *
 */
public class PingPacket extends PFTPacket implements EmptyPacket {

	private static final long serialVersionUID = 6563352904730299370L;

	private static PingPacket instance = new PingPacket();

	public PingPacket() {
	}

	public static PingPacket instance() {
		return instance;
	}

}

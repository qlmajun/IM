package com.pft.communication.server.process;

import com.pft.communicate.protocal.packet.PFTPacket;
import com.pft.communication.server.connector.session.LocalClientSession;

/****
 * 报文处理基本类
 * 
 * @author majun@12301.cc
 *
 * @param <E>
 */
public abstract class PacketHandler<E extends PFTPacket> {

	protected void reply(LocalClientSession clientSession, PFTPacket packet) {
		clientSession.deliver(packet);
	}

	protected boolean accept(LocalClientSession clientSession, E packet) {
		return true;
	}

	public abstract boolean process(LocalClientSession clientSession, E packet);
}

package com.pft.communicate.protocal.packet;

import java.io.Serializable;

/***
 * 通讯报文基础类
 * 
 * @author majun@12301.cc
 *
 */
public abstract class PFTPacket implements Serializable, Cloneable {

	private static final long serialVersionUID = 3041899014911454640L;

	@Override
	protected PFTPacket clone() throws CloneNotSupportedException {
		return (PFTPacket) super.clone();
	}
}

package com.pft.communicate.protocal.packet;

import com.pft.communicate.protocal.util.IDMaker;

/***
 * 基础报文
 * 
 * @author majun@12301.cc
 *
 */
public abstract class BasicPftPacket extends PFTPacket {

	private static final long serialVersionUID = 3816089178050574878L;

	/** 报文Id值 **/
	protected String id;

	/** 报文发送方 **/
	protected String from;

	/** 报文接收方 **/
	protected String to;

	public BasicPftPacket() {
		this(null, null, null);
	}

	public BasicPftPacket(String id, String from, String to) {
		if (id == null) {
			this.id = IDMaker.makeId();
		} else {
			this.id = id;
		}
		this.from = from;
		this.to = to;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BasicPftPacket other = (BasicPftPacket) obj;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BasicPftPacket [id=" + id + ", from=" + from + ", to=" + to + "]";
	}
}

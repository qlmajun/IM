package com.pft.communicate.protocal.packet.message;

import com.pft.communicate.protocal.packet.BasicPftPacket;

/****
 * 消息回执报文
 * 
 * @author majun@12301.cc
 *
 */
public class MessageReceiptsPacket extends BasicPftPacket {

	private static final long serialVersionUID = -1787150349686422145L;

	/** 时间戳 **/
	private long dateline;

	public MessageReceiptsPacket() {
		this(null);
	}

	public MessageReceiptsPacket(String id) {
		super(id, null, null);
		this.dateline = System.currentTimeMillis();
	}

	public long getDateline() {
		return dateline;
	}

	public void setDateline(long dateline) {
		this.dateline = dateline;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (dateline ^ (dateline >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		MessageReceiptsPacket other = (MessageReceiptsPacket) obj;
		if (dateline != other.dateline)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MessageReceiptsPacket [dateline=" + dateline + ", id=" + id + ", from=" + from + ", to=" + to + "]";
	}
}

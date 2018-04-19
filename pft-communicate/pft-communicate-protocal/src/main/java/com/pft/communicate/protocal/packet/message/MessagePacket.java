package com.pft.communicate.protocal.packet.message;

import com.pft.communicate.protocal.packet.BasicPftPacket;

/***
 * 消息报文
 * 
 * @author majun@12301.cc
 *
 */
public class MessagePacket extends BasicPftPacket {

	private static final long serialVersionUID = -6623876316011716316L;

	/** 消息类型 **/
	private int contentType = ContentType.TEXT.getValue();

	/** 消息体, 为JSON, 字段自定义 **/
	private String content;

	/** 是否需要消息回执 **/
	private boolean receipts = true;

	/** 是否需要消息回执 **/
	private long dateLine;

	public MessagePacket() {
		super();
	}

	public MessagePacket(String to, String content) {
		this(to, content, ContentType.TEXT);
	}

	public MessagePacket(String to, String content, ContentType contentType) {
		this(null, to, content, contentType);
	}

	public MessagePacket(String from, String to, String content, ContentType contentType) {
		this(to, content, from, contentType, true);
	}

	public MessagePacket(String to, String content, String from, ContentType contentType, boolean receipts) {
		super();
		this.to = to;
		this.from = from;
		this.content = content;
		this.contentType = contentType.getValue();
		this.receipts = receipts;
		this.dateLine = System.currentTimeMillis();
	}

	public int getContentType() {
		return contentType;
	}

	public void setContentType(int contentType) {
		this.contentType = contentType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isReceipts() {
		return receipts;
	}

	public void setReceipts(boolean receipts) {
		this.receipts = receipts;
	}

	public long getDateLine() {
		return dateLine;
	}

	public void setDateLine(long dateLine) {
		this.dateLine = dateLine;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + contentType;
		result = prime * result + (int) (dateLine ^ (dateLine >>> 32));
		result = prime * result + (receipts ? 1231 : 1237);
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
		MessagePacket other = (MessagePacket) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (contentType != other.contentType)
			return false;
		if (dateLine != other.dateLine)
			return false;
		if (receipts != other.receipts)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MessagePacket [contentType=" + contentType + ", content=" + content + ", receipts=" + receipts + ", dateLine=" + dateLine + ", id=" + id + ", from=" + from + ", to=" + to + "]";
	}
}

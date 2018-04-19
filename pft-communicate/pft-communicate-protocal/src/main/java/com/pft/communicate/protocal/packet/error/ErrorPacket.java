package com.pft.communicate.protocal.packet.error;

import com.pft.communicate.protocal.packet.BasicPftPacket;

/****
 * 错误报文
 * 
 * @author majun@12301.cc
 *
 */
public class ErrorPacket extends BasicPftPacket {

	private static final long serialVersionUID = -1977372366161880769L;

	/** 错误码 **/
	private int code;

	/** 错误消息 **/
	private String message;

	public ErrorPacket() {
		super();
	}

	public ErrorPacket(String id, int code, String message) {
		super(id, null, null);
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + code;
		result = prime * result + ((message == null) ? 0 : message.hashCode());
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
		ErrorPacket other = (ErrorPacket) obj;
		if (code != other.code)
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ErrorPacket [code=" + code + ", message=" + message + ", id=" + id + ", from=" + from + ", to=" + to + "]";
	}
}

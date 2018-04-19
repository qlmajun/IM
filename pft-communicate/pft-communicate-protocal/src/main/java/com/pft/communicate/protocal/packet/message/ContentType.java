package com.pft.communicate.protocal.packet.message;

/****
 * 消息枚举类型
 * 
 * @author majun@12301.cc
 *
 */
public enum ContentType {

	TEXT(2);

	private int value;

	private ContentType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}

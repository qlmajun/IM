package com.pft.communicate.protocal.common.exception;

public class InvalidPFTPacketException extends Exception {

	private static final long serialVersionUID = 2710916485032945199L;

	public InvalidPFTPacketException() {
		super();
	}

	public InvalidPFTPacketException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidPFTPacketException(String message) {
		super(message);
	}

	public InvalidPFTPacketException(Throwable cause) {
		super(cause);
	}

}

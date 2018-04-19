package com.pft.communication.server.common.exception;

public class PftPacketIllegalStateException extends IllegalStateException {

	private static final long serialVersionUID = -8095525526201328380L;

	public PftPacketIllegalStateException() {
		super();
	}

	public PftPacketIllegalStateException(String message, Throwable cause) {
		super(message, cause);
	}

	public PftPacketIllegalStateException(String s) {
		super(s);
	}

	public PftPacketIllegalStateException(Throwable cause) {
		super(cause);
	}

}

package com.pft.communicate.core.exception;

/**
 * 报文处理的自定义异常, 包含报文的错误code和http的错误code, 其中 <b>报文code=httpcode*10+N(1<N<10)</b>
 * 或者<b> 报文code=httpcode*100+N(9<N<100)</b>
 *
 * @author majun@12301.cc
 *
 */
public abstract class PftCommunicationException extends RuntimeException {

	private static final long serialVersionUID = -3162480031299026049L;

	public PftCommunicationException(String message, Throwable cause) {
		super(message, cause);
	}

	public PftCommunicationException(String message) {
		super(message);
	}

	public PftCommunicationException(Throwable cause) {
		super(cause);
	}

	/**
	 * 返回报文的错误code, 错误代码 5位数字, 前三位和http status保持一致, 后两位为自定义的错误码
	 *
	 * @return
	 */
	public abstract int getCode();

	/**
	 * 获取http的错误code
	 *
	 * @return
	 */
	public final int getHttpCode() {

		int code = getCode();

		if (code > 9999) {
			return code / 100;
		}

		if (code > 1000) {
			return code / 10;
		}
		return code;
	}

}

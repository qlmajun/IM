package com.pft.communicate.cache.exception;

/****
 * 自定义异常
 * 
 * @author majun@12301.cc
 *
 */
public class DuplicateCacheDeclareException extends RuntimeException {

	private static final long serialVersionUID = -2004944416971523135L;

	public DuplicateCacheDeclareException(String message, Throwable cause) {
		super(message, cause);
	}

	public DuplicateCacheDeclareException(String message) {
		super(message);
	}

	public DuplicateCacheDeclareException(Throwable cause) {
		super(cause);
	}

}

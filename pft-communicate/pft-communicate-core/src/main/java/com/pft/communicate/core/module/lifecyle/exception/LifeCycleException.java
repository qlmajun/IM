package com.pft.communicate.core.module.lifecyle.exception;

import com.pft.communicate.core.exception.PftCommunicationException;
import com.pft.communicate.core.module.lifecyle.LifeCycle.LifeCycleState;

/****
 * 自定义声明中期异常
 * 
 * @author majun@12301.cc
 *
 */
public class LifeCycleException extends PftCommunicationException {

	private static final long serialVersionUID = 7790194450032246981L;

	private LifeCycleState state;

	public static final int CODE = 50000;

	@Override
	public int getCode() {
		return CODE;
	}

	public LifeCycleState getLifeCycleState() {
		return state;
	}

	public LifeCycleException(LifeCycleState peroid, String message, Throwable cause) {
		super(message, cause);
		this.state = peroid;
	}

	public LifeCycleException(String message) {
		super(message);
	}

	public LifeCycleException(LifeCycleState peroid, String message) {
		super(message);
		this.state = peroid;
	}

}

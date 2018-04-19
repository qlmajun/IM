package com.pft.communicate.core.module.lifecyle;

import com.pft.communicate.core.module.lifecyle.exception.LifeCycleException;

/****
 * Bean生命周期接口声明
 * 
 * @author majun@12301.cc
 *
 */
public interface LifeCycle {

	public enum LifeCycleState {
		before_init, init, after_init, before_start, start, after_start, before_stop, stop, after_stop
	}

	void init() throws LifeCycleException;

	void start() throws LifeCycleException;

	void stop() throws LifeCycleException;

	LifeCycleState getLifeCycleState();
}

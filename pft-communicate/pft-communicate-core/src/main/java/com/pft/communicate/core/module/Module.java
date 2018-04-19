package com.pft.communicate.core.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pft.communicate.core.module.annotation.ModuleConfig;
import com.pft.communicate.core.module.lifecyle.LifeCycle;
import com.pft.communicate.core.module.lifecyle.exception.LifeCycleException;

/**
 * 模块代码的基础实现
 *
 * @author majun@12301.cc
 *
 */
@ModuleConfig(startupPriority = ModuleConfig.DEFAULT_STARTUP_PRIORITY, stopPriority = ModuleConfig.DEFAULT_STOP_PRIORITY, enable = true)
public abstract class Module implements LifeCycle {

	private static final Logger logger = LoggerFactory.getLogger(Module.class);

	protected boolean disabled = false;

	protected LifeCycleState lifeCycleState = LifeCycleState.before_init;

	@Override
	public final void init() throws LifeCycleException {

		beforeInit();
		printState(LifeCycleState.init, true);
		lifeCycleState = LifeCycleState.init;
		doInit();
		afterInit();
	}

	protected void beforeInit() throws LifeCycleException {

	}

	protected abstract void doInit() throws LifeCycleException;

	protected void afterInit() throws LifeCycleException {
		lifeCycleState = LifeCycleState.after_init;
		printState(LifeCycleState.init, false);
	}

	@Override
	public final void start() throws LifeCycleException {

		beforeStart();
		printState(LifeCycleState.start, true);
		lifeCycleState = LifeCycleState.start;
		doStart();
		afterStart();

	}

	protected void beforeStart() throws LifeCycleException {
		lifeCycleState = LifeCycleState.before_start;
	}

	protected abstract void doStart() throws LifeCycleException;

	protected void afterStart() throws LifeCycleException {
		printState(LifeCycleState.start, false);
		lifeCycleState = LifeCycleState.after_start;
	}

	@Override
	public final void stop() throws LifeCycleException {

		printState(LifeCycleState.stop, true);

		lifeCycleState = LifeCycleState.stop;
		doStop();
		printState(LifeCycleState.stop, false);

		lifeCycleState = LifeCycleState.after_stop;
	}

	protected void beforeStop() throws LifeCycleException {
		lifeCycleState = LifeCycleState.before_stop;
	}

	protected abstract void doStop() throws LifeCycleException;

	@Override
	public final LifeCycleState getLifeCycleState() {
		return lifeCycleState;
	}

	private void printState(LifeCycleState state, boolean start) {
		if (start) {
			logger.info("module " + getModuleName() + " is " + state.name() + "ing...");
		} else {
			logger.info("module " + getModuleName() + " " + state.name() + "ed successfully!");
		}
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public String getModuleName() {
		return this.getClass().getSimpleName();
	}

}

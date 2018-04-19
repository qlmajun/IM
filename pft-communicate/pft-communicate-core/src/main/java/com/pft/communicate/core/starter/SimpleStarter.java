package com.pft.communicate.core.starter;

import java.net.URL;

import org.apache.commons.configuration2.ImmutableConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pft.communicate.core.configuration.ConfigurationManager;
import com.pft.communicate.core.module.ModuleManager;
import com.pft.communicate.core.module.lifecyle.exception.LifeCycleException;

/****
 * 简单的启动类
 * 
 * @author majun@12301.cc
 *
 */
public abstract class SimpleStarter {

	private static final Logger logger = LoggerFactory.getLogger(SimpleStarter.class);

	protected ConfigurationManager configurationManager;

	protected ModuleManager moduleManager;

	private String homeDirectory;

	private String applicationName;

	/** 保存程序中的启动器 **/
	private static SimpleStarter starter;

	public SimpleStarter(ConfigurationManager configurationManager, ModuleManager moduleManager) {
		super();
		this.configurationManager = configurationManager;
		this.moduleManager = moduleManager;

		if (starter != null) {
			logger.warn("multi starter found, previous starter {} and current starter {} ", starter.getClass(),
					this.getClass());
		}
		starter = this;
	}

	/**
	 * 返回启动器, 包括application方式启动和容器启动
	 *
	 * @return jvm中的启动器
	 */
	public static SimpleStarter getStarter() {
		return starter;
	}

	@SuppressWarnings("unchecked")
	public <T> T getModule(Class<T> moduleClass) {

		if (moduleClass.isInstance(configurationManager)) {
			return (T) configurationManager;
		}

		T module = moduleManager.getModule(moduleClass);
		if (module == null) {
			throw new LifeCycleException("module with class " + moduleClass + "not found");
		}
		return module;
	}

	public ImmutableConfiguration getConfiguration() {
		return configurationManager.getConfiguration();
	}

	/**
	 * 返回项目的根目录
	 *
	 * @return 项目的home目录, 以"/"结尾
	 */
	public String getHomeDirectory() {
		if (isProductEnvironment()) {
			homeDirectory = System.getProperty("app.home");
		}

		if (homeDirectory == null) {
			homeDirectory = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		}

		if (!homeDirectory.endsWith("/")) {
			homeDirectory += "/";
		}

		return homeDirectory;
	}

	/**
	 * 判断是否以生产模式启动的, 如果启动类在jar包中则为生产环境
	 *
	 * @return
	 */
	public boolean isProductEnvironment() {
		URL resource = this.getClass().getResource(this.getClass().getSimpleName() + ".class");
		if (resource.toString().startsWith("jar")) {
			return true;
		}
		return false;
	}

	public String getConfDirectory() {

		if (isProductEnvironment()) {
			return getHomeDirectory() + "conf/";
		}
		return getHomeDirectory();

	}

	/**
	 * 返回该实例的名称, 默认的project_name
	 *
	 * @return 实例的名称
	 */
	public String getApplicationName() {

		if (!StringUtils.isEmpty(applicationName)) {
			return applicationName;
		}

		applicationName = getConfiguration().getString("application.name");

		if (!StringUtils.isEmpty(applicationName)) {
			return applicationName;
		}

		String homeDir = getHomeDirectory();

		if (!isProductEnvironment()) {
			homeDir = homeDir.replace("/target/classes", "");
		}

		applicationName = homeDir.substring(homeDir.lastIndexOf("/", homeDir.length() - 2) + 1, homeDir.length() - 1);

		return applicationName;
	}

	public String getHostIp() {
		return configurationManager.getConfiguration().getString(ConfigurationManager.PROPERTY_DEFAULT_IP_ADDRESSS,
				"127.0.0.1");
	}

	public void start() {
		configurationManager.init();
		moduleManager.initModules();
		moduleManager.loadModules();
		moduleManager.startModules();
		Runtime.getRuntime().addShutdownHook(new ShutdownHookThread());
	}

	public void stop() {
		configurationManager.stop();
		moduleManager.stopModules();
	}

	protected class ShutdownHookThread extends Thread {
		@Override
		public void run() {
			logger.info("shutdown hook start running.....");
			SimpleStarter.this.stop();
			logger.info("shutdown hook finshed.....");
		}
	}

}

package com.pft.communicate.core.starter;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pft.communicate.core.configuration.ConfigurationManager;
import com.pft.communicate.core.module.ModuleManager;

/****
 * Spring启动类，读取Spring的配置文件
 * 
 * @author majun@12301.cc
 *
 */

public class SpringContextStarter extends SimpleStarter {

	private static final Logger logger = LoggerFactory.getLogger(SpringContextStarter.class);

	protected ApplicationContext context;

	private static final String SPRING_XML_CONFIGURATION = "classpath:applicationContext.xml";

	public SpringContextStarter(ConfigurationManager configurationManager, ModuleManager moduleManager) {
		super(configurationManager, moduleManager);
	}

	/**
	 * 返回启动器, 包括application方式启动和容器启动
	 *
	 * @return jvm中的启动器
	 */
	public static SpringContextStarter getStarter() {
		SimpleStarter starter = SimpleStarter.getStarter();
		if (starter != null && starter instanceof SpringContextStarter) {
			return (SpringContextStarter) starter;
		}
		return null;
	}

	private void initSpringContext() {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(SPRING_XML_CONFIGURATION);
		this.context = context;
		context.start();

	}

	void setApplicationContext(ApplicationContext context) {
		this.context = context;
	}

	void beforeSpringContextStart() {
		configurationManager.init();
		moduleManager.initModules();
	}

	@Override
	public void start() {
		beforeSpringContextStart();
		initSpringContext();
		afterSpringContextStart();
	}

	void afterSpringContextStart() {
		moduleManager.loadModules();
		moduleManager.startModules();
		Runtime.getRuntime().addShutdownHook(new ShutdownHookThread());
	}

	public <T> T getSpringBean(Class<T> clazz) {
		return context.getBean(clazz);
	}

	public <T> T getSpringBean(Class<T> clazz, String beanName) {
		return context.getBean(beanName, clazz);
	}

	void beforeSpringContextStop() {
		tryToStopDubboListener();
	}

	@Override
	public void stop() {
		beforeSpringContextStop();
		if (context instanceof AbstractApplicationContext) {
			((AbstractApplicationContext) context).stop();
		}
		afterSpringContextStop();
	}

	void afterSpringContextStop() {
		super.stop();
	}

	/**
	 * 尝试停止dubbo监听
	 */
	private void tryToStopDubboListener() {
		try {
			Class<?> clazz = Class.forName("com.alibaba.dubbo.config.ProtocolConfig");
			Method method = clazz.getMethod("destroyAll", new Class<?>[0]);
			method.invoke(null, new Object[0]);
		} catch (ClassNotFoundException e) {
			logger.info("no dubbo found, stop try ");
		} catch (Exception e) {
			logger.error("try to stop dubbo failed", e);
		}
	}

}

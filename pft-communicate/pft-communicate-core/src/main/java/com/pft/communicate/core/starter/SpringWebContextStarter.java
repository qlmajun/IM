package com.pft.communicate.core.starter;

import javax.servlet.ServletContextEvent;

import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import com.pft.communicate.core.configuration.ConfigurationManager;
import com.pft.communicate.core.module.ModuleManager;

public abstract class SpringWebContextStarter extends ContextLoaderListener {

	private SpringContextStarter starter;

	public SpringWebContextStarter(ModuleManager moduleManager) {
		starter = new SpringContextStarter(new ConfigurationManager(), moduleManager);
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {

		starter.beforeSpringContextStart();

		super.contextInitialized(sce);

		WebApplicationContext currentWebApplicationContext = getCurrentWebApplicationContext();
		starter.setApplicationContext(currentWebApplicationContext);

		starter.afterSpringContextStart();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		starter.beforeSpringContextStop();
		super.contextDestroyed(sce);
		starter.afterSpringContextStop();
	}

}

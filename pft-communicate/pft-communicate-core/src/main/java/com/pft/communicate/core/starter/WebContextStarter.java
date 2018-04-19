package com.pft.communicate.core.starter;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.pft.communicate.core.configuration.ConfigurationManager;
import com.pft.communicate.core.module.ModuleManager;

/****
 * web项目启动类
 * 
 * @author majun@12301.cc
 *
 */
public abstract class WebContextStarter extends SpringContextStarter implements ServletContextListener {

	public WebContextStarter(ConfigurationManager configurationManager, ModuleManager moduleManager) {
		super(configurationManager, moduleManager);
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		start();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		stop();
	}

}

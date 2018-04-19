package com.pft.communicate.core.configuration;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration2.ImmutableConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedBuilderParametersImpl;
import org.apache.commons.configuration2.builder.PropertiesBuilderParametersImpl;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.event.ConfigurationEvent;
import org.apache.commons.configuration2.event.Event;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.reloading.PeriodicReloadingTrigger;
import org.apache.commons.configuration2.reloading.ReloadingEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pft.communicate.core.module.Module;
import com.pft.communicate.core.module.lifecyle.exception.LifeCycleException;
import com.pft.communicate.core.support.network.NetInterfaceUtil;

/****
 * 配置文件读取和写入操作管理类
 * 
 * @author majun@12301.cc
 *
 */

public class ConfigurationManager extends Module {

	private static final Logger logger = LoggerFactory.getLogger(ConfigurationManager.class);

	private String configurationFileName = "application.properties";

	public static final String PROPERTY_DEFAULT_IP_ADDRESSS = "host.ip.address";

	private ReloadingFileBasedConfigurationBuilder<PropertiesConfiguration> builder;

	private PeriodicReloadingTrigger trigger;

	private ImmutableConfiguration configuration;

	public ConfigurationManager() {
	}

	public ConfigurationManager(String fileName) {
		if (StringUtils.isNotEmpty(fileName)) {
			this.configurationFileName = fileName;
		}
	}

	@Override
	protected void doInit() {
		FileBasedBuilderParametersImpl params = new PropertiesBuilderParametersImpl();
		params.setFileName(configurationFileName);
		builder = new ReloadingFileBasedConfigurationBuilder<PropertiesConfiguration>(PropertiesConfiguration.class);
		builder.configure(params);

		try {
			PropertiesConfiguration configConfiguration = builder.getConfiguration();
			addDefaultProperties(configConfiguration);
			builder.save();
			configuration = configConfiguration;
		} catch (ConfigurationException e) {
			logger.error("ConfigurationException when get configuration object", e);
		}
	}

	@Override
	protected void doStart() throws LifeCycleException {
		getConfiguration().getInt("file.properties.reload.peroid.second", 10);
		trigger = new PeriodicReloadingTrigger(builder.getReloadingController(), null, 10, TimeUnit.SECONDS);
		trigger.start();

		builder.addEventListener(ConfigurationEvent.ANY, new EventListener<Event>() {
			@Override
			public void onEvent(Event event) {
				logger.info("configuration file changed with event " + event);
				if (event instanceof ReloadingEvent) {
					try {
						configuration = builder.getConfiguration();
					} catch (ConfigurationException e) {
						logger.error("ConfigurationException when get configuration object", e);
						configuration = null;
					}
				}
			}
		});

	}

	private void addDefaultProperties(PropertiesConfiguration configConfiguration) {
		addIPAddressProperties(configConfiguration);
	}

	private void addIPAddressProperties(PropertiesConfiguration configConfiguration) {
		String ipAddress = configConfiguration.getString(PROPERTY_DEFAULT_IP_ADDRESSS);

		if (!StringUtils.isEmpty(ipAddress)) {
			try {
				List<InetAddress> internetAddress = NetInterfaceUtil.getInternetAddress();
				if (internetAddress.contains(ipAddress)) {
					return;
				}

			} catch (SocketException e) {
				logger.error("get Network Interface error", e);
				return;
			}

		}

		ipAddress = NetInterfaceUtil.getIdentifyAddress();
		configConfiguration.setProperty(PROPERTY_DEFAULT_IP_ADDRESSS, ipAddress);
	}

	@Override
	protected void doStop() throws LifeCycleException {
		if (trigger != null) {
			trigger.shutdown();
		}
	}

	public ImmutableConfiguration getConfiguration() {
		return configuration;
	}

}

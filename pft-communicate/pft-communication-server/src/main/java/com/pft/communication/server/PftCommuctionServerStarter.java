package com.pft.communication.server;

import com.pft.communicate.core.configuration.ConfigurationManager;
import com.pft.communicate.core.starter.SpringContextStarter;

/****
 * 服务启动类
 * 
 * @author majun@12301.cc
 *
 */
public class PftCommuctionServerStarter extends SpringContextStarter {

	private static PftCommuctionServerStarter starter;

	public PftCommuctionServerStarter() {
		super(new ConfigurationManager(), new PftCommunctionModuleManagerImpl());
		starter = this;
	}

	public static void main(String[] args) {
		new PftCommuctionServerStarter().start();
	}

	public static PftCommuctionServerStarter getInstance() {
		return starter;
	}

}

package com.pft.communication.server;

import java.util.ArrayList;
import java.util.List;

import com.pft.communicate.core.cache.RedisCacheManager;
import com.pft.communicate.core.module.Module;
import com.pft.communicate.core.module.ModuleManagerImpl;
import com.pft.communication.server.connector.mina.ConnectionManagerImpl;
import com.pft.communication.server.connector.session.SessionManagerImpl;
import com.pft.communication.server.module.PacketDispatcherManager;
import com.pft.communication.server.session.impl.PftDeviceSessionManagerImpl;

public class PftCommunctionModuleManagerImpl extends ModuleManagerImpl {

	@Override
	public List<Class<? extends Module>> getModules() {
		List<Class<? extends Module>> moduleClasses = new ArrayList<Class<? extends Module>>();

		moduleClasses.add(RedisCacheManager.class);

		moduleClasses.add(PacketDispatcherManager.class);

		moduleClasses.add(PftDeviceSessionManagerImpl.class);

		moduleClasses.add(SessionManagerImpl.class);

		moduleClasses.add(ConnectionManagerImpl.class);

		return moduleClasses;
	}
}

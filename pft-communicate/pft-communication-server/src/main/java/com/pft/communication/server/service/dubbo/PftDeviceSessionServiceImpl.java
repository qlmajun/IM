package com.pft.communication.server.service.dubbo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.pft.communicate.common.dubbo.service.PftDeviceSessionService;
import com.pft.communicate.common.dubbo.service.dto.PftDeviceSessionDTO;
import com.pft.communication.server.PftCommuctionServerStarter;
import com.pft.communication.server.connector.session.LocalClientSession;
import com.pft.communication.server.connector.session.SessionManager;

/****
 * 票付通设备连接会员操作dubbo服务接口实现
 * 
 * @author majun@12301.cc
 *
 */
public class PftDeviceSessionServiceImpl implements PftDeviceSessionService {

	private SessionManager sessionManager = PftCommuctionServerStarter.getInstance().getModule(SessionManager.class);

	@Override
	public List<PftDeviceSessionDTO> getSessions() {

		Collection<LocalClientSession> list = sessionManager.getSessions();

		if (list == null || list.size() == 0) {
			return Collections.emptyList();
		}

		List<PftDeviceSessionDTO> pftDeviceSessions = new ArrayList<>(list.size());

		PftDeviceSessionDTO pftDeviceSession = null;

		for (LocalClientSession localClientSession : list) {

			String deviceId = localClientSession.getDeviceId();

			long startTimeStamp = localClientSession.getCreationDate();

			pftDeviceSession = new PftDeviceSessionDTO();

			pftDeviceSession.setDeviceId(deviceId);

			pftDeviceSession.setStartTimeStamp(startTimeStamp);

			pftDeviceSessions.add(pftDeviceSession);
		}

		return pftDeviceSessions;
	}

}

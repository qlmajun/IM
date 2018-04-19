package com.pft.communication.server.connector.session;

import java.util.Collection;
import java.util.Set;

/****
 * session管理接口声明
 * 
 * @author majun@12301.cc
 *
 */
public interface SessionManager {

	/**
	 * 添加session到集合中, 会进行冲突检测
	 *
	 * @param session
	 * @return true 表示添加成功, false 表示添加失败
	 */
	boolean addSession(LocalClientSession session);

	/****
	 * 获取本地所有客户端的session集合
	 * 
	 * @return
	 */
	Collection<LocalClientSession> getSessions();

	/***
	 * 获取本地所有session设备Id
	 * 
	 * @return
	 */
	Set<String> getSessionDeviceIds();

	/***
	 * 获取设备对应的 LocalClientSession
	 * 
	 * @param deviceId
	 *            设备Id
	 * @return
	 */
	LocalClientSession getClientSession(String deviceId);

	/****
	 * 关闭设备会话
	 * 
	 * @param deviceId
	 *            设备Id
	 * @param timestamp
	 *            时间戳
	 * @return
	 */
	boolean removeSession(String deviceId, long timestamp);
}

package com.pft.communication.server.session;

/****
 * 设备会话管理接口声明
 * 
 * @author majun@12301.cc
 *
 */
public interface PftDeviceSessionManager {

	/****
	 * 创建客户端会话
	 * 
	 * @param deviceId
	 *            设备Id
	 * @return
	 */
	boolean createClientSession(String deviceId);

	/***
	 * 销毁客户端会话
	 * 
	 * @param deviceId
	 *            设备Id
	 * @return
	 */
	boolean destoryClientSession(String deviceId);

	/***
	 * 将更改后的会话对象保存到缓存服务器
	 * 
	 * @param session
	 */
	void persistent(PftDeviceSession session);

	/***
	 * 判断指定设备的连接是否存在
	 * 
	 * @param deviceId
	 *            设备Id
	 * @return
	 */
	boolean isClientSessionExists(String deviceId);

	/****
	 * 获取指定设备的会话
	 * 
	 * @param deviceId
	 *            设备Id
	 * @return
	 */
	PftDeviceSession getClientSession(String deviceId);

	/***
	 * 判断设备是否在线
	 * 
	 * @param deviceId
	 *            设备Id
	 * @return
	 */
	boolean isDeviceAvailable(String deviceId);
}

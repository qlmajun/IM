package com.pft.communication.server.session;

import java.io.Serializable;

import com.pft.communicate.protocal.packet.PFTPacket;

/****
 * 设备连接的session对象操作接口声明
 * 
 * @author majun@12301.cc
 *
 */
public interface PftDeviceSession extends Serializable {

	/***
	 * 获取设备的Id
	 * 
	 * @return
	 */
	String getDeviceId();

	/**
	 * 获取用户的在线状态标记类, 对该对象的更改必须调用{@code setPresence(PresenceStatus status}方法保存
	 *
	 * @return 状态标示
	 */
	PresenceStatus getPresence();

	/**
	 * 更改用户的在线状态, 更改状态必须调用本方法, 否则不会生效
	 *
	 * @param presence
	 */
	void setPresence(PresenceStatus presence);

	/**
	 * 获取用户session创建的时间
	 *
	 * @return
	 */
	long getStartTimestamp();

	/**
	 * 关闭客户端连接
	 * 
	 * @param notifySessionManager
	 *            true通知SessionManager, 除非是SessionManger调用,否则均需要通知
	 * @param sendStreamEndPacket
	 *            true发送StreamEndPacket
	 * @return
	 */
	public boolean close(boolean notifySessionManager, boolean sendStreamEndPacket);

	/**
	 * 关闭客户端连接，默认发送StreamEndPacket
	 *
	 * @param notifySessionManager
	 *            true通知SessionManager, 除非是SessionManger调用,否则均需要通知
	 *
	 * @return 是否正常关闭
	 */
	boolean close(boolean notifySessionManager);

	/**
	 * 向指定的session发送报文
	 *
	 * @param packet
	 *            被发送的报文
	 * @return 是否发送成功
	 */
	boolean deliver(PFTPacket packet);

	/**
	 * 将更改保存到缓存服务器
	 */
	void persistent();

	/**
	 * 获取客户端的IP
	 * 
	 * @return
	 */
	public String getClientAddress();

}

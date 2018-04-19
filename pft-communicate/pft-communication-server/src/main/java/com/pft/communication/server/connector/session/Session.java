package com.pft.communication.server.connector.session;

import java.io.Serializable;

import com.pft.communicate.protocal.packet.PFTPacket;

public interface Session extends Serializable {

	public static final String __CLOSING__ = "__CLOSING__";

	/***
	 * 获取Session状态
	 * 
	 * @return
	 */
	public SessionStatus getStatus();

	/****
	 * 设置session状态
	 * 
	 * @param status
	 *            状态值
	 */
	public void setStatus(SessionStatus status);

	/***
	 * 获取设备Id
	 * 
	 * @return
	 */
	public String getDeviceId();

	/***
	 * 获取创建时间戳
	 * 
	 * @return
	 */
	public long getCreationDate();

	/***
	 * 获取最近活跃时间戳
	 * 
	 * @return
	 */
	public long getLastActiveDate();

	/***
	 * 退出关闭
	 */
	public void closeOnFlush();

	/***
	 * 退出关闭
	 */
	public void closeNow();

	/**
	 * 客户端链接是否已经处于正在关闭状态, 防止重复关闭
	 *
	 * @return true表示已经被关闭, 正在等待结果
	 */
	public boolean isClosing();

	/***
	 * 判断是否关闭
	 * 
	 * @return
	 */
	public boolean isClosed();

	/***
	 * 发送报文
	 * 
	 * @param packet
	 */
	public void deliver(PFTPacket packet);
}
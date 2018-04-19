package com.pft.communication.server.connector.support.configuration;

public class ServerSocketListenerSetting {

	public static final int DEFAULT_PORT = 5222;

	public static final int DEFAULT_SSL_PORT = 5223;

	/** 需要绑定的IP地址 **/
	private String bindInterface;

	/** 需要绑定的端口号, <1表示不启用 **/
	private int port;

	/** 需要绑定的安全端口号, <1表示不启用 **/
	private int sslPort;

	/** IO处理线程数量，默认等于CPU核心数量 **/
	private int ioTreads;

	/** 重用地址，默认true **/
	private boolean reuseAddress = true;

	/** 是否器用tcp_nodelay选项 **/
	private boolean tcpNoDelay = false;

	/**
	 * 设置主服务监听端口的未完成监听队列的最大值, 如果当前已经到了最大值，再新的连接来将被服务器拒绝,
	 * 如果我们给listen的backlog参数设值超过了/proc/sys/net/core/somaxconn，那么backlog参数的值为自动被改写为/proc/sys/net/core/somaxconn的值，
	 * 它的默认大小为128
	 **/
	private int backlog = 50;

	/** 设置接收缓冲区的大小 **/
	private int receiveBufferSize = -1;

	/** 设置输出缓冲区的大小 **/
	private int sendBufferSize = -1;

	/** * @see java.net.Socket#setSoLinger(boolean, int) **/
	private int solinger = -1;

	/** socket认为客户端空闲的最大时间, second **/
	private int maxInactiveInterval;

	public ServerSocketListenerSetting() {
		super();
		ioTreads = Runtime.getRuntime().availableProcessors();
	}

	public ServerSocketListenerSetting(int ioTreads) {
		super();
		this.ioTreads = ioTreads;
	}

	public int getIoTreads() {
		return ioTreads;
	}

	public boolean isReuseAddress() {
		return reuseAddress;
	}

	public int getBacklog() {
		return backlog;
	}

	public int getReceiveBufferSize() {
		return receiveBufferSize;
	}

	public int getSendBufferSize() {
		return sendBufferSize;
	}

	public int getSolinger() {
		return solinger;
	}

	protected void setIoTreads(int ioTreads) {
		this.ioTreads = ioTreads;
	}

	protected void setReuseAddress(boolean reuseAddress) {
		this.reuseAddress = reuseAddress;
	}

	protected void setBacklog(int backlog) {
		this.backlog = backlog;
	}

	protected void setReceiveBufferSize(int receiveBufferSize) {
		this.receiveBufferSize = receiveBufferSize;
	}

	protected void setSendBufferSize(int sendBufferSize) {
		this.sendBufferSize = sendBufferSize;
	}

	protected void setSolinger(int solinger) {
		this.solinger = solinger;
	}

	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}

	public void setMaxInactiveInterval(int maxInactiveInterval) {
		this.maxInactiveInterval = maxInactiveInterval;
	}

	public String getBindInterface() {
		return bindInterface;
	}

	public void setBindInterface(String bindInterface) {
		this.bindInterface = bindInterface;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getSslPort() {
		return sslPort;
	}

	public void setSslPort(int sslPort) {
		this.sslPort = sslPort;
	}

	public boolean isTcpNoDelay() {
		return tcpNoDelay;
	}

	public void setTcpNoDelay(boolean tcpNoDelay) {
		this.tcpNoDelay = tcpNoDelay;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + backlog;
		result = prime * result + ((bindInterface == null) ? 0 : bindInterface.hashCode());
		result = prime * result + ioTreads;
		result = prime * result + maxInactiveInterval;
		result = prime * result + port;
		result = prime * result + receiveBufferSize;
		result = prime * result + (reuseAddress ? 1231 : 1237);
		result = prime * result + sendBufferSize;
		result = prime * result + solinger;
		result = prime * result + sslPort;
		result = prime * result + (tcpNoDelay ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServerSocketListenerSetting other = (ServerSocketListenerSetting) obj;
		if (backlog != other.backlog)
			return false;
		if (bindInterface == null) {
			if (other.bindInterface != null)
				return false;
		} else if (!bindInterface.equals(other.bindInterface))
			return false;
		if (ioTreads != other.ioTreads)
			return false;
		if (maxInactiveInterval != other.maxInactiveInterval)
			return false;
		if (port != other.port)
			return false;
		if (receiveBufferSize != other.receiveBufferSize)
			return false;
		if (reuseAddress != other.reuseAddress)
			return false;
		if (sendBufferSize != other.sendBufferSize)
			return false;
		if (solinger != other.solinger)
			return false;
		if (sslPort != other.sslPort)
			return false;
		if (tcpNoDelay != other.tcpNoDelay)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ServerSocketListenerSetting [bindInterface=" + bindInterface + ", port=" + port + ", sslPort=" + sslPort + ", ioTreads=" + ioTreads + ", reuseAddress=" + reuseAddress + ", tcpNoDelay=" + tcpNoDelay + ", backlog=" + backlog + ", receiveBufferSize=" + receiveBufferSize + ", sendBufferSize=" + sendBufferSize + ", solinger=" + solinger + ", maxInactiveInterval=" + maxInactiveInterval + "]";
	}

}

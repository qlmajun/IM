package com.pft.communication.server.connector.mina;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.configuration2.ImmutableConfiguration;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoServiceStatistics;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pft.communicate.core.module.Module;
import com.pft.communicate.core.module.annotation.ModuleConfig;
import com.pft.communicate.core.module.lifecyle.exception.LifeCycleException;
import com.pft.communication.server.PftCommuctionServerStarter;
import com.pft.communication.server.connector.mina.encode.PftProtocalDecoder;
import com.pft.communication.server.connector.mina.encode.PftProtocalEncoder;
import com.pft.communication.server.connector.mina.security.SSLConfig;
import com.pft.communication.server.connector.support.configuration.ServerSocketListenerSetting;

@ModuleConfig(startupPriority = 200, stopPriority = 10)
public class ConnectionManagerImpl extends Module implements ConnectionManager {

	private static final Logger logger = LoggerFactory.getLogger(ConnectionManagerImpl.class);

	private ServerSocketListenerSetting serverSetting;

	private SocketAcceptor plainSocketAcceptor;
	private SocketAcceptor sslSocketAcceptor;

	public ConnectionManagerImpl() {
	}

	@Override
	protected void doInit() throws LifeCycleException {

		ImmutableConfiguration configuration = PftCommuctionServerStarter.getInstance().getConfiguration();

		serverSetting = new ServerSocketListenerSetting();

		serverSetting.setPort(configuration.getInt("pft.network.port", ServerSocketListenerSetting.DEFAULT_PORT));

		serverSetting.setSslPort(configuration.getInt("pft.network.security.port", ServerSocketListenerSetting.DEFAULT_SSL_PORT));

		serverSetting.setMaxInactiveInterval(configuration.getInt("pft.connection.inactive.interval.second", 125));

		serverSetting.setTcpNoDelay(configuration.getBoolean("pft.socket.tcp-nodelay", false));

		createClientListener();
		// createSSLClientListener();
	}

	@Override
	protected void doStart() throws LifeCycleException {
		try {
			startClientListener();
			startSSLClientListener();
		} catch (IOException e) {
			logger.error("IOException when bind socket", e);
			throw new LifeCycleException("Exception when start socket listener");
		}
	}

	@Override
	public void createClientListener() {

		plainSocketAcceptor = buildClientListener();

		DefaultIoFilterChainBuilder filterChainBuilder = plainSocketAcceptor.getFilterChain();

		// pft协议的解析器
		filterChainBuilder.addFirst("PFT", new ProtocolCodecFilter(new PftProtocalEncoder(), new PftProtocalDecoder()));

		// mina线程处理模型
		int eventHandlerThreads = PftCommuctionServerStarter.getInstance().getConfiguration().getInt("stellar.processing.threads", 16);

		ExecutorFilter executorFilter = buildExecutorFilter("plain socket executor thread - ", eventHandlerThreads);

		filterChainBuilder.addLast("threadPool", executorFilter);

	}

	@Override
	public void createSSLClientListener() {
		ImmutableConfiguration configuration = PftCommuctionServerStarter.getInstance().getConfiguration();

		String algorithm = configuration.getString("jump.socket.ssl.algorithm", "TLS");

		// Add the SSL filter now since sockets are "borned" encrypted in the
		// old ssl method
		SSLContext sslContext;
		KeyManagerFactory keyFactory;
		try {
			sslContext = SSLContext.getInstance(algorithm);
			keyFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyFactory.init(SSLConfig.getKeyStore(), SSLConfig.getKeyPassword().toCharArray());
			TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustFactory.init(SSLConfig.getc2sTrustStore());
			sslContext.init(keyFactory.getKeyManagers(), trustFactory.getTrustManagers(), new java.security.SecureRandom());
		} catch (Exception e) {
			logger.error("Error starting SSL JUMP listener", e);
			return;
		}

		SslFilter sslFilter = new SslFilter(sslContext);
		if (configuration.getString("jump.client.cert.policy", "disabled").equals("needed")) {
			sslFilter.setNeedClientAuth(true);
		} else if (configuration.getString("jump.client.cert.policy", "disabled").equals("wanted")) {
			sslFilter.setWantClientAuth(true);
		}

		sslSocketAcceptor = buildClientListener();

		DefaultIoFilterChainBuilder filterChainBuilder = sslSocketAcceptor.getFilterChain();

		filterChainBuilder.addFirst("tls", sslFilter);

		// pft协议的解析器
		filterChainBuilder.addFirst("PFT", new ProtocolCodecFilter(new PftProtocalEncoder(), new PftProtocalDecoder()));

		// mina线程处理模型
		int eventHandlerThreads = PftCommuctionServerStarter.getInstance().getConfiguration().getInt("stellar.processing.threads", 16);
		ExecutorFilter executorFilter = buildExecutorFilter("plain socket executor thread - ", eventHandlerThreads);
		filterChainBuilder.addLast("threadPool", executorFilter);

	}

	/**
	 * 创建mina线程管理的过滤器
	 *
	 * @param threadNamePrefix
	 * @param eventHandlerThreads
	 * @return
	 */
	private ExecutorFilter buildExecutorFilter(String threadNamePrefix, int eventHandlerThreads) {

		ExecutorFilter executorFilter = new ExecutorFilter();
		ThreadPoolExecutor eventExecutor = (ThreadPoolExecutor) executorFilter.getExecutor();
		final ThreadFactory originalThreadFactory = eventExecutor.getThreadFactory();

		ThreadFactory newThreadFactory = new ThreadFactory() {
			private final AtomicInteger threadId = new AtomicInteger(0);

			@Override
			public Thread newThread(Runnable runnable) {
				Thread t = originalThreadFactory.newThread(runnable);
				t.setName(threadNamePrefix + threadId.incrementAndGet());
				t.setDaemon(true);
				return t;
			}
		};

		eventExecutor.setThreadFactory(newThreadFactory);
		eventExecutor.setKeepAliveTime(60, TimeUnit.SECONDS);
		eventExecutor.setMaximumPoolSize(eventHandlerThreads + 1);
		eventExecutor.setCorePoolSize(eventHandlerThreads + 1);

		return executorFilter;
	}

	/**
	 * 创建端口监听器
	 *
	 * @return
	 */
	private SocketAcceptor buildClientListener() {
		SocketAcceptor socketAcceptor = new NioSocketAcceptor(Runtime.getRuntime().availableProcessors() + 1);
		socketAcceptor.setReuseAddress(serverSetting.isReuseAddress());
		socketAcceptor.setBacklog(serverSetting.getBacklog());

		SocketSessionConfig socketSessionConfig = socketAcceptor.getSessionConfig();
		socketSessionConfig.setReuseAddress(serverSetting.isReuseAddress());
		socketSessionConfig.setReceiveBufferSize(serverSetting.getReceiveBufferSize());
		socketSessionConfig.setSendBufferSize(serverSetting.getSendBufferSize());
		socketSessionConfig.setSoLinger(serverSetting.getSolinger());
		socketSessionConfig.setTcpNoDelay(serverSetting.isTcpNoDelay());

		// 最大不活动时间
		socketSessionConfig.setBothIdleTime(serverSetting.getMaxInactiveInterval());
		return socketAcceptor;
	}

	@Override
	public void startClientListener() throws IOException {

		String netInterface = serverSetting.getBindInterface();
		int port = serverSetting.getPort();

		if (plainSocketAcceptor == null) {
			createClientListener();
		}

		if (!plainSocketAcceptor.isActive()) {
			InetAddress bindInterface = null;
			if (netInterface != null) {
				if (netInterface.trim().length() > 0) {
					bindInterface = InetAddress.getByName(netInterface);

				}
			} else {
				bindInterface = InetAddress.getByName("0.0.0.0");
			}

			logger.info("Server start try to listen to address [" + bindInterface.getHostAddress() + ":" + port + "]");

			// 设置io hanlder
			plainSocketAcceptor.setHandler(new MinaConnectionEventHandler());

			// Start accepting connections
			plainSocketAcceptor.bind(new InetSocketAddress(bindInterface, port));
		}
	}

	@Override
	public void startSSLClientListener() throws IOException {

		if (sslSocketAcceptor == null) {
			return;
		}

		String netInterface = serverSetting.getBindInterface();
		int port = serverSetting.getSslPort();
		InetAddress bindInterface = null;
		if (netInterface != null) {
			if (netInterface.trim().length() > 0) {
				bindInterface = InetAddress.getByName(netInterface);
			}
		} else {
			bindInterface = InetAddress.getByName("0.0.0.0");
		}

		sslSocketAcceptor.setHandler(new MinaConnectionEventHandler());
		sslSocketAcceptor.bind(new InetSocketAddress(bindInterface, port));
		logger.info("Server started, listen to address [" + bindInterface.getHostAddress() + ":" + port + "] successed!");
	}

	@Override
	protected void doStop() throws LifeCycleException {
		stopClientListeners();
	}

	@Override
	public void stopClientListeners() {
		long startTs = System.currentTimeMillis();

		if (plainSocketAcceptor != null) {
			logger.info("plain text socket statics :{} ", getStatisticMessage(plainSocketAcceptor.getStatistics()));
			plainSocketAcceptor.unbind();
			plainSocketAcceptor.dispose(false);
		}

		if (sslSocketAcceptor != null) {
			logger.info("security socket statics :{} ", getStatisticMessage(sslSocketAcceptor.getStatistics()));
			sslSocketAcceptor.dispose(false);
			plainSocketAcceptor.unbind();
		}
		logger.info("stop listeners cost {}ms", System.currentTimeMillis() - startTs);

	}

	private String getStatisticMessage(IoServiceStatistics statistics) {
		StringBuilder builder = new StringBuilder();
		builder.append("Statistic [ReadBytesThroughput=");
		builder.append(statistics.getReadBytesThroughput());
		builder.append(", WrittenBytesThroughput=");
		builder.append(statistics.getWrittenBytesThroughput());
		builder.append(", ReadMessagesThroughput=");
		builder.append(statistics.getReadMessagesThroughput());
		builder.append(", WrittenMessagesThroughput=");
		builder.append(statistics.getWrittenMessagesThroughput());
		builder.append(", LargestReadBytesThroughput=");
		builder.append(statistics.getLargestReadBytesThroughput());
		builder.append(", LargestWrittenBytesThroughput=");
		builder.append(statistics.getLargestWrittenBytesThroughput());
		builder.append(", LargestReadMessagesThroughput=");
		builder.append(statistics.getLargestReadMessagesThroughput());
		builder.append(", LargestWrittenMessagesThroughput=");
		builder.append(statistics.getLargestWrittenMessagesThroughput());
		builder.append(", ReadBytes=");
		builder.append(statistics.getReadBytes());
		builder.append(", WrittenBytes=");
		builder.append(statistics.getWrittenBytes());
		builder.append(", ReadMessages=");
		builder.append(statistics.getReadMessages());
		builder.append(", WrittenMessages=");
		builder.append(statistics.getWrittenMessages());
		builder.append("]");
		return builder.toString();
	}

}

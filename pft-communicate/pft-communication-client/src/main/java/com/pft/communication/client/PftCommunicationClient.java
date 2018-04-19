package com.pft.communication.client;

import java.net.InetSocketAddress;
import java.util.Scanner;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.pft.communicate.protocal.packet.auth.AuthPacket;
import com.pft.communicate.protocal.packet.message.ContentType;
import com.pft.communicate.protocal.packet.message.MessagePacket;
import com.pft.communication.client.encode.PftProtocalDecoder;
import com.pft.communication.client.encode.PftProtocalEncoder;
import com.pft.communication.client.handler.ClientHandler;
import com.pft.communication.client.keepalive.ClientKeepAliveMessageFactoryImpl;

/***
 * 客户端
 * 
 * @author majun@12301.cc
 *
 */
public class PftCommunicationClient {

	private static final String host = "127.0.0.1";

	private static final int port = 5222;

	public static void main(String[] args) {

		// 创建连接
		SocketConnector connector = new NioSocketConnector();

		// 设置连接超时时间
		connector.setConnectTimeoutMillis(30 * 1000);

		// 绑定逻辑处理器
		connector.setHandler(new ClientHandler());

		// 设置客户端空闲时间
		// connector.getSessionConfig().setBothIdleTime(30);

		ClientKeepAliveMessageFactoryImpl ckafi = new ClientKeepAliveMessageFactoryImpl();

		KeepAliveFilter kaf = new KeepAliveFilter(ckafi, IdleStatus.READER_IDLE, KeepAliveRequestTimeoutHandler.CLOSE);

		kaf.setForwardEvent(true);

		// 设置当连接的读取通道空闲的时候，心跳包请求时间间隔
		kaf.setRequestInterval(15);

		// 设置心跳包请求后 等待反馈超时时间。 超过该时间后则调用KeepAliveRequestTimeoutHandler.CLOSE
		kaf.setRequestTimeout(30);

		connector.getFilterChain().addLast("pftClientFilter", new ProtocolCodecFilter(new PftProtocalEncoder(), new PftProtocalDecoder()));

		connector.getFilterChain().addLast("heartbeat", kaf);

		// 连接到服务器
		ConnectFuture future = connector.connect(new InetSocketAddress(host, port));

		// 等待连接创建完成
		future.awaitUninterruptibly();

		// 获得session
		IoSession session = future.getSession();

		// 发送消息
		AuthPacket authPacket = new AuthPacket("pftDevice_1");

		session.write(authPacket);

		Scanner scanner = new Scanner(System.in);

		while (scanner.hasNext()) {

			String str = scanner.next();

			// 发送消息报文
			MessagePacket messagePacket = new MessagePacket("pftDevice_1", "pftDevice_2", str, ContentType.TEXT);

			session.write(messagePacket);

			if ("bye".equals(str)) {
				scanner.close();
				session.closeNow();
				break;
			}
		}

		// 等待连接断开
		session.getCloseFuture().awaitUninterruptibly();

		connector.dispose();
	}

}

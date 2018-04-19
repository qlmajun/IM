package com.pft.communication.server.connector.session;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pft.communicate.protocal.packet.PFTPacket;
import com.pft.communicate.protocal.packet.auth.AuthPacket;
import com.pft.communicate.protocal.packet.ping.PingPacket;
import com.pft.communicate.protocal.parser.CompressStrategy;
import com.pft.communicate.protocal.parser.ParserFactory;
import com.pft.communication.server.connector.connection.ClientConnection;
import com.pft.communication.server.connector.mina.encode.PftProtocalEncoder;

public class ClientSocketConnection implements ClientConnection {

	private static final long serialVersionUID = 6040279700999298653L;

	private static final Logger logger = LoggerFactory.getLogger(ClientSocketConnection.class);

	public static final String CHARSET = "UTF-8";

	private LocalClientSession session;

	private IoSession ioSession;

	private boolean closed;

	public ClientSocketConnection(IoSession session) {
		this.ioSession = session;
		this.closed = false;
	}

	@Override
	public boolean validate() {
		if (isClosed()) {
			return false;
		}
		deliver(PingPacket.instance());
		return !isClosed();
	}

	@Override
	public void init(LocalClientSession session) {
		this.session = session;
	}

	@Override
	public byte[] getAddress() throws UnknownHostException {
		InetSocketAddress inetSocketAddress = (InetSocketAddress) ioSession.getRemoteAddress();
		return inetSocketAddress.getAddress().getAddress();
	}

	@Override
	public String getHostAddress() throws UnknownHostException {
		InetSocketAddress inetSocketAddress = (InetSocketAddress) ioSession.getRemoteAddress();
		return inetSocketAddress.getAddress().getHostAddress();
	}

	@Override
	public String getHostName() throws UnknownHostException {
		InetSocketAddress inetSocketAddress = (InetSocketAddress) ioSession.getRemoteAddress();
		return inetSocketAddress.getAddress().getHostName();
	}

	@Override
	public void closeNow() {
		close(true);
	}

	@Override
	public void closeOnFlush() {
		close(false);
	}

	@Override
	public void systemShutdown() {
		closeOnFlush();
	}

	@Override
	public boolean isClosed() {
		if (session == null) {
			return closed;
		}
		return session.getStatus() == SessionStatus.CLOSED;
	}

	@Override
	public boolean isSecure() {
		return ioSession.getFilterChain().contains("tls");
	}

	@Override
	public void deliver(PFTPacket packet) {
		deliver(packet, true);
	}

	@Override
	public void deliver(String streamID, PFTPacket packet) {
		deliver(packet);
	}

	private void deliver(PFTPacket packet, boolean asynchronous) {

		if (!isClosed()) {

			byte[] bytes;

			// 增加报文编码前压缩操作
			bytes = ParserFactory.getDefaultEncoder().encode(packet);

			boolean errorDelivering = false;
			try {
				IoBuffer buffer = new PftProtocalEncoder().encodeData(ioSession, bytes);
				if (!ioSession.isConnected()) {
					throw new IOException("Connection reset/closed by peer");
				}

				ioSession.write(buffer);
			} catch (Exception e) {
				if (packet instanceof AuthPacket) {
					logger.warn("Error delivering auth packet, with session create time " + (System.currentTimeMillis() - ioSession.getCreationTime()) + "ms", e);
				} else {
					logger.warn("Error delivering jump packet:\n" + packet, e);
				}
				errorDelivering = true;
			}
			// Close the connection if delivering text fails and we are already
			// not closing the connection
			if (errorDelivering && asynchronous) {
				closeNow();
			}
		}
	}

	@Override
	public void setCompressStrategy(CompressStrategy strategy) {
		// TODO Auto-generated method stub

	}

	@Override
	public CompressStrategy getCompressStrategy() {
		// TODO Auto-generated method stub
		return null;
	}

	private void close(boolean immediately) {
		synchronized (this) {
			if (!isClosed()) {
				if (session != null) {
					session.setStatus(SessionStatus.CLOSED);
				}
				if (immediately) {
					ioSession.closeNow();
				} else {
					ioSession.closeOnFlush();
				}
				closed = true;
			}
		}
	}

}

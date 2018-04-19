package com.pft.communication.server.connector.mina.encode;

import java.util.Arrays;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.pft.communicate.protocal.common.exception.InvalidPFTPacketException;
import com.pft.communicate.protocal.packet.Header;
import com.pft.communication.server.connector.mina.MinaConnectionEventHandler;
import com.pft.communication.server.connector.session.LocalClientSession;

public class PftProtocalDecoder extends CumulativeProtocolDecoder {

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {

		if (in.remaining() < 13) {
			return false;
		}

		in.mark();

		byte[] bytes = new byte[13];
		in.get(bytes);

		if (Header.isProtocalHeader(bytes)) {

			int packetSize = Header.getPacketSize(bytes);

			if (in.remaining() >= packetSize) {// 当前报文已经全部存在于buffer中

				byte[] bodyBytes = new byte[packetSize];

				in.get(bodyBytes);

				byte[] messageByte = byteMerger(bytes, bodyBytes);

				out.write(messageByte);

				if (in.remaining() >= 13) {
					return doDecode(session, in, out);
				}
				return !in.hasRemaining();

			} else {// buffer中的报文不完整，等待下次读取
				in.reset();
				return false;
			}
		} else {

			String address = "__unknown__";
			String hostAddress = "__unknown__";
			LocalClientSession localClientSession = (LocalClientSession) session.getAttribute(MinaConnectionEventHandler.SESSION);

			if (localClientSession != null) {
				address = localClientSession.getDeviceId();
				hostAddress = localClientSession.getConnection().getHostAddress();
			}

			throw new InvalidPFTPacketException("Invalid Packet from host " + hostAddress + " and address " + address + ", only JUMP packet support:" + Arrays.toString(bytes));
		}
	}

	/***
	 * 将消息头字节和消息体字节合并
	 *
	 * @param head
	 *            消息头字节
	 * @param body
	 *            消息体字节
	 * @return
	 */
	private byte[] byteMerger(byte[] head, byte[] body) {
		byte[] messageByte = new byte[head.length + body.length];
		System.arraycopy(head, 0, messageByte, 0, head.length);
		System.arraycopy(body, 0, messageByte, head.length, body.length);
		return messageByte;
	}

}

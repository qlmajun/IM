package com.pft.communication.client.encode;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.pft.communicate.protocal.packet.PFTPacket;
import com.pft.communicate.protocal.parser.ParserFactory;

public class PftProtocalEncoder extends ProtocolEncoderAdapter {

	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
		byte[] bytes = null;

		if (message instanceof PFTPacket) {
			bytes = ParserFactory.getDefaultEncoder().encode((PFTPacket) message);
		} else if (message instanceof byte[]) {
			bytes = (byte[]) message;
		} else if (message instanceof ByteBuffer) {
			bytes = ((ByteBuffer) message).array();
		}

		out.write(encodeData(session, bytes));
	}

	public IoBuffer encodeData(IoSession session, byte[] msgByte) throws UnsupportedEncodingException {

		IoBuffer packetBuff = IoBuffer.allocate(msgByte.length);

		packetBuff.setAutoExpand(true);

		packetBuff.put(msgByte);

		packetBuff.flip();

		return packetBuff;
	}

}

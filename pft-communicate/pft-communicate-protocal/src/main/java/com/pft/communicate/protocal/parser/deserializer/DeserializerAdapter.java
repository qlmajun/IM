package com.pft.communicate.protocal.parser.deserializer;

import com.pft.communicate.protocal.packet.Header.OpCode;
import com.pft.communicate.protocal.OpcodeMapper;
import com.pft.communicate.protocal.packet.PFTPacket;

/****
 * 报文解码器的适配器,解决每增加一个报文都需要创建解码器的问题. 由于大部分类的解码器非常简单，逻辑相同, 该类提取出公共部分。
 * 
 * @author majun@12301.cc
 *
 */
public class DeserializerAdapter implements Deserializer {

	private Class<? extends PFTPacket> clazz;

	public DeserializerAdapter(Class<? extends PFTPacket> clazz) {
		this.clazz = clazz;
	}

	@Override
	public PFTPacket deserialize(String body) {
		return DeserializerManager.getInstance().getCommonDeserializerManager().getCommonDeserializer().deserialize(body, clazz);
	}

	@Override
	public OpCode getOpCode() {
		return OpcodeMapper.getOpCodeByPacket(clazz);
	}

}

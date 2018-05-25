package xlink.cm.message;

import io.netty.buffer.ByteBuf;
import xlink.cm.message.type.CMMessageType;

public class ProductionTestEndMessage extends CMMessage {

	@Override
	public CMMessageType getMessageType() {
		// TODO Auto-generated method stub
		return CMMessageType.ProductionTestEnd;
	}

	@Override
	public void parseValue(int version, ByteBuf byteBuf) throws Exception {
		int testResultLength = byteBuf.readUnsignedShort();
		byte[] result = new byte[testResultLength];
		byteBuf.readBytes(result);
	}

	@Override
	public byte[] toPayload(int version) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}

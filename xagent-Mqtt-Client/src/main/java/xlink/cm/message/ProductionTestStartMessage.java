package xlink.cm.message;

import io.netty.buffer.ByteBuf;
import xlink.cm.message.type.CMMessageType;

public class ProductionTestStartMessage extends CMMessage{

	@Override
	public CMMessageType getMessageType() {
		// TODO Auto-generated method stub
		return CMMessageType.ProductionTestStart;
	}

	@Override
	public void parseValue(int version, ByteBuf byteBuf) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public byte[] toPayload(int version) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}

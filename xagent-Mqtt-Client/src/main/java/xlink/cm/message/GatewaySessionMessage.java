package xlink.cm.message;

import io.netty.buffer.ByteBuf;
import xlink.cm.message.type.CMMessageType;

public class GatewaySessionMessage extends CMMessage{

	
	@Override
	public CMMessageType getMessageType() {
		return CMMessageType.GatewaySession;
	}
	
	@Override
	public void parseValue(int version, ByteBuf byteBuf) throws Exception {
	}

	@Override
	public byte[] toPayload(int version) throws Exception {
		return null;
	}


}

package xlink.cm.message.sysevent;

import io.netty.buffer.ByteBuf;
import xlink.cm.message.CMMessage;
import xlink.cm.message.type.CMMessageType;

public class ServerTimeEventMessage extends CMMessage{

	@Override
	public CMMessageType getMessageType() {
		return CMMessageType.SysEvent;
	}

	@Override
	public void parseValue(int version, ByteBuf buf) throws Exception {
		
	}

	@Override
	public byte[] toPayload(int version) throws Exception {
		return new byte[0];
	}

}

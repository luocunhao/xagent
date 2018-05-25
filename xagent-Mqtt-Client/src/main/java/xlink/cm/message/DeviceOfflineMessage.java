package xlink.cm.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import xlink.cm.message.type.CMMessageType;

public class DeviceOfflineMessage extends CMMessage{
	
	@Override
	public CMMessageType getMessageType() {
		return CMMessageType.DeviceOffline;
	}

	@Override
	public void parseValue(int version, ByteBuf buf) {
	}

	@Override
	public byte[] toPayload(int version) {
		int length = 4;
		ByteBuf buf = Unpooled.buffer(length);
		buf.writeShort(getMessageType().type());
		buf.writeShort(0);
		return buf.array();
	}


}

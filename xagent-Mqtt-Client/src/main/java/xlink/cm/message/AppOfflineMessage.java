package xlink.cm.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import xlink.cm.message.type.CMMessageType;


public class AppOfflineMessage extends CMMessage {
	@Override
	public CMMessageType getMessageType() {
		return CMMessageType.AppOffline;
	}

	@Override
	public void parseValue(int version, ByteBuf buf) throws Exception {
		//None
	}

	@Override
	public byte[] toPayload(int version) throws Exception {
		ByteBuf byteBuf = Unpooled.buffer(4);
		byteBuf.writeShort(getMessageType().type());
		byteBuf.writeShort(0);
		return byteBuf.array();
	}

}

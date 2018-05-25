package xlink.cm.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import xlink.cm.message.type.CMMessageType;

public class DeviceTicketGetMessage extends CMMessage {
	
//	private int messageId;

	@Override
	public CMMessageType getMessageType() {
		return CMMessageType.DeviceTicketGet;
	}

	@Override
	public void parseValue(int version, ByteBuf byteBuf) throws Exception {
//		messageId = byteBuf.readUnsignedShort();
	}

	@Override
	public byte[] toPayload(int version) throws Exception {
//		int length = 4 + 2;
//		ByteBuf buf = Unpooled.buffer(length);
//		buf.writeShort(getMessageType().type());
//		buf.writeShort(length - 4);
//		buf.writeShort(messageId);
//		return buf.array();
		return null;
	}

//	public int getMessageId() {
//		return messageId;
//	}
//
//	public void setMessageId(int messageId) {
//		this.messageId = messageId;
//	}
	
}

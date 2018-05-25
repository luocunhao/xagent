package xlink.cm.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import xlink.cm.message.type.CMMessageType;

public class DatapointSetResultMessage extends CMMessage{
	private int messageId;
	private int code;
	
	@Override
	public CMMessageType getMessageType() {
		return CMMessageType.DatapointSetResult;
	}

	@Override
	public void parseValue(int version, ByteBuf buf) throws Exception {
//		code = value[0];
		messageId = buf.readUnsignedShort();
		code = buf.readUnsignedByte();
	}

	@Override
	public byte[] toPayload(int version) {
		int length = 4 + 2 +  1;
		ByteBuf buf =Unpooled.buffer(length);
		buf.writeShort(getMessageType().type());
		buf.writeShort(length - 4);
		buf.writeShort(messageId);
		buf.writeByte(code);
		return buf.copy().array();
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	
}

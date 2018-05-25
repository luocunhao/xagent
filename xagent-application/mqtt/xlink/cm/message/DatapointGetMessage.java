package xlink.cm.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import xlink.cm.message.type.CMMessageType;

public class DatapointGetMessage extends CMMessage{
	private int messageId;
	private int dpVersion;
	private int flag;

	@Override
	public CMMessageType getMessageType() {
		return CMMessageType.DatapointGet;
	}

	@Override
	public void parseValue(int version, ByteBuf buf) throws Exception {
		messageId = buf.readUnsignedShort();
		flag = buf.readByte();
		this.dpVersion = (flag & 0x80 )> 0 ? 2 : 1;
	}

	@Override
	public byte[] toPayload(int version) {
		int length = 4 + 2 + 1;
		ByteBuf buf = Unpooled.buffer(length);
		buf.writeShort(getMessageType().type());
		buf.writeShort(length - 4);
		buf.writeShort(messageId);
		buf.writeByte(flag);
		return buf.array();
	}



	public int getDpVersion() {
		return dpVersion;
	} 

	public void setDpVersion(int dpVersion) {
		this.dpVersion = dpVersion;
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}
	

}

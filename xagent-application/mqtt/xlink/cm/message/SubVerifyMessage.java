package xlink.cm.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import xlink.cm.message.type.CMMessageType;

public class SubVerifyMessage extends CMMessage{
	
	private int messageId;
	private int appId;
	private int ticket;

	@Override
	public CMMessageType getMessageType() {
		return CMMessageType.SubVerify;
	}

	@Override
	public void parseValue(int version, ByteBuf byteBuf) throws Exception {
		messageId = byteBuf.readUnsignedShort();
		appId = byteBuf.readInt();
		ticket = byteBuf.readInt();
	}

	@Override
	public byte[] toPayload(int version) throws Exception {
		int length = 4 + 2 + 4 + 4;
		ByteBuf buf = Unpooled.buffer(length);
		buf.writeShort(getMessageType().type());
		buf.writeShort(length - 4);
		buf.writeShort(getMessageId());
		buf.writeInt(getAppId());
		buf.writeInt(getTicket());
		return buf.array();
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public int getAppId() {
		return appId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

	public int getTicket() {
		return ticket;
	}

	public void setTicket(int ticket) {
		this.ticket = ticket;
	}

	
}

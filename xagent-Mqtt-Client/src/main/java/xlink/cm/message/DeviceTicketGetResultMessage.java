package xlink.cm.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import xlink.cm.message.type.CMMessageType;

public class DeviceTicketGetResultMessage extends CMMessage{
	
	private int messageId;
	private byte retCode;
	private int ticket;

	@Override
	public CMMessageType getMessageType() {
		return CMMessageType.DeviceTicketGetResult;
	}

	@Override
	public void parseValue(int version, ByteBuf byteBuf) throws Exception {
		messageId = byteBuf.readUnsignedShort();
		retCode = byteBuf.readByte();
		ticket = byteBuf.readInt();
	}

	@Override
	public byte[] toPayload(int version) throws Exception {
		int length = 4 + 2 + 1 + 4;
		ByteBuf buf = Unpooled.buffer(length);
		buf.writeShort(getMessageType().type());
		buf.writeShort(length - 4);
		buf.writeShort(messageId);
		buf.writeByte(retCode);
		buf.writeInt(ticket);
		return buf.array();
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public byte getRetCode() {
		return retCode;
	}

	public void setRetCode(byte retCode) {
		this.retCode = retCode;
	}

	public int getTicket() {
		return ticket;
	}

	public void setTicket(int ticket) {
		this.ticket = ticket;
	}

	
}

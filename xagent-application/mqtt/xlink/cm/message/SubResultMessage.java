package xlink.cm.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import xlink.cm.message.type.CMMessageType;
import xlink.cm.message.type.SubRetCode;

public class SubResultMessage extends CMMessage{
	
	private int messageId;
	private SubRetCode code;
	private int deviceId;
	private int ticket;
	
	@Override
	public CMMessageType getMessageType() {
		return CMMessageType.SubResult;
	}

	@Override
	public void parseValue(int version, ByteBuf byteBuf) throws Exception {
		messageId = byteBuf.readUnsignedShort();
		code = SubRetCode.fromCode(byteBuf.readByte());
		deviceId = byteBuf.readInt();
		ticket = byteBuf.readInt();
	}

	@Override
	public byte[] toPayload(int version) throws Exception {
		int length = 4 + 2 + 1 + 4 + 4;
		ByteBuf buf = Unpooled.buffer(length);
		buf.writeShort(getMessageType().type());
		buf.writeShort(length - 4);
		buf.writeShort(getMessageId());
		buf.writeByte(getCode().code());
		buf.writeInt(getDeviceId());
		buf.writeInt(getTicket());
		return buf.array();
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public SubRetCode getCode() {
		return code;
	}

	public void setCode(SubRetCode code) {
		this.code = code;
	}

	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	public int getTicket() {
		return ticket;
	}

	public void setTicket(int ticket) {
		this.ticket = ticket;
	}
	

}

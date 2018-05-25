package xlink.cm.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;

import xlink.cm.message.type.CMMessageType;
import xlink.cm.message.type.NotifyFromType;
import xlink.cm.message.type.NotifyMessageType;
import xlink.cm.message.type.NotifyMsgEncodeType;

public class DeviceNotifyEventMessage extends CMMessage{

	private NotifyFromType fromType;
	private int fromId;
	private NotifyMessageType messageType;
	private byte flag;
	private NotifyMsgEncodeType msgEncodeType; 
	private String msg;
	
	@Override
	public CMMessageType getMessageType() {
		return CMMessageType.DeviceNotifyEvent;
	}

	@Override
	public void parseValue(int version, ByteBuf buf) throws Exception {
//		ByteBuf buf = Unpooled.wrappedBuffer(value);
		fromType = NotifyFromType.fromType(buf.readByte());
		messageType = NotifyMessageType.fromType(buf.readUnsignedShort());
		fromId = buf.readInt();
		flag = buf.readByte();
		msgEncodeType = (flag & 0x80) > 0 ? NotifyMsgEncodeType.String: NotifyMsgEncodeType.ByteArray;
		switch (msgEncodeType) {
		case String:
			int msgLength = buf.readShort();
			msg = buf.readBytes(msgLength).toString(Charset.forName("UTF-8"));
			break;
		case ByteArray:
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public byte[] toPayload(int version) throws Exception {
		ByteBuf nofityBuf = Unpooled.buffer();
		nofityBuf.writeByte(fromType.type());
		nofityBuf.writeInt(fromId);
		nofityBuf.writeShort(messageType.type());
		nofityBuf.writeByte(flag);
		byte[] msgBytes = msg.getBytes();
		nofityBuf.writeShort(msgBytes.length);
		nofityBuf.writeBytes(msgBytes);
		byte[] notifyBytes = nofityBuf.copy().array();
		ByteBuf buf = Unpooled.buffer(4 + notifyBytes.length);
		buf.writeShort(getMessageType().type());
		buf.writeShort(notifyBytes.length);
		buf.writeBytes(notifyBytes);
		return buf.array();
	}
	
	

	public void setMessageType(NotifyMessageType messageType) {
		this.messageType = messageType;
	}

	public NotifyFromType getFromType() {
		return fromType;
	}

	public void setFromType(NotifyFromType fromType) {
		this.fromType = fromType;
	}

	public int getFromId() {
		return fromId;
	}

	public void setFromId(int fromId) {
		this.fromId = fromId;
	}

	public byte getFlag() {
		return flag;
	}

	public void setFlag(byte flag) {
		this.flag = flag;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}


	public NotifyMsgEncodeType getMsgEncodeType() {
		return msgEncodeType;
	}

	public void setMsgEncodeType(NotifyMsgEncodeType msgEncodeType) {
		this.msgEncodeType = msgEncodeType;
	}
	
	
}

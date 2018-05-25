package xlink.cm.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;

import xlink.cm.message.type.CMMessageType;
import xlink.cm.message.type.NotifyFromType;
import xlink.cm.message.type.NotifyMessageType;
import xlink.cm.message.type.NotifyMsgEncodeType;

public class AppNotifyEventMessage extends CMMessage {

	private NotifyFromType      fromType;
	private int                 fromId;
	private byte                flag;
	private NotifyMessageType   messageType;
	private NotifyMsgEncodeType msgEncodeType;
	private String              msg;


	@Override
	public CMMessageType getMessageType() {
		return CMMessageType.AppNotifyEvent;
	}

	@Override
	public void parseValue(int version, ByteBuf buf) throws Exception {
		//		ByteBuf buf = Unpooled.wrappedBuffer(value);
		fromType = NotifyFromType.fromType(buf.readByte());
		fromId = buf.readInt();
		messageType = NotifyMessageType.fromType(buf.readUnsignedShort());
		flag = buf.readByte();
		msgEncodeType = (flag & 0x80) > 0 ? NotifyMsgEncodeType.String : NotifyMsgEncodeType.ByteArray;
		int msgLength = buf.readShort();
		msg = buf.readBytes(msgLength).toString(Charset.forName("UTF-8"));

	}

	@Override
	public byte[] toPayload(int version) throws Exception {
		flag = 0;
		switch (fromType) {
			case Server:
				flag |= 0x80;
				break;
			case Device:
				flag |= 0x40;
				break;
			case App:
				flag |= 0x20;
				break;
		}
		ByteBuf notifyBuf = Unpooled.buffer();
		notifyBuf.writeByte(fromType.type());
		notifyBuf.writeInt(fromId);
		notifyBuf.writeShort(messageType.type());
		notifyBuf.writeByte(flag);
		byte[] msgBytes = msg.getBytes();
		notifyBuf.writeShort(msgBytes.length);
		notifyBuf.writeBytes(msgBytes);
		byte[]  notifyBytes = notifyBuf.copy().array();
		ByteBuf buf         = Unpooled.buffer(4 + notifyBytes.length);
		buf.writeShort(getMessageType().type());
		buf.writeShort(notifyBytes.length);
		buf.writeBytes(notifyBytes);
		return buf.array();
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

	public void setMessageType(NotifyMessageType messageType) {
		this.messageType = messageType;
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

	public NotifyMsgEncodeType getMsgEncodeType() {
		return msgEncodeType;
	}

	public void setMsgEncodeType(NotifyMsgEncodeType msgEncodeType) {
		this.msgEncodeType = msgEncodeType;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}


}

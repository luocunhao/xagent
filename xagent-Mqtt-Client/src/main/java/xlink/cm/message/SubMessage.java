package xlink.cm.message;

import java.nio.charset.Charset;

import cn.xlink.cmmqttclient.core.utils.Utils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import xlink.cm.message.type.CMMessageType;

public class SubMessage extends CMMessage{
	
	private int messageId;
	private String mac;
	private String productId;
	private int ticket;

	@Override
	public CMMessageType getMessageType() {
		return CMMessageType.Sub;
	}

	@Override
	public void parseValue(int version, ByteBuf byteBuf) throws Exception {
		messageId = byteBuf.readUnsignedShort();
		short macLength = byteBuf.readShort();
		StringBuffer mac = new StringBuffer();
		for (int i = 0; i < macLength; i++) {
			int b = (int) ( 0xFF & byteBuf.readByte() );
			mac.append(String.format("%02X", b));// 统一大写，没有冒号
		}
		this.mac = mac.toString();
		short pidLength = byteBuf.readShort();
		byte[] pidBytes = new byte[pidLength];
		byteBuf.readBytes(pidBytes);
		productId = new String(pidBytes, Charset.forName("UTF-8"));
		ticket = byteBuf.readInt();
	}

	@Override
	public byte[] toPayload(int version) throws Exception {
		byte[] macBytes = Utils.hexStringToByteArray(getMac());
		byte[] pidBytes = productId.getBytes();
		int length = 4 + 2 + 2 + macBytes.length + 2 + pidBytes.length + 4;
		ByteBuf buf = Unpooled.buffer(length);
		buf.writeShort(getMessageType().type());
		buf.writeShort(length - 4);
		buf.writeShort(getMessageId());
		buf.writeShort(macBytes.length);
		buf.writeBytes(macBytes);
		buf.writeShort(pidBytes.length);
		buf.writeBytes(pidBytes);
		buf.writeInt(getTicket());
		return buf.array();
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public int getTicket() {
		return ticket;
	}

	public void setTicket(int ticket) {
		this.ticket = ticket;
	}

}

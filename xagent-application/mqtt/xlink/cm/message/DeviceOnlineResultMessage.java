package xlink.cm.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import xlink.cm.message.type.CMMessageType;

public class DeviceOnlineResultMessage extends CMMessage {
	
	public static enum Code{
		Success(0),
		DeviceKeyError(1),
		CertificateUnauthorize(2),
		ServerUnavailable(3)
		;
		private final int code;
		private Code(int code) {
			this.code = code;
		}
		public int code(){
			return code;
		}
	}

	private int code;
	
	@Override
	public CMMessageType getMessageType() {
		return CMMessageType.DeviceOnlineResult;
	}

	@Override
	public void parseValue(int version, ByteBuf buf) {
//		code = value[0];
		code = buf.readUnsignedByte();
	}

	@Override
	public byte[] toPayload(int version) {
		int length = 4 + 1;
		ByteBuf buf = Unpooled.buffer(length);
		buf.writeShort(getMessageType().type());
		buf.writeShort(length - 4);
		buf.writeByte(code);
		return buf.array();
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

}

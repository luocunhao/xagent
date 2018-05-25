package xlink.cm.message.sysevent;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import xlink.cm.exception.CMMessageEncodeException;
import xlink.cm.exception.CMMessageParseException;
import xlink.cm.message.CMMessage;
import xlink.cm.message.type.CMMessageType;
import xlink.cm.message.type.SysEventMessageType;

public class KickedOfflineEventMessage extends CMMessage{
	
	private boolean kickDevice;
	private boolean kickApp;
	private int deviceId;
	private int appId;
	private byte reason;
	

	@Override
	public void parseValue(int version, ByteBuf buf) throws Exception {
//		ByteBuf buf = Unpooled.wrappedBuffer(value);
		byte flag = buf.readByte();
		//两个标志位是互斥的
		if (((flag & 0x80) > 0 && (flag & 0x40) > 0)  || (flag & 0x80) == 0 && (flag & 0x40) == 0 ) {
			throw new CMMessageParseException("Unkown KickedOffline Flags : " + flag);
		}
		if ((flag & 0x80) > 0 ) {
			kickDevice = true;
			deviceId = buf.readInt();
		}
		if ((flag & 0x40) > 0 ) {
			kickApp = true;
			appId = buf.readInt();
		}
		reason = buf.readByte();		
	}

	@Override
	public byte[] toPayload(int version) throws Exception {
		ByteBuf valueBuf = Unpooled.buffer();
		byte flag = 0;
		if ((isKickDevice() && isKickApp()) || (!isKickDevice() && !isKickApp())) {
			throw new CMMessageEncodeException("can not write KickedOffline flag.");
		}
		if (isKickDevice()) {
			flag |= 0x80;
		}
		if (isKickApp()) {
			flag |= 0x40;
		}
		valueBuf.writeByte(flag);
		if (isKickDevice()) {
			valueBuf.writeInt(deviceId);
		}
		if (isKickApp()) {
			valueBuf.writeInt(appId);
		}
		valueBuf.writeByte(reason);
		byte[] value = valueBuf.copy().array();
		
		int length = 4 + 4 + value.length;
		ByteBuf buf = Unpooled.buffer(length);
		buf.writeShort(getMessageType().type());
		buf.writeShort(length - 4);
		buf.writeShort(SysEventMessageType.KickedOffline.type());
		buf.writeShort(value.length);
		buf.writeBytes(value);
		return buf.array();
	}

	public boolean isKickDevice() {
		return kickDevice;
	}

	public void setKickDevice(boolean kickDevice) {
		this.kickDevice = kickDevice;
	}

	public boolean isKickApp() {
		return kickApp;
	}

	public void setKickApp(boolean kickApp) {
		this.kickApp = kickApp;
	}

	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	public int getAppId() {
		return appId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

	public byte getReason() {
		return reason;
	}

	public void setReason(byte reason) {
		this.reason = reason;
	}

	@Override
	public CMMessageType getMessageType() {
		return CMMessageType.SysEvent;
	}

	
	

	
}

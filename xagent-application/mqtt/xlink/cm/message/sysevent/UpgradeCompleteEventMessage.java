package xlink.cm.message.sysevent;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import xlink.cm.exception.CMMessageEncodeException;
import xlink.cm.exception.CMMessageParseException;
import xlink.cm.message.CMMessage;
import xlink.cm.message.type.CMMessageType;
import xlink.cm.message.type.SysEventMessageType;

public class UpgradeCompleteEventMessage extends CMMessage {

	private int deviceId;
	private boolean isFirmwareUpgrade;
	private boolean isMCUUpgrade;
	private int retcode;
	private int beforeVersion;
	private int afterVersion;
	

	@Override
	public CMMessageType getMessageType() {
		return CMMessageType.SysEvent;
	}

	@Override
	public void parseValue(int version, ByteBuf buf) throws Exception {
//		ByteBuf buf = Unpooled.wrappedBuffer(value);
		deviceId = buf.readInt();
		byte flag = buf.readByte();
		// 两个标志位是互斥的
		if (( ( flag & 0x80 ) > 0 && ( flag & 0x40 ) > 0 ) || ( flag & 0x80 ) == 0 && ( flag & 0x40 ) == 0) {
			throw new CMMessageParseException("Unkown UpgradeComplete Flags : " + flag);
		}
		if (( flag & 0x80 ) > 0) {
			isFirmwareUpgrade = true;
		}

		if (( flag & 0x40 ) > 0) {
			isMCUUpgrade = true;
		}
		retcode = buf.readByte();
		beforeVersion = buf.readShort();
		afterVersion = buf.readShort();
	}

	@Override
	public byte[] toPayload(int version) throws Exception {
		ByteBuf valueBuf = Unpooled.buffer();
		valueBuf.writeInt(deviceId);
		byte flag = 0;
		if ((isFirmwareUpgrade() && isMCUUpgrade()) || (!isFirmwareUpgrade() && !isMCUUpgrade())) {
			throw new CMMessageEncodeException("can not write UpgradeComplete flag.");
		}
		if (isFirmwareUpgrade()) {
			flag |= 0x80;
		}
		if (isMCUUpgrade()) {
			flag |= 0x40;
		}
		valueBuf.writeByte(flag);
		valueBuf.writeByte(retcode);
		valueBuf.writeShort(beforeVersion);
		valueBuf.writeShort(afterVersion);
		byte[] value = valueBuf.copy().array();
		
		int length = 4 + 4 + value.length;
		ByteBuf buf = Unpooled.buffer(length);
		buf.writeShort(getMessageType().type());
		buf.writeShort(length - 4);
		buf.writeShort(SysEventMessageType.UpgradeComplete.type());
		buf.writeShort(value.length);
		buf.writeBytes(value);
		return buf.copy().array();
	}



	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	public boolean isFirmwareUpgrade() {
		return isFirmwareUpgrade;
	}

	public void setFirmwareUpgrade(boolean isFirmwareUpgrade) {
		this.isFirmwareUpgrade = isFirmwareUpgrade;
	}

	public boolean isMCUUpgrade() {
		return isMCUUpgrade;
	}

	public void setMCUUpgrade(boolean isMCUUpgrade) {
		this.isMCUUpgrade = isMCUUpgrade;
	}

	public int getRetcode() {
		return retcode;
	}

	public void setRetcode(int retcode) {
		this.retcode = retcode;
	}

	public int getBeforeVersion() {
		return beforeVersion;
	}

	public void setBeforeVersion(int beforeVersion) {
		this.beforeVersion = beforeVersion;
	}

	public int getAfterVersion() {
		return afterVersion;
	}

	public void setAfterVersion(int afterVersion) {
		this.afterVersion = afterVersion;
	}


}

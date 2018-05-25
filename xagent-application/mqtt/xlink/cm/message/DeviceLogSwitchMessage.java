package xlink.cm.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import xlink.cm.message.type.CMMessageType;
import xlink.cm.message.type.DeviceLogLevelType;

public class DeviceLogSwitchMessage extends CMMessage{

	private boolean isOpen;
	private DeviceLogLevelType level;
	private byte flag; 

	@Override
	public CMMessageType getMessageType() {
		return CMMessageType.DeviceLogSwitch;
	}

	@Override
	public void parseValue(int version, ByteBuf byteBuf) throws Exception {
		flag = byteBuf.readByte();
		//level(4)+ type(12)  
		int lv = flag;
		lv = lv >> 4;
		level = DeviceLogLevelType.fromType(lv);
		isOpen = (flag & 0x8) > 1;
	}

	@Override
	public byte[] toPayload(int version) throws Exception {
		int length = 4 + 1;
		ByteBuf buf = Unpooled.buffer(length);
		buf.writeShort(getMessageType().type());
		buf.writeShort(length - 4);
		buf.writeByte(flag);
		return buf.copy().array();
	}

	public boolean isOpen() {
		return isOpen;
	}

	public DeviceLogLevelType getLevel() {
		return level;
	}

	public byte getFlag() {
		return flag;
	}

	public void setFlag(byte flag) {
		this.flag = flag;
	}

	
}

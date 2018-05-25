package xlink.cm.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import xlink.cm.message.type.DeviceStateType;

public class DeviceStateOfflineMessage extends DeviceStateMessage{

	/**
	 * 下线原因
	 */
	private int reasonCode;
	
	public DeviceStateOfflineMessage(){
		
	}
	
	public DeviceStateOfflineMessage(int reasonCode) {
		super();
		stateType = DeviceStateType.Offline;
		this.reasonCode = reasonCode;
	}

	@Override
	protected ByteBuf toStatePayload(int version) {
		return Unpooled.buffer(1).writeByte(this.reasonCode);
	}

	@Override
	protected void parserStatePayload(int payloadLengh, ByteBuf byteBuf) {
		reasonCode = byteBuf.readByte();
		
	}

	@Override
	public String toString() {
		return "DeviceStateOfflineMessage [reasonCode=" + reasonCode + "]";
	}
	
	
}

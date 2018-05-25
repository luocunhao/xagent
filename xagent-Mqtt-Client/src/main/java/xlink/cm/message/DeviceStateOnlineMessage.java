package xlink.cm.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import xlink.cm.message.type.DeviceStateType;

public class DeviceStateOnlineMessage extends DeviceStateMessage{

	public DeviceStateOnlineMessage(){
		stateType = DeviceStateType.Online;
	}
	

	
	@Override
	protected ByteBuf toStatePayload(int version) {
		// TODO Auto-generated method stub
		return Unpooled.buffer(0);
	}

	@Override
	protected void parserStatePayload(int payloadLengh, ByteBuf byteBuf) {
		// TODO Auto-generated method stub
		
	}

	
	
}

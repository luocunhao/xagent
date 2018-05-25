package xlink.cm.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import xlink.cm.message.type.AppStateType;

public class AppStateOnlineMessage extends AppStateMessage{
	
	private String resource;

	public AppStateOnlineMessage(){
		
	}
	
	public AppStateOnlineMessage(String resource) {
		super();
		stateType = AppStateType.Online;
		this.resource = resource;
	}

	@Override
	protected ByteBuf toStatePayload(int version) {
		if(resource == null){
			return Unpooled.buffer(0);
		}
		return Unpooled.buffer(resource.length()+1).writeByte(resource.length()).writeBytes(resource.getBytes());
	}

	@Override
	protected void parserStatePayload(int payloadLengh, ByteBuf byteBuf) {
		int size = byteBuf.readUnsignedByte();
		byte[] data = new byte[size];
		byteBuf.readBytes(data);
		resource = new String(data);		
	}

	@Override
	public String toString() {
		return "AppStateOnlineMessage [resource=" + resource + "]";
	}

	
}

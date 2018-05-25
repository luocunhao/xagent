package xlink.cm.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import xlink.cm.message.type.AppStateType;

public class AppStateOfflineMessage extends AppStateMessage{
	
	private String resource;
	private int reasonCode;

	public AppStateOfflineMessage(){
		
	}

	public AppStateOfflineMessage(String resource, int reasonCode) {
		super();
		stateType = AppStateType.Offline;
		this.resource = resource;
		this.reasonCode = reasonCode;
	}

	@Override
	protected ByteBuf toStatePayload(int version) {
		ByteBuf buf = Unpooled.buffer();
		if(resource == null){
			buf.writeShort(0);
			
		}else{
			buf.writeByte(resource.length()).writeBytes(resource.getBytes());	
		}
		buf.writeByte(reasonCode);
		return buf;
	}

	@Override
	protected void parserStatePayload(int payloadLengh, ByteBuf byteBuf) {
		int size = byteBuf.readUnsignedByte();
		byte[] data = new byte[size];
		byteBuf.readBytes(data);
		resource = new String(data);	
		reasonCode = byteBuf.readByte();
	}

	@Override
	public String toString() {
		return "AppStateOfflineMessage [resource=" + resource + ", reasonCode=" + reasonCode + "]";
	}

	
}

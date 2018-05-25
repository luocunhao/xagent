package xlink.cm.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import xlink.cm.message.type.AppStateType;
import xlink.cm.message.type.CMMessageType;

public abstract class AppStateMessage extends CMMessage {
	
	protected AppStateType stateType;
	
	@Override
	public CMMessageType getMessageType() {
		// TODO Auto-generated method stub
		return CMMessageType.AppState;
	}
	
	public static AppStateMessage parseValueTemplate(int version,ByteBuf byteBuf) throws Exception{
		byteBuf.markReaderIndex();
		AppStateType state = AppStateType.fromType(byteBuf.readShort());
		byteBuf.resetReaderIndex();
		
		switch(state){
			case Online:{
				AppStateOnlineMessage stateOnlineMsg = new AppStateOnlineMessage();
				stateOnlineMsg.parseValue(version, byteBuf);
				return stateOnlineMsg;
			}
			case Offline:{
				AppStateOfflineMessage stateOfflineMsg = new AppStateOfflineMessage();
				stateOfflineMsg.parseValue(version, byteBuf);
				return stateOfflineMsg;
			}
			default:
				return null;
		}
	}

	@Override
	public void parseValue(int version, ByteBuf byteBuf) throws Exception {
		int state = byteBuf.readShort();
		this.stateType = AppStateType.fromType(state);
		int payloadLengh = byteBuf.readUnsignedShort();
		parserStatePayload(payloadLengh,byteBuf);
		
	}

	@Override
	public byte[] toPayload(int version) throws Exception {
		ByteBuf stateBuf = toStatePayload(version);
		int length = 4 + 4 + stateBuf.readableBytes();
		ByteBuf buf = Unpooled.buffer(length);
		buf.writeShort(getMessageType().type());
		buf.writeShort(length - 4);
		buf.writeShort((this.stateType.type() & 0xFFFF));
		buf.writeShort((stateBuf.readableBytes() & 0xFFFF));
		buf.writeBytes(stateBuf);
		return buf.array();
	}

	abstract protected ByteBuf toStatePayload(int version);
	
	abstract protected void parserStatePayload(int payloadLengh,ByteBuf byteBuf);
}

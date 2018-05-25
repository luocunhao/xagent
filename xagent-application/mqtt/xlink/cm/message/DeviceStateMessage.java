package xlink.cm.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import xlink.cm.message.type.CMMessageType;
import xlink.cm.message.type.DeviceStateType;

public abstract class DeviceStateMessage extends CMMessage {
	
	protected DeviceStateType stateType;

	@Override
	public CMMessageType getMessageType() {
		return CMMessageType.DeviceState;
	}

  public DeviceStateType getStateType() {
    return stateType;
  }

  public static DeviceStateMessage parserValueTemplate(int version, ByteBuf byteBuf)
      throws Exception {
		byteBuf.markReaderIndex();
		DeviceStateType state = DeviceStateType.fromType(byteBuf.readShort());
		byteBuf.resetReaderIndex();
		
		switch(state){
			case Online:{
				DeviceStateOnlineMessage stateOnlineMsg = new DeviceStateOnlineMessage();
				stateOnlineMsg.parseValue(version, byteBuf);
				return stateOnlineMsg;
			}
			case Offline:{
				DeviceStateOfflineMessage stateOfflineMsg = new DeviceStateOfflineMessage();
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
		this.stateType = DeviceStateType.fromType(state);
		int payloadLengh = byteBuf.readUnsignedShort();
		parserStatePayload(payloadLengh,byteBuf);
		
		
	}

	@Override
	public byte[] toPayload(int version) throws Exception {
		
		ByteBuf stateBuf = toStatePayload(version);
		int length = 4 + 4 + stateBuf.readableBytes();
		ByteBuf buf = Unpooled.buffer(length);
		buf.writeShort(getMessageType().type());
		buf.writeShort(((length - 4) & 0xFFFF));
		buf.writeShort((this.stateType.type() & 0xFFFF));
		buf.writeShort((stateBuf.readableBytes()& 0xFFFF));
		buf.writeBytes(stateBuf,stateBuf.readableBytes());
		return buf.array();
	}
	
	abstract protected ByteBuf toStatePayload(int version);
	
	abstract protected void parserStatePayload(int payloadLengh,ByteBuf byteBuf);

}

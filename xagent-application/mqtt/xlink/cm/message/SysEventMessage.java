package xlink.cm.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import xlink.cm.exception.CMMessageParseException;
import xlink.cm.message.sysevent.KickedOfflineEventMessage;
import xlink.cm.message.sysevent.ServerTimeEventMessage;
import xlink.cm.message.sysevent.ServerTimeResponseEventMessage;
import xlink.cm.message.sysevent.UpgradeCompleteEventMessage;
import xlink.cm.message.sysevent.UpgradeEventMessage;
import xlink.cm.message.type.CMMessageType;
import xlink.cm.message.type.SysEventMessageType;

public class SysEventMessage extends CMMessage{
	
	private SysEventMessageType eventType = null;
//	private byte[] eventValue;
	private ByteBuf eventValue;
	

	@Override
	public CMMessageType getMessageType() {
		return CMMessageType.SysEvent;
	}
	
	@Override
	public void parseValue(int version, ByteBuf buf) throws Exception {
//		ByteBuf buf = Unpooled.wrappedBuffer(value);
		short sysEventType = buf.readShort();
		this.eventType = SysEventMessageType.fromType(sysEventType);
		if (eventType == null) {
			throw new CMMessageParseException("unkown sys event type: " + sysEventType);
		}
		short eventLength = buf.readShort();
//		eventValue = new byte[eventLength];
		eventValue = buf.readBytes(eventLength);
		
	}
	
	@Override
	public byte[] toPayload(int version) throws Exception {
		int length = 4 + 4 + eventValue.readableBytes();
		ByteBuf buf = Unpooled.buffer(length);
		buf.writeShort(getMessageType().type());
		buf.writeShort(length - 4);
		buf.writeShort(eventType.type());
		buf.writeShort(eventValue.readableBytes());
		buf.writeBytes(eventValue);
		return buf.copy().array();
	}
	
	
	public SysEventMessageType getEventType() {
		return eventType;
	}

	public ByteBuf getEventValue() {
		return eventValue;
	}
	

	public static CMMessage funcParseSysEventMessage(int version, SysEventMessage sysEventMessage) throws Exception{
		switch (sysEventMessage.getEventType()) {
		case KickedOffline:{
			KickedOfflineEventMessage kickedOfflineEventMessage = new KickedOfflineEventMessage();
			kickedOfflineEventMessage.parseValue(version, sysEventMessage.getEventValue());
			return kickedOfflineEventMessage;
		}
		case ServerTime:{
			ServerTimeEventMessage serverTimeEventMessage = new ServerTimeEventMessage();
			serverTimeEventMessage.parseValue(version, sysEventMessage.getEventValue());
			return serverTimeEventMessage;
		}
		case ServerTimeResponse: {
			ServerTimeResponseEventMessage serverTimeResponseEventMessage = new ServerTimeResponseEventMessage();
			serverTimeResponseEventMessage.parseValue(version, sysEventMessage.getEventValue());
			return serverTimeResponseEventMessage;
		}
		case Upgrade:{
			UpgradeEventMessage upgradeEventMessage = new UpgradeEventMessage();
			upgradeEventMessage.parseValue(version, sysEventMessage.getEventValue());
			return upgradeEventMessage;
		}
		case UpgradeComplete:{
			UpgradeCompleteEventMessage upgradeCompleteEventMessage = new UpgradeCompleteEventMessage();
			upgradeCompleteEventMessage.parseValue(version, sysEventMessage.getEventValue());
			return upgradeCompleteEventMessage;
		}
		default:
			return null;
		}
	}
}

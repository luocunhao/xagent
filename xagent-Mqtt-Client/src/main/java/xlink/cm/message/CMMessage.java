package xlink.cm.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttQoS;
import xlink.cm.message.type.CMMessageType;

public abstract class CMMessage {
	private MqttQoS qoS;
	private int mqttMessageId;
	protected static final Logger logger = LoggerFactory.getLogger(CMMessage.class);

	public static class TLV {
		private final int type;
		private final int length;
		private final byte[] value;

		public TLV(int type, int length, byte[] value) {
			super();
			this.type = type;
			this.length = length;
			this.value = value;
		}

		public int getType() {
			return type;
		}

		public int getLength() {
			return length;
		}

		public byte[] getValue() {
			return value;
		}
	}

	protected static TLV readTLV(ByteBuf buf) {
		int type = buf.readUnsignedShort();
		int length = buf.readUnsignedShort();
		byte[] value = new byte[length];
		buf.readBytes(value);
		buf.readBytes(length);
		return new TLV(type, length, value);
	}

	public abstract CMMessageType getMessageType();

	public abstract void parseValue(int version, ByteBuf byteBuf) throws Exception;

	public abstract byte[] toPayload(int version) throws Exception;

	public MqttQoS getQoS() {
		return qoS;
	}

	public void setQoS(MqttQoS qoS) {
		this.qoS = qoS;
	}


	public int getMqttMessageId() {
		return mqttMessageId;
	}

	public void setMqttMessageId(int mqttMessageId) {
		this.mqttMessageId = mqttMessageId;
	}

	public static CMMessage funcParseMessage(int version, ByteBuf byteBuf) throws Exception {
		int type = byteBuf.readUnsignedShort();
		int length = byteBuf.readUnsignedShort();
		byte[] valueBytes = new byte[length];
//		ByteBuf value =	byteBuf.readBytes(length);
		byteBuf.readBytes(valueBytes);
		byteBuf.release();
		ByteBuf value = Unpooled.wrappedBuffer(valueBytes);
		CMMessageType messageType = CMMessageType.fromType(type);
		switch (messageType) {
		case Activation: {
			ActivationMessage activationMessage = new ActivationMessage();
			activationMessage.parseValue(version, value);
			return activationMessage;
		}
		case ActivetionResult: {
			ActivationResultMessage activationResultMessage = new ActivationResultMessage();
			activationResultMessage.parseValue(version, value);
			return activationResultMessage;
		}
		case DeviceOnline: {
			DeviceOnlineMessage deviceOnlineMessage = new DeviceOnlineMessage();
			deviceOnlineMessage.parseValue(version, value);
			return deviceOnlineMessage;
		}
		case DeviceOnlineResult: {
			DeviceOnlineResultMessage deviceOnlineResultMessage = new DeviceOnlineResultMessage();
			deviceOnlineResultMessage.parseValue(version, value);
			return deviceOnlineResultMessage;
		}
		case DeviceOffline: {
			DeviceOfflineMessage deviceOfflineMessage = new DeviceOfflineMessage();
			deviceOfflineMessage.parseValue(version, value);
			return deviceOfflineMessage;
		}
		case DatapointSync: {
			DatapointSyncMessage datapointSyncMessage = new DatapointSyncMessage();
			datapointSyncMessage.parseValue(version, value);
			return datapointSyncMessage;
		}
		case DatapointSet: {
			DatapointSetMessage datapointSetMessage = new DatapointSetMessage();
			datapointSetMessage.parseValue(version, value);
			return datapointSetMessage;
		}
		case DatapointSetResult: {
			DatapointSetResultMessage datapointSetResultMessage = new DatapointSetResultMessage();
			datapointSetResultMessage.parseValue(version, value);
			return datapointSetResultMessage;
		}
		case DatapointGet: {
			DatapointGetMessage datapointGetMessage = new DatapointGetMessage();
			datapointGetMessage.parseValue(version, value);
			return datapointGetMessage;
		}
		case DatapointGetResult: {
			DatapointGetResultMessage datapointGetResultMessage = new DatapointGetResultMessage();
			datapointGetResultMessage.parseValue(version, value);
			return datapointGetResultMessage;
		}
		case AppOffline: {
			AppOfflineMessage appOfflineMessage = new AppOfflineMessage();
			appOfflineMessage.parseValue(version, value);
			return appOfflineMessage;
		}
		case DeviceNotifyEvent: {
			DeviceNotifyEventMessage deviceNotifyEventMessage = new DeviceNotifyEventMessage();
			deviceNotifyEventMessage.parseValue(version, value);
			return deviceNotifyEventMessage;
		}
		case AppNotifyEvent: {
			AppNotifyEventMessage appNotifyEventMessage = new AppNotifyEventMessage();
			appNotifyEventMessage.parseValue(version, value);
			return appNotifyEventMessage;
		}
		case SysEvent: {
			SysEventMessage sysEventMessage = new SysEventMessage();
			sysEventMessage.parseValue(version, value);
			return sysEventMessage;
		}
		case Sub:{
			SubMessage subMessage = new SubMessage();
			subMessage.parseValue(version, value);
			return subMessage;
		}
		case SubResult:{
			SubResultMessage subResultMessage = new SubResultMessage();
			subResultMessage.parseValue(version, value);
			return subResultMessage;
		}
		case SubVerify:{
			SubVerifyMessage subVerifyMessage = new SubVerifyMessage();
			subVerifyMessage.parseValue(version, value);
			return subVerifyMessage;
		}
		case SubVerifyResult:{
			SubVerifyResultMessage subVerifyResultMessage = new SubVerifyResultMessage();
			subVerifyResultMessage.parseValue(version, value);
			return subVerifyResultMessage;
		}
		case DeviceLogSwitch:{
			DeviceLogSwitchMessage logSwitchMessage = new DeviceLogSwitchMessage();
			logSwitchMessage.parseValue(version, value);
			return logSwitchMessage;
		}
		case DeviceLog:{
			DeviceLogMessage logMessage = new DeviceLogMessage();
			logMessage.parseValue(version, value);
			return logMessage;
		}
		case DeviceTicketGet:{
			DeviceTicketGetMessage deviceTicketGetMessage = new DeviceTicketGetMessage();
			deviceTicketGetMessage.parseValue(version, value);
			return deviceTicketGetMessage;
		}
		case DeviceTicketGetResult:{
			DeviceTicketGetResultMessage deviceTicketGetResultMessage = new DeviceTicketGetResultMessage();
			deviceTicketGetResultMessage.parseValue(version, value);
			return deviceTicketGetResultMessage;
		}
		case DeviceLocation:{
			DeviceLocationMessage deviceLoactionMessage = new DeviceLocationMessage();
			deviceLoactionMessage.parseValue(version, value);
			return deviceLoactionMessage;
		}
		case ProductionTestStart:{
			ProductionTestStartMessage productionTestStartMessage = new ProductionTestStartMessage();
			productionTestStartMessage.parseValue(version, byteBuf);
			return productionTestStartMessage;
		}
		case ProductionTestEnd:{
			ProductionTestEndMessage productionTestEndMessage = new ProductionTestEndMessage();
			productionTestEndMessage.parseValue(version, byteBuf);
			return productionTestEndMessage;
		}
		case GatewaySubDevice:{
			GatewaySubDeviceMessage gatewaySubDeviceMessage = new GatewaySubDeviceMessage();
			gatewaySubDeviceMessage.parseValue(version, byteBuf);
			return gatewaySubDeviceMessage;
		}
		case GatewayModule:{
			GatewayModuleMessage gatewayModuleMessage = new GatewayModuleMessage();
			gatewayModuleMessage.parseValue(version, byteBuf);
			return gatewayModuleMessage;
		}
		case GatewaySession:{
			GatewaySessionMessage gatewaySessionMessage = new GatewaySessionMessage();
			gatewaySessionMessage.parseValue(version, byteBuf);
			return gatewaySessionMessage;
		}
      case DeviceState:
        return DeviceStateMessage.parserValueTemplate(version, value);
      case AppState:
        return AppStateMessage.parseValueTemplate(version, value);
		default:
			return null;
		}
	}
}

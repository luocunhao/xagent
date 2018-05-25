package xlink.cm.message;

import com.alibaba.fastjson.JSONObject;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import xlink.cm.message.type.CMMessageType;
import xlink.cm.message.type.DeviceLogLevelType;
import xlink.cm.message.type.DeviceLogType;

public class DeviceLogMessage extends CMMessage{
	
	private static final String SEPARATOR = "#.#";

	private DeviceLogLevelType level;
	private int timestamp;
	private DeviceLogType type;
	private Object[] args;
	
	@Override
	public CMMessageType getMessageType() {
		return CMMessageType.DeviceLog;
	}

	@Override
	public void parseValue(int version, ByteBuf byteBuf) throws Exception {
		//level(4)+ type(12)  
		short sh = byteBuf.readShort();
		int lv = sh;
		lv = lv >> 12;
		int tp = sh & 0x0FFF;
		
		
		level = DeviceLogLevelType.fromType(lv);
		type = DeviceLogType.fromType(tp);
		timestamp = byteBuf.readInt();
		short argsLength = byteBuf.readShort();
		byte[] argsBytes = new byte[argsLength];
		byteBuf.readBytes(argsBytes);
		String argsString = new String(argsBytes, "UTF-8");
		args = argsString.split(SEPARATOR);
	}

	@Override
	public byte[] toPayload(int version) throws Exception {
		StringBuffer argsString = new StringBuffer();
		for (Object object : args) {
			argsString.append(object.toString());
			argsString.append(SEPARATOR);
		}
		byte[] argsBytes = argsString.toString().getBytes("UTF-8");
		int length = 4 + 2 + 4 + 2 + argsBytes.length; 
		ByteBuf buf = Unpooled.buffer(length);
		buf.writeShort(getMessageType().type());
		buf.writeShort(length - 4);
		buf.writeByte(getLevel().type());
		buf.writeByte(getType().type());
		buf.writeInt(getTimestamp());
		buf.writeShort(argsBytes.length);
		buf.writeBytes(argsBytes);
		return buf.copy().array();
	}
	
	public int getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public DeviceLogLevelType getLevel() {
		return level;
	}

	public void setLevel(DeviceLogLevelType level) {
		this.level = level;
	}

	public DeviceLogType getType() {
		return type;
	}

	public void setType(DeviceLogType type) {
		this.type = type;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object... args) {
		this.args = args;
	}
	
	
	public static DeviceLogMessage newOnlineMessage(DeviceLogLevelType levelType, int timestamp, int deviceId, String mac, String ip){
		DeviceLogMessage logMessage = new DeviceLogMessage();
		logMessage.setType(DeviceLogType.Online);
		logMessage.setLevel(levelType);
		logMessage.setTimestamp(timestamp);
		logMessage.setArgs(deviceId, mac, ip);
		return logMessage;
	}
	
	public static DeviceLogMessage newOfflineMessage(DeviceLogLevelType levelType, int timestamp, int deviceId, String mac){
		DeviceLogMessage logMessage = new DeviceLogMessage();
		logMessage.setType(DeviceLogType.Offline);
		logMessage.setLevel(levelType);
		logMessage.setTimestamp(timestamp);
		logMessage.setArgs(deviceId, mac);
		return logMessage;
	}
	
	public static DeviceLogMessage newPingMessage(DeviceLogLevelType levelType, int timestamp, int deviceId, String mac){
		DeviceLogMessage logMessage = new DeviceLogMessage();
		logMessage.setType(DeviceLogType.Ping);
		logMessage.setLevel(levelType);
		logMessage.setTimestamp(timestamp);
		logMessage.setArgs(deviceId, mac);
		return logMessage;
	}
	
	public static DeviceLogMessage newRecvSetMessage(DeviceLogLevelType levelType, int timestamp, int deviceId, String mac){
		DeviceLogMessage logMessage = new DeviceLogMessage();
		logMessage.setType(DeviceLogType.RecvSet);
		logMessage.setLevel(levelType);
		logMessage.setTimestamp(timestamp);
		logMessage.setArgs(deviceId, mac);
		return logMessage;
	}
	
	public static DeviceLogMessage newRespSetMessage(DeviceLogLevelType levelType, int timestamp, int deviceId, String mac, int retCode){
		DeviceLogMessage logMessage = new DeviceLogMessage();
		logMessage.setType(DeviceLogType.RespSet);
		logMessage.setLevel(levelType);
		logMessage.setTimestamp(timestamp);
		logMessage.setArgs(deviceId, mac, retCode);
		return logMessage;
	}
	
	public static DeviceLogMessage newRecvSubMessage(DeviceLogLevelType levelType, int timestamp, int deviceId, String mac, int appId){
		DeviceLogMessage logMessage = new DeviceLogMessage();
		logMessage.setType(DeviceLogType.RecvSub);
		logMessage.setLevel(levelType);
		logMessage.setTimestamp(timestamp);
		logMessage.setArgs(deviceId, mac, appId);
		return logMessage;
	}
	
	public static DeviceLogMessage newRespSubMessage(DeviceLogLevelType levelType, int timestamp, int deviceId, String mac, int appId, int retCode){
		DeviceLogMessage logMessage = new DeviceLogMessage();
		logMessage.setType(DeviceLogType.RespSub);
		logMessage.setLevel(levelType);
		logMessage.setTimestamp(timestamp);
		logMessage.setArgs(deviceId, mac, appId, retCode);
		return logMessage;
	}
	
	public static DeviceLogMessage newSyncMessage(DeviceLogLevelType levelType, int timestamp, int deviceId, String mac, JSONObject datapoint){
		DeviceLogMessage logMessage = new DeviceLogMessage();
		logMessage.setType(DeviceLogType.Sync);
		logMessage.setLevel(levelType);
		logMessage.setTimestamp(timestamp);
		logMessage.setArgs(deviceId, mac, datapoint.toJSONString());
		return logMessage;
	}
	
	public static DeviceLogMessage newRecvNotifyMessage(DeviceLogLevelType levelType, int timestamp, int deviceId, String mac){
		DeviceLogMessage logMessage = new DeviceLogMessage();
		logMessage.setType(DeviceLogType.RecvNotify);
		logMessage.setLevel(levelType);
		logMessage.setTimestamp(timestamp);
		logMessage.setArgs(deviceId, mac);
		return logMessage;
	}
	
	public static DeviceLogMessage newCompleteUpgradeMessage(DeviceLogLevelType levelType, int timestamp, int deviceId, String mac, int beforeVersion, int afterVersion){
		DeviceLogMessage logMessage = new DeviceLogMessage();
		logMessage.setType(DeviceLogType.CompleteUpgrade);
		logMessage.setLevel(levelType);
		logMessage.setTimestamp(timestamp);
		logMessage.setArgs(deviceId, mac, beforeVersion, afterVersion);
		return logMessage;
	}
	
	/**
	 * 自定义
	 * @param timestamp
	 * @param levelType
	 * @param customLog
	 * @return
	 */
	public static DeviceLogMessage newCustomTraceMessage(int timestamp, DeviceLogLevelType levelType, String customLog){
		DeviceLogMessage logMessage = new DeviceLogMessage();
		logMessage.setType(DeviceLogType.CusteomTrace);
		logMessage.setLevel(levelType);
		logMessage.setTimestamp(timestamp);
		logMessage.setArgs(customLog);
		return logMessage;
	}
	
}

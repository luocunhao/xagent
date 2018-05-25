package xlink.cm.message.sysevent;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;

import xlink.cm.exception.CMMessageEncodeException;
import xlink.cm.exception.CMMessageParseException;
import xlink.cm.message.CMMessage;
import xlink.cm.message.type.CMMessageType;
import xlink.cm.message.type.SysEventMessageType;

public class UpgradeEventMessage extends CMMessage{
	
	private int deviceId;
	private boolean isFirmwareUpgrade;
	private boolean isMCUUpgrade;
	private int firmwareMod;
	private int firmwareVersion;
	private int mcuMod;
	private int mcuVersion;
	/**
	 * 是否强制升级
	 */
	private boolean must;
	private int fileSize;
	private String downloadUrl;
	private String fileMd5;
	
	
	@Override
	public CMMessageType getMessageType() {
		return CMMessageType.SysEvent;
	}

	@Override
	public void parseValue(int version, ByteBuf buf) throws Exception {
//		ByteBuf buf = Unpooled.wrappedBuffer(value);
		deviceId = buf.readInt();
		byte flag = buf.readByte();
		//两个标志位是互斥的
		if (((flag & 0x80) > 0 && (flag & 0x40) > 0)  || (flag & 0x80) == 0 && (flag & 0x40) == 0 ) {
			throw new CMMessageParseException("Unkown Upgrade Flags : " + flag);
		}
		if ((flag & 0x80) > 0 ) {
			isFirmwareUpgrade = true;
			firmwareMod = buf.readByte();
			firmwareVersion = buf.readShort();
		}
		
		if ((flag & 0x40) > 0 ) {
			isMCUUpgrade = true;
			mcuMod = buf.readByte();
			mcuVersion = buf.readShort();
		}
		
		fileSize = buf.readInt();
		
		must = (flag & 0x20 ) > 0 ? true: false;
		
		short downloadUrlLength = buf.readShort();
		downloadUrl = buf.readBytes(downloadUrlLength).toString(Charset.forName("UTF-8"));
		short fileMd5Length = buf.readShort();
		fileMd5 = buf.readBytes(fileMd5Length).toString(Charset.forName("UTF-8"));
	}

	@Override
	public byte[] toPayload(int version) throws Exception {
		ByteBuf valueBuf = Unpooled.buffer();
		valueBuf.writeInt(deviceId);
		byte flag = 0;
		if ((isFirmwareUpgrade() && isMCUUpgrade()) || (!isFirmwareUpgrade() && !isMCUUpgrade())) {
			throw new CMMessageEncodeException("can not write upgrade flag.");
		}
		if (isFirmwareUpgrade()) {
			flag |= 0x80;
		}
		if (isMCUUpgrade()) {
			flag |= 0x40;
		}
		if (isMust()) {
			flag |= 0x20;
		}
		
		valueBuf.writeByte(flag);
		
		if (isFirmwareUpgrade) {
			valueBuf.writeByte(firmwareMod);
			valueBuf.writeShort(firmwareVersion);
		}
		if (isMCUUpgrade) {
			valueBuf.writeByte(mcuMod);
			valueBuf.writeShort(mcuVersion);
		}
		
		valueBuf.writeInt(fileSize);
		
		byte[] downloadUrlBytes = downloadUrl.getBytes();
		valueBuf.writeShort(downloadUrlBytes.length);
		valueBuf.writeBytes(downloadUrlBytes);
		
		byte[] fileMd5Bytes = fileMd5.getBytes();
		valueBuf.writeShort(fileMd5Bytes.length);
		valueBuf.writeBytes(fileMd5Bytes);
		
		byte[] value = valueBuf.array();
		
		int length = 4 + 4 + value.length;
		ByteBuf buf = Unpooled.buffer(length);
		buf.writeShort(getMessageType().type());
		buf.writeShort(length - 4);
		buf.writeShort(SysEventMessageType.Upgrade.type());
		buf.writeShort(value.length);
		buf.writeBytes(value);
		return buf.copy().array();
	}
	
	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public void setMust(boolean must) {
		this.must = must;
	}

	public boolean isMust() {
		return must;
	}
	public int getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}
	public boolean isFirmwareUpgrade() {
		return isFirmwareUpgrade;
	}
	public void setFirmwareUpgrade(boolean isFirmwareUpgrade) {
		this.isFirmwareUpgrade = isFirmwareUpgrade;
	}
	public boolean isMCUUpgrade() {
		return isMCUUpgrade;
	}
	public void setMCUUpgrade(boolean isMCUUpgrade) {
		this.isMCUUpgrade = isMCUUpgrade;
	}
	public int getFirmwareMod() {
		return firmwareMod;
	}
	public void setFirmwareMod(int firmwareMod) {
		this.firmwareMod = firmwareMod;
	}
	public int getFirmwareVersion() {
		return firmwareVersion;
	}
	public void setFirmwareVersion(int firmwareVersion) {
		this.firmwareVersion = firmwareVersion;
	}
	public int getMcuMod() {
		return mcuMod;
	}
	public void setMcuMod(int mcuMod) {
		this.mcuMod = mcuMod;
	}
	public int getMcuVersion() {
		return mcuVersion;
	}
	public void setMcuVersion(int mcuVersion) {
		this.mcuVersion = mcuVersion;
	}
	public String getDownloadUrl() {
		return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	public String getFileMd5() {
		return fileMd5;
	}
	public void setFileMd5(String fileMd5) {
		this.fileMd5 = fileMd5;
	}

}

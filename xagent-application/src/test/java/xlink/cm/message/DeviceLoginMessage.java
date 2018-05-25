package xlink.cm.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class DeviceLoginMessage {
  private int msgId = 123;
  private String productId = "1607d2aed9bfc6001607d2aed9bfc601";
  private String mac = "200000000002";
  private boolean hasWifiFirmware = false;
  private boolean hasWifiVersion = true;
  private boolean hasMCUFirmware = false;
  private boolean hasMCUVersion = true;
  private byte wifiFirmware = 9;
  private int wifiVersion = 3;
  private byte mcuFirmware = 2;
  private int mcuVersion = 4;


	public byte[] toPayload(int version) throws Exception {
		byte[] pidBytes = productId.getBytes();
		byte[] macBytes = mac.getBytes();
		

    ByteBuf extraBuf = Unpooled.buffer();
		byte loginFlag = 0;
		if (hasWifiFirmware) {
			loginFlag |= 0x80;
		}
		if (hasWifiVersion) {
			loginFlag |= 0x40;
		}
		if (hasMCUFirmware) {
			loginFlag |= 0x20;
		}
		if (hasMCUVersion) {
			loginFlag |= 0x10;
		}
    extraBuf.writeByte(loginFlag);
		if (hasWifiFirmware) {
      extraBuf.writeByte(wifiFirmware);
		}
		if (hasWifiVersion) {
      extraBuf.writeShort(wifiVersion);
		}
		if (hasMCUFirmware) {
      extraBuf.writeByte(mcuFirmware);
		}
		if (hasMCUVersion) {
      extraBuf.writeShort(mcuVersion);
		}

    int length = 2 + 2 + pidBytes.length + 2 + macBytes.length + extraBuf.readableBytes();
    ByteBuf buf = Unpooled.buffer(length + 4);
    buf.writeShort(36);
    buf.writeShort(length);
    buf.writeShort(msgId);
    buf.writeShort(pidBytes.length);
    buf.writeBytes(pidBytes);
    buf.writeShort(macBytes.length);
    buf.writeBytes(macBytes);
    buf.writeBytes(extraBuf);
		return buf.array();
	}

	public int getMsgId() {
		return msgId;
	}

	public String getProductId() {
		return productId;
	}

	public String getMac() {
		return mac;
	}

	public boolean isHasWifiFirmware() {
		return hasWifiFirmware;
	}

	public boolean isHasWifiVersion() {
		return hasWifiVersion;
	}

	public boolean isHasMCUFirmware() {
		return hasMCUFirmware;
	}

	public boolean isHasMCUVersion() {
		return hasMCUVersion;
	}

	public byte getWifiFirmware() {
		return wifiFirmware;
	}

	public int getWifiVersion() {
		return wifiVersion;
	}

	public byte getMcuFirmware() {
		return mcuFirmware;
	}

	public int getMcuVersion() {
		return mcuVersion;
	}

}

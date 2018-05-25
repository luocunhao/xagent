package xlink.cm.message;

import java.nio.charset.Charset;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import xlink.cm.message.struct.ActivationStruct;
import xlink.cm.message.type.CMMessageType;
import xlink.core.utils.ContainerGetter;
import xlink.core.utils.Utils;


public class ActivationMessage extends CMMessage {

	private List<ActivationStruct> activationMessageList = ContainerGetter.arrayList();

	@Override
	public CMMessageType getMessageType() {
		return CMMessageType.Activation;
	}

	@Override
	public byte[] toPayload(int version) {
		ByteBuf buf = Unpooled.buffer();
		for (ActivationStruct activationStruct : activationMessageList) {
			byte[] pidBytes = activationStruct.getPid().getBytes();
			buf.writeShort(pidBytes.length);
			buf.writeBytes(pidBytes);
			byte[] macBytes = Utils.hexStringToByteArray(activationStruct.getMac());
			buf.writeShort(macBytes.length);
			buf.writeBytes(macBytes);
			byte activeFlag = 0;
			if (activationStruct.isHasWifiFirmware()) {
				activeFlag |= 0x80;
			}
			if (activationStruct.isHasWifiVersion()) {
				activeFlag |= 0x40;
			}
			if (activationStruct.isHasMCUFirmware()) {
				activeFlag |= 0x20;
			}
			if (activationStruct.isHasMCUVersion()) {
				activeFlag |= 0x10;
			}
			if (activationStruct.isHasSn()) {
				activeFlag |= 0x8;
			}
			if (activationStruct.isHasGatewayId()) {
				activeFlag |= 0x4;
			}
      if (activationStruct.isHasActiveIp()) {
        activeFlag |= 0x02;
      }
      if (activationStruct.isHasMoreFlag()) {
        activeFlag |= 0x01;
      }
			buf.writeByte(activeFlag);
			if (activationStruct.isHasWifiFirmware()) {
				buf.writeByte(activationStruct.getWifiFirmware());
			}
			if (activationStruct.isHasWifiVersion()) {
				buf.writeShort(activationStruct.getWifiVersion());
			}
			if (activationStruct.isHasMCUFirmware()) {
				buf.writeByte(activationStruct.getMcuFirmware());
			}
			if (activationStruct.isHasMCUVersion()) {
				buf.writeShort(activationStruct.getMcuVersion());
			}
			if (activationStruct.isHasSn()) {
				byte[] snBytes = activationStruct.getSn().getBytes(Charset.forName("UTF-8"));
				buf.writeShort(snBytes.length);
				buf.writeBytes(snBytes);
			}
			if (activationStruct.isHasGatewayId()) {
				buf.writeInt(activationStruct.getGatewayId());
			}

      if (activationStruct.isHasMoreFlag()) {
        buf.writeByte(activationStruct.getOtherFlag());
      }
      if (activationStruct.isHasActiveIp()) {
        buf.writeBytes(activationStruct.getActiveIp());
      }
		}
		byte[] value = buf.copy().array();
    System.out.println("lengh " + value.length);
		ByteBuf finalBuf = Unpooled.buffer(value.length + 4);
		finalBuf.writeShort(getMessageType().type());
		finalBuf.writeShort(value.length);
		finalBuf.writeBytes(value);
		return finalBuf.array();
	}
	

	@Override
	public void parseValue(int version,ByteBuf byteBuf) {
//		ByteBuf byteBuf = Unpooled.wrappedBuffer(value);
		while (byteBuf.isReadable()) {
			int pidLength = byteBuf.readUnsignedShort();
			String pid = byteBuf.readBytes(pidLength).toString(Charset.forName("UTF-8"));
			short macLength = byteBuf.readShort();
			StringBuffer mac = new StringBuffer();
			for (int i = 0; i < macLength; i++) {
				int b = (int) ( 0xFF & byteBuf.readByte() );
				mac.append(String.format("%02X", b));// 统一大写，没有冒号
			}
			byte activeFlag = byteBuf.readByte();
			boolean hasWifiFirmware = ( activeFlag & 0x80 ) > 0;
			boolean hasWifiVersion = ( activeFlag & 0x40 ) > 0;
			boolean hasMCUFirmware = ( activeFlag & 0x20 ) > 0;
			boolean hasMCUVersion = ( activeFlag & 0x10 ) > 0;
			boolean hasSn = ( activeFlag & 0x8 ) > 0;
			boolean hasGatewayDeviceId = ( activeFlag & 0x4 ) > 0;
      boolean hasActiveIp = (activeFlag & 0x02) > 0;
      boolean hasMoreFlag = (activeFlag & 0x01) > 0;
			byte wifiFirmware = 0;
			int wifiVersion = 0;
			byte mcuFirmware = 0;
			int mcuVersion = 0;
			String sn = null;
			int gatewayId = 0;
      byte[] activeIp = null;
      byte otherFlag = 0;
			if (hasWifiFirmware) {
				wifiFirmware = byteBuf.readByte();
			}
			if (hasWifiVersion) {
				wifiVersion = byteBuf.readUnsignedShort();
			}
			if (hasMCUFirmware) {
				mcuFirmware = byteBuf.readByte();
			}
			if (hasMCUVersion) {
				mcuVersion = byteBuf.readUnsignedShort();
			}
			if (hasSn) {
				int snLength = byteBuf.readUnsignedShort();
				sn = byteBuf.readBytes(snLength).toString(Charset.forName("UTF-8"));
			}
			if (hasGatewayDeviceId) {
				gatewayId = byteBuf.readInt();
			}
      if (hasMoreFlag) {
        otherFlag = byteBuf.readByte();
      }
			
      if (hasActiveIp) {
        activeIp = new byte[4];
        byteBuf.readBytes(activeIp);
      }

      ActivationStruct activationStruct = new ActivationStruct(pid, mac.toString(), hasWifiFirmware,
          hasWifiVersion, hasMCUFirmware, hasMCUVersion, hasSn, hasGatewayDeviceId, wifiFirmware,
          wifiVersion, mcuFirmware, mcuVersion, sn, gatewayId, hasMoreFlag, otherFlag, hasActiveIp,
          activeIp);
			activationMessageList.add(activationStruct);
		}
	}
	

	public List<ActivationStruct> getActivationMessageList() {
		return activationMessageList;
	}

	public void setActivationMessageList(List<ActivationStruct> activationMessageList) {
		this.activationMessageList = activationMessageList;
	}

}



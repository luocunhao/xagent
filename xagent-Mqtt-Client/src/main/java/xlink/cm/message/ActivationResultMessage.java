package xlink.cm.message;

import java.nio.charset.Charset;
import java.util.List;

import cn.xlink.cmmqttclient.core.utils.ContainerGetter;
import cn.xlink.cmmqttclient.core.utils.Utils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import xlink.cm.message.struct.ActivationResultStruct;
import xlink.cm.message.type.CMMessageType;

public class ActivationResultMessage extends CMMessage {


	private List<ActivationResultStruct> activationResultList = ContainerGetter.arrayList();

	@Override
	public CMMessageType getMessageType() {
		return CMMessageType.ActivetionResult;
	}

	@Override
	public void parseValue(int version, ByteBuf byteBuf) {
		//		ByteBuf byteBuf = Unpooled.wrappedBuffer(value);
		while (byteBuf.isReadable(2)) {
			short        macLength = byteBuf.readShort();
			StringBuffer mac       = new StringBuffer();
			for (int i = 0; i < macLength; i++) {
				int b = (int) (0xFF & byteBuf.readByte());
				mac.append(String.format("%02X", b));// 统一大写，没有冒号
			}
			int    code     = byteBuf.readByte();
			int    deviceId = 0;
			String key      = null;
			if (code == 0) {
				deviceId = byteBuf.readInt();
				int keyLength = byteBuf.readShort();
				key = byteBuf.readBytes(keyLength).toString(Charset.forName("UTF-8"));
			}
			ActivationResultStruct activationResultStruct = new ActivationResultStruct(mac.toString(), code, deviceId, key);
			activationResultList.add(activationResultStruct);
		}
	}

	@Override
	public byte[] toPayload(int version) {
		ByteBuf buf = Unpooled.buffer();
		for (ActivationResultStruct activationResult : activationResultList) {
			byte[] macBytes = Utils.hexStringToByteArray(activationResult.getMac());
			buf.writeShort(macBytes.length);
			buf.writeBytes(macBytes);
			buf.writeByte(activationResult.getCode());
			if (activationResult.getCode() == 0) {
				buf.writeInt(activationResult.getDeviceId());
				byte[] keyBytes = activationResult.getKey().getBytes();
				buf.writeShort(keyBytes.length);
				buf.writeBytes(keyBytes);
			}
		}

		byte[]  value    = buf.copy().array();
		ByteBuf finalBuf = Unpooled.buffer(4 + value.length);
		finalBuf.writeShort(getMessageType().type());
		finalBuf.writeShort(value.length);
		finalBuf.writeBytes(value);
		return finalBuf.array();
	}

	public List<ActivationResultStruct> getActivationResultList() {
		return activationResultList;
	}


	public void setActivationResultList(List<ActivationResultStruct> activationResultStructs) {
		this.activationResultList = activationResultStructs;
	}


}

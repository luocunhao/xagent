package xlink.cm.message;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import xlink.cm.message.struct.Datapoint;
import xlink.cm.message.struct.DatapointDefine;
import xlink.cm.message.type.CMMessageType;

public class DatapointSetMessage extends DatapointMessage{
	
//	private boolean hasName;
//	private String name;
//	private boolean hasDatapoint;
//	private List<Datapoint> datapoint = ContainerGetter.arrayList();
//	private int version;
	private byte[] value;

	@Override
	public CMMessageType getMessageType() {
		return CMMessageType.DatapointSet;
	}

	
  public byte[] toPayload(int version, int dpVersion, int messageId, String name,
      List<Datapoint> datapointList, List<DatapointDefine> datapointTemplate) throws IOException {
		byte flag = 0;
		if (name != null) {
			flag |= 0x80;
		}
		if (datapointList != null && datapointList.isEmpty() == false) {
			flag |= 0x40;
		}
		if (dpVersion == 2) {
			flag |= 0x20;
		}
		
		ByteBuf datapointByteBuf = Unpooled.buffer();
		if (name != null) {
			byte[] nameBytes = name.getBytes(Charset.forName("UTF-8"));
			datapointByteBuf.writeShort(nameBytes.length);
			datapointByteBuf.writeBytes(nameBytes);
		}
		
		if (dpVersion == 1) {
			datapointByteBuf.writeBytes(encodeV1(datapointList, datapointTemplate));
		}else{
      datapointByteBuf.writeBytes(encodeV2(datapointList));
		}
		int length = 4 + 2 + 1 + datapointByteBuf.readableBytes();
		ByteBuf buf = Unpooled.buffer(length);
		buf.writeShort(getMessageType().type());
		buf.writeShort(length -4);
		buf.writeShort(messageId);
		buf.writeByte(flag);
		buf.writeBytes(datapointByteBuf);
		return buf.copy().array();
	}
	

	@Override
	public void parseValue(int version, ByteBuf buf) throws Exception {
		
		this.value = new byte[buf.readableBytes()];
		buf.readBytes(value);
//		ByteBuf buf = Unpooled.wrappedBuffer(value);
//		byte flag = buf.readByte();
		
//		hasName = (flag & 0x80) > 0;
//		hasDatapoint = (flag & 0x40) > 0;
//		version = (flag & 0x20) > 0 ? 2 : 1;
//		if (hasName) {
//			int nameLength = buf.readUnsignedShort();
//			name = buf.readBytes(nameLength).toString(Charset.forName("UTF-8"));
//		}
//		if (hasDatapoint) {
//			CMDeviceChannelInfo deviceChannelInfo = (CMDeviceChannelInfo) CMChannelInfoManager.instance().get(deviceId);
//			switch (version) {
//			case 1:
//				datapoint = parseV1(deviceChannelInfo.getProductId(), buf);
//				break;
//			case 2:
//				datapoint = parseV2(deviceChannelInfo.getProductId(), buf);
//				break;
//			default:
//				throw new CMMessageParseException("unkown datapoint version");
//			}
//		}
	}
	

	@Override
	public byte[] toPayload(int version) {
		ByteBuf buf = Unpooled.buffer(4 + value.length);
		buf.writeShort(getMessageType().type());
		buf.writeShort(value.length);
		buf.writeBytes(value);
		return buf.copy().array();
	}

	public byte[] getValue() {
		return value;
	}

//	public boolean isHasName() {
//		return hasName;
//	}
//
//	public void setHasName(boolean hasName) {
//		this.hasName = hasName;
//	}
//
//	public String getName() {
//		return name;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}
//
//	public boolean isHasDatapoint() {
//		return hasDatapoint;
//	}
//
//	public void setHasDatapoint(boolean hasDatapoint) {
//		this.hasDatapoint = hasDatapoint;
//	}
//
//	public List<Datapoint> getDatapoint() {
//		return datapoint;
//	}
//
//	public void setDatapoint(List<Datapoint> datapoint) {
//		this.datapoint = datapoint;
//	}
//
//	public int getVersion() {
//		return version;
//	}
//
//	public void setVersion(int version) {
//		this.version = version;
//	}

}

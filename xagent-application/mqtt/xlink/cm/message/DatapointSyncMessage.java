package xlink.cm.message;

import java.io.IOException;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import xlink.cm.exception.DatapointException;
import xlink.cm.message.struct.Datapoint;
import xlink.cm.message.struct.DatapointDefine;
import xlink.cm.message.struct.DatapointSyncStruct;
import xlink.cm.message.type.CMMessageType;
import xlink.core.utils.ContainerGetter;

public class DatapointSyncMessage extends DatapointMessage {

	private int version;
	private ByteBuf datapoint;

	@Override
	public CMMessageType getMessageType() {
		return CMMessageType.DatapointSync;
	}
	
	
  public byte[] encode(List<Datapoint> datapoints) {
    return encodeV2(datapoints);
	}
	
	
  public DatapointSyncStruct parse(String productId) throws IOException, DatapointException {
//		ByteBuf buf = Unpooled.wrappedBuffer(value);
		byte flag = datapoint.readByte();
		boolean hasName = ( flag & 0x80 ) > 0;
		boolean hasDatapoint = ( flag & 0x40 ) > 0;
		int version = ( flag & 0x20 ) > 0 ? 2 : 1;
		boolean unPush = ( flag & 0x10 ) > 0;
		boolean hasTimestamp = ( flag & 0x8 ) > 0;
		long timestamp = System.currentTimeMillis();
		String name = null;
		List<Datapoint> datapointList = ContainerGetter.arrayList();
		if (hasName) {
			short nameLength = datapoint.readShort();
			byte[] nameBytes = new byte[nameLength];
			datapoint.readBytes(nameBytes);
			name = new String(nameBytes);
		}
		if (hasTimestamp) {
			timestamp =	datapoint.readLong();
		}

		if (hasDatapoint) {
			switch (version) {
			case 1:
          datapointList = parseV1(productId, datapoint, ContainerGetter. <DatapointDefine> arrayList());
				break;
			case 2:
          datapointList = parseV2(productId, datapoint);
				break;
			default:
				break;
			}
		}
		return new DatapointSyncStruct(version, flag, hasName, name, hasDatapoint, hasTimestamp, timestamp, datapointList, version, unPush);
	}

	@Override
	public void parseValue(int version, ByteBuf buf) throws Exception {
		
		this.datapoint = buf;
		this.version = version;
	}

	@Override
	public byte[] toPayload(int version) {
		ByteBuf buf = Unpooled.buffer(4 + datapoint.readableBytes());
		buf.writeShort(getMessageType().type());
		buf.writeShort( datapoint.readableBytes());
		buf.writeBytes(datapoint);
		return buf.array();
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}



	public ByteBuf getDatapoint() {
		return datapoint;
	}



	public void setDatapoint(ByteBuf datapoint) {
		this.datapoint = datapoint;
	}

	

}

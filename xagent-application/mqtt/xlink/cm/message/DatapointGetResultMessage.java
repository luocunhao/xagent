package xlink.cm.message;

import java.io.IOException;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import xlink.cm.message.struct.Datapoint;
import xlink.cm.message.struct.DatapointDefine;
import xlink.cm.message.type.CMMessageType;
import xlink.cm.message.type.GetDeviceDatapointResultType;
import xlink.core.utils.ContainerGetter;

public class DatapointGetResultMessage extends DatapointMessage {
	private int messageId;
	private GetDeviceDatapointResultType resultType;
	private byte flag;
	private boolean hasName;
	private String name;
	private boolean hasDatapoint;
	private List<Datapoint> datapoint = ContainerGetter.arrayList();
	
	private String productId;

	
	@Override
	public CMMessageType getMessageType() {
		return CMMessageType.DatapointGetResult;
	}

	@Override
	public void parseValue(int version, ByteBuf buf) throws Exception {
		throw new UnsupportedOperationException();
	}
	
	
  public byte[] toPayload(int version, int dpVersion, List<DatapointDefine> datapointTemplate)
      throws IOException {
		ByteBuf dpBuf = Unpooled.buffer();
		dpBuf.writeShort(messageId);
		dpBuf.writeByte(resultType.retCode());
		if (resultType == GetDeviceDatapointResultType.Success) {
			flag = 0;
			if (hasName) {
				flag |= 0x80;
			}
			if (hasDatapoint) {
				flag |= 0x40;
			}
			if (version == 2) {
				flag |= 0x20;
			}
			dpBuf.writeByte(flag);
			if (hasName) {
				byte[] nameBytes = name.getBytes();
				dpBuf.writeShort(nameBytes.length);
				dpBuf.writeBytes(nameBytes);
			}
			if (hasDatapoint) {
				byte[] datapointBytes = null;
				switch (dpVersion) {
				case 1:
					datapointBytes = encodeV1(datapoint, datapointTemplate);
					break;
				case 2:
            datapointBytes = encodeV2(datapoint);
					break;
				default:
					throw new UnsupportedOperationException();
				}
				dpBuf.writeBytes(datapointBytes);
			}
		}
		byte[] dpBytes = dpBuf.copy().array();
		ByteBuf finalBuf = Unpooled.buffer(4 + dpBytes.length);
		finalBuf.writeShort(getMessageType().type());
		finalBuf.writeShort(dpBytes.length);
		finalBuf.writeBytes(dpBytes);
		return finalBuf.array();
	}

	@Override
  public byte[] toPayload(int version) throws IOException {
		throw new UnsupportedOperationException();
	}
	


	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public void setResultType(GetDeviceDatapointResultType resultType) {
		this.resultType = resultType;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public byte getFlag() {
		return flag;
	}

	public void setFlag(byte flag) {
		this.flag = flag;
	}

	public boolean isHasName() {
		return hasName;
	}

	public void setHasName(boolean hasName) {
		this.hasName = hasName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isHasDatapoint() {
		return hasDatapoint;
	}

	public void setHasDatapoint(boolean hasDatapoint) {
		this.hasDatapoint = hasDatapoint;
	}

	public List<Datapoint> getDatapoint() {
		return datapoint;
	}

	public void setDatapoint(List<Datapoint> datapoint) {
		this.datapoint = datapoint;
	}

	public String getProductId() {
		return productId;
	}


}

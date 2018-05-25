package xlink.cm.message;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import xlink.cm.exception.DatapointException;
import xlink.cm.message.struct.Datapoint;
import xlink.cm.message.struct.DatapointDefine;
import xlink.cm.message.type.DatapointType;
import xlink.core.utils.ContainerGetter;
import xlink.core.utils.StringTools;
import xlink.core.utils.Utils;
import xlink.mqtt.client.utils.LogHelper;
import xlink.utils.tuple.TowTuple;

public abstract class DatapointMessage extends CMMessage{
	
	private static final Logger logger = LoggerFactory.getLogger(DatapointMessage.class);
	
	
  protected byte[] encodeV1(List<Datapoint> datapoints, List<DatapointDefine> datapointTemplate)
      throws IOException {
		int markBytes = datapointTemplate.size() / 8;
		if (( datapointTemplate.size() % 8 ) != 0) {
			markBytes++;
		}
		byte[] marks = new byte[markBytes];
		ByteBuf dataBuf = Unpooled.buffer();
		for (Datapoint datapoint : datapoints) {
			int index = datapoint.getIndex();
			int flagIndex = index % 8;
			Utils.setBitValid(marks[index / 8], flagIndex);
		}
		dataBuf.writeBytes(marks);
		for (Datapoint datapoint : datapoints) {
			writeValue(datapoint.getType(), datapoint.getValue(), dataBuf);
		}
		return dataBuf.copy().array();
	}
	
  protected List<Datapoint> parseV1(String productId, ByteBuf buf,
      List<DatapointDefine> datapointTemplate) throws IOException, DatapointException {
		List<Datapoint> datapoint = ContainerGetter.arrayList();
		int dataPointCount = datapointTemplate.size();
		int markBytes = datapointTemplate.size() / 8;
		if (( datapointTemplate.size() % 8 ) != 0) {
			markBytes++;
		}
		byte[] masks = new byte[markBytes];
		for (int i = 0; i < markBytes; i++) {
			masks[i] = buf.readByte();
		}

		for (int i = 0; i < dataPointCount; i++) {
			if (isDataPointValid(masks, i)) {
				DatapointDefine dataType = datapointTemplate.get(i);
				Object value = readValue(dataType.getType(), buf);
				if (value == null) {
					throw new DatapointException(DatapointException.Type.parseError);
				}
				Datapoint dataPoint = new Datapoint(i, dataType.getType(), value);
				datapoint.add(dataPoint);
			}
		}
		return datapoint;
		
	}
	
  protected byte[] encodeV2(List<Datapoint> datapoints) {
		ByteBuf dataBuf = Unpooled.buffer();
		for (Datapoint datapoint : datapoints) {
				dataBuf.writeByte(datapoint.getIndex());
				writeValueV2(datapoint.getType(), datapoint.getValue(), dataBuf);
		}
		return dataBuf.copy().array();
	}

  protected List<Datapoint> parseV2(String productId, ByteBuf buf)
      throws DatapointException, IOException {
		List<Datapoint> datapoint = ContainerGetter.arrayList();
		while (buf.readableBytes() >= 3) {
			int index = Utils.getUnsignedByte(buf.readByte());

			// 获取值
      TowTuple<DatapointType, Object> typeAndValue = readValueV2(index, buf);
      if (typeAndValue == null) {
				throw new DatapointException(DatapointException.Type.parseError);
			}
			// 加入列表
      Datapoint dataPoint = new Datapoint(index, typeAndValue.first, typeAndValue.second);
			datapoint.add(dataPoint);
		}
		return datapoint;
	}
	
  /*
   * private DatapointType getDatapointTypeFromTemplateV2(int templateIndex, List<DatapointDefine>
   * datapointTemplate) { for (DatapointDefine dataType : datapointTemplate) { if
   * (dataType.getIndex() == templateIndex) { return dataType.getType(); } } return
   * DatapointType.Unkown; }
   * 
   * private DatapointDefine getDatapointDefineFromTemplateV2(int index, List<DatapointDefine>
   * datapointTemplate){ for (DatapointDefine datapointDefine : datapointTemplate) { if
   * (datapointDefine.getIndex() == index) { return datapointDefine; } } return null; }
   */

	
	private boolean isDataPointValid(byte[] flags, int index) {
		byte flag = flags[index / 8];
		int flagIndex = index % 8;
		if (Utils.getBitValue(flag, flagIndex) == 1) {
			return true;
		}
		return false;
	}
	
	private void writeValue(DatapointType type, Object value, ByteBuf datapoint){
		switch (type) {
		case Boolean:
			datapoint.writeBoolean(Boolean.valueOf(value.toString()));
			break;
		case Byte:
			datapoint.writeByte(Byte.valueOf(value.toString()));
			break;
		case Float:
			datapoint.writeFloat(StringTools.getFloat(value));
			break;
		case Short:
			datapoint.writeShort(StringTools.getShort(value));
			break;
		case Int:
			datapoint.writeInt(StringTools.getInt(value));
			break;
		case String: {
			byte[] valueBytes = value.toString().getBytes();
			datapoint.writeShort(valueBytes.length);
			datapoint.writeBytes(valueBytes);
			break;
		}
		default:
			break;
		}
	}
	
	
	private Object readValue(DatapointType type, ByteBuf dataPoint) throws IndexOutOfBoundsException {
		switch (type) {
		case Boolean:
			byte v = dataPoint.readByte();
			if (v == 1) {
				return true;
			} else {
				return false;
			}
		case Byte:
			return (int) ( 0xFF & dataPoint.readByte() );
		case Float:
			return dataPoint.readFloat();
		case Short:
			return dataPoint.readShort();
		case Int:
			return dataPoint.readInt();
		case String: {
			int len = dataPoint.readShort();
			byte[] bytes = new byte[len];
			dataPoint.readBytes(bytes);
			String ret = new String(bytes, Charset.forName("UTF-8"));
//			String ret = dataPoint.readBytes(len).toString(Charset.forName("UTF-8"));
			return ret;
		}
		default:
			break;
		}

		return null;
	}
	
	
	private void writeValueV2(DatapointType type, Object value, ByteBuf datapoint){
		short length = 0;
		length |=  (byte)type.device_type() << 4;
		length = (short) ( length << 8 );
		
		switch (type) {
      case Boolean:/*
                    * { int len = 1; length |= (short) len & 0x0FFF; datapoint.writeShort(length);
                    * datapoint.writeBoolean(Boolean.valueOf(value.toString())); break; }
                    */
		case Byte:{
			int len =1;
			length |= (short) len & 0x0FFF;
			datapoint.writeShort(length);
			datapoint.writeByte(StringTools.getByte(value));
			break;
		}
		case Float:{
			int len = 4;
			length |= (short)len & 0x0FFF;
			datapoint.writeShort(length);
			datapoint.writeFloat(StringTools.getFloat(value));
			break;
		}
		case Short:{
			int len = 2;
			length |= (short)len & 0x0FFF;
			datapoint.writeShort(length);
			datapoint.writeShort(StringTools.getShort(value));
			break;
		}
		case Int:{
			int len = 4;
			length |= (short)len & 0x0FFF;
			datapoint.writeShort(length);
			datapoint.writeInt(StringTools.getInt(value));
			break;
		}
		case String:{
			byte[] valueBytes = value.toString().getBytes();
			int len = valueBytes.length;
			length |= (short)len & 0x0FFF;
			datapoint.writeShort(length);
			datapoint.writeBytes(valueBytes);
			break;
		}
		case ByteArray:{
        byte[] bytes = null;
        if (value instanceof byte[]) {
          bytes = (byte[]) value;

        } else {
          LogHelper.LOGGER().warn("[datapointMessage] value is not byteArray,type is ",
              value.getClass());
          bytes = new byte[] {(byte) 0};
        }
        // byte[] bytes = Utils.hexStringToBytes(value.toString());
			int len = bytes.length;
			length |= (short) len & 0x0FFF;
			datapoint.writeShort(length);
			datapoint.writeBytes(bytes);
			break;
		}
		case UnsignedShort:{
			int len = 2;
			length |= (short) len& 0x0FFF;
			datapoint.writeShort(length);
			datapoint.writeShort(StringTools.getInt(value));
			break;
		}
		case UnsignedInt:{
			int len = 4;
			length |= (short) len & 0x0FFF;
			datapoint.writeShort(length);
			datapoint.writeInt(StringTools.getInt(value));
			break;
		}
		default:
			break;
		}
		
	}
	
  private TowTuple<DatapointType, Object> readValueV2(int index, ByteBuf byteBuf)
      throws IndexOutOfBoundsException {
		short length = byteBuf.readShort();
		// 去掉前面的类型
		int typeInBuf = length;
		typeInBuf = typeInBuf >> 12;
		typeInBuf &= 0x000F;
		length &= 0x0FFF;// 0b0000111111111111;
		if (length > byteBuf.readableBytes()) {
			logger.error("Buffer length overflow");
		}
    DatapointType type = DatapointType.fromDeviceType(typeInBuf);
		int readindex = byteBuf.readerIndex();
		// 具体处理
		switch (type) {
		case Boolean:
        /*
         * byte v = byteBuf.readByte(); if (v == 1) { return new TowTuple<DatapointType,
         * Object>(type, true); } else { return new TowTuple<DatapointType, Object>(type, false); }
         */
		case Byte:
        return new TowTuple<DatapointType, Object>(type, Utils.getUnsignedByte(byteBuf.readByte()));
		case Float:
        return new TowTuple<DatapointType, Object>(type, byteBuf.readFloat());
		case Short:
        return new TowTuple<DatapointType, Object>(type, byteBuf.readShort());
		case Int:
        return new TowTuple<DatapointType, Object>(type, byteBuf.readInt());
		case String:
			byte[] bytes = new byte[length];
			byteBuf.readBytes(bytes);
//			new String(bytes, "UTF-8");
//			String ret = byteBuf.readBytes(length).toString(Charset.forName("UTF-8"));
			String ret = new String(bytes, Charset.forName("UTF-8"));;
        return new TowTuple<DatapointType, Object>(type, ret);
		case ByteArray:
			byte[] databytes = new byte[length];
			byteBuf.readBytes(databytes);
        return new TowTuple<DatapointType, Object>(type, databytes);
		case UnsignedShort:
        return new TowTuple<DatapointType, Object>(type,
            Utils.getUnsignedShort(byteBuf.readShort()));
		case UnsignedInt :
        return new TowTuple<DatapointType, Object>(type, Utils.getUnsignedInt(byteBuf.readInt()));
		default:
			break;
		}
		byteBuf.readerIndex(readindex + length); 
		return null;
	}

}

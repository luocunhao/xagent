package xlink.cm.message.struct;

import java.nio.charset.Charset;

import com.alibaba.fastjson.JSONObject;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import xlink.cm.message.type.DatapointType;
import xlink.core.utils.StringTools;
import xlink.core.utils.Utils;

public class Datapoint {
	/**
	 * 索引
	 */
	private int index;
	/**
	 * 数据端点类型
	 */
	private DatapointType type;
	/**
	 * 值
	 */
	private Object value;
	

	public Datapoint(int index, DatapointType type, Object value) {
		super();
		this.index = index;
		this.type = type;
		this.value = value;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setType(DatapointType type) {
		this.type = type;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public boolean getBooleanValue() {
		return StringTools.getBoolean(value);
	}

	public byte getByteValue() {
		return (Byte) value;
	}

	public int getIntValue() {
		return StringTools.getInt(value);
	}

	public long getLongValue() {
		return StringTools.getLong(value);
	}

	public String getStringValue() {
		return value.toString();
	}

	public float getFloatValue() {
		return StringTools.getFloat(value);
	}

	public DatapointType getType() {
		return type;
	}

	/**
	 * 获取二进制数据的长度
	 * 
	 * @return
	 */
	public int getBinarySize() {

		switch (this.type) {
		case Boolean:
		case Byte:
			return 1;
		case Short:
			return 2;
		case Int:
			return 4;
		case Float:
			return 4;
		case String:
			String s = getStringValue();
			return s.getBytes(Charset.forName("UTF-8")).length + 2;
		case ByteArray:
			byte[] bytes = Utils.hexStringToByteArray(getStringValue());
			return bytes.length;
		case UnsignedInt:
			return 4;
		case UnsignedShort:
			return 2;
		default:
			return 0;
		}
	}

	public int getBinarySizeV2() {
		switch (this.type) {
		case String:
			String s = getStringValue();
			return s.getBytes(Charset.forName("UTF-8")).length;

		default:
			return getBinarySize();
		}
	}

	/**
	 * 按照数据模板形式返回数据bytes
	 * 
	 * @return
	 */
	public ByteBuf getBytes() {

		ByteBuf bf = null;
		switch (this.type) {
		case Boolean:
			bf = Unpooled.buffer(1);
			bf.writeByte(getBooleanValue() ? (byte) 1 : (byte) 0);
			break;
		case Byte:
			bf = Unpooled.buffer(1);
			bf.writeByte(getIntValue());
			break;
		case Short:
			bf = Unpooled.buffer(2);
			bf.writeShort(getIntValue());
			break;
		case Int:
			bf = Unpooled.buffer(4);
			bf.writeInt(getIntValue());
			break;
		case Float:
			bf = Unpooled.buffer(4);
			bf.writeFloat(getFloatValue());
			break;
		case String:
			String s = getStringValue();
			int len = s.getBytes(Charset.forName("UTF-8")).length;
			bf = Unpooled.buffer(len + 2);
			bf.writeShort(len);
			bf.writeBytes(s.getBytes(Charset.forName("UTF-8")));
			break;
		case UnsignedInt:
			bf = Unpooled.buffer(4);
			bf.writeInt((int) getLongValue());
			break;
		case UnsignedShort:
			bf = Unpooled.buffer(2);
			bf.writeShort((short) getIntValue());
			break;

		case ByteArray:
			byte[] bytes = Utils.hexStringToByteArray(getStringValue());
			bf = Unpooled.buffer(bytes.length);
			bf.writeBytes(bytes);
			break;
		default:
			break;
		}

		return bf;
	}

	/**
	 * V2和V1只有String返回的Bytes不同
	 * 
	 * @return
	 */
	public ByteBuf getBytesV2() {

		ByteBuf bf = null;
		switch (this.type) {
		case String:
			String s = getStringValue();
			int len = s.getBytes(Charset.forName("UTF-8")).length;
			bf = Unpooled.buffer(len);
			bf.writeBytes(s.getBytes(Charset.forName("UTF-8")));
			break;
		case ByteArray:
			byte[] bytes = Utils.hexStringToByteArray(getStringValue());
			bf = Unpooled.buffer(bytes.length);
			bf.writeBytes(bytes);
			break;
		default:
			return getBytes();
		}

		return bf;
	}

	@Override
	public String toString() {
		return "Datapoint [index=" + index + ", type=" + type + ", value=" + value + "]";
	}
	
	
	public JSONObject toJSON(){
		JSONObject item = new JSONObject();
		item.put("i", getIndex());
		item.put("t", getType().type());
		item.put("v", getValue());
		return item;
	}
	
  public DatapointType getDataType() {
    return this.type;
  }

}

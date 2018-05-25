package xlink.cm.message;

import java.util.Date;

import io.netty.buffer.ByteBuf;
import xlink.cm.message.type.CMMessageType;

public class DeviceLocationMessage extends CMMessage{
	
	private double longitude=0;
	private double latitude=0;
	private String street = "";
	private Date createTime=null;

	@Override
	public CMMessageType getMessageType() {
		return CMMessageType.DeviceLocation;
	}

	@Override
	public void parseValue(int version, ByteBuf byteBuf) throws Exception {
		byte flag = byteBuf.readByte();
		//flag的第7位,表示是否有时间戳
		boolean isHasTimestamp = (flag & 0x80) == 0?false:true;
		//flag的第6位。0表示4个字节低精度； 值1表示8个字节高精度
		boolean ishighAccuracy = (flag & 0x40) == 0?false:true;
		//flag的第5位。表示是否附带街道信息
		boolean isHashStreet = (flag & 0x20) == 0?false:true;
		
		if(isHasTimestamp){
			long timestamp = byteBuf.readLong();
			createTime = new Date(timestamp);
		}
		
		if(ishighAccuracy){
			longitude = byteBuf.readDouble();
			latitude = byteBuf.readDouble();
		}else{
			longitude = byteBuf.readFloat();
			latitude = byteBuf.readFloat();
		}
		
		if(isHashStreet){
			int streetLength = byteBuf.readUnsignedShort();
			byte[] streetBytes = new byte[streetLength];
			byteBuf.readBytes(streetBytes);
			street = new String(streetBytes);
		}
	}

	@Override
	public byte[] toPayload(int version) throws Exception {
		return null;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public String getStreet() {
		return street;
	}

	public Date getCreateTime() {
		return createTime;
	}
}

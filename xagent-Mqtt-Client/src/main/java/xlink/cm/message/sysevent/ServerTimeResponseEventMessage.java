package xlink.cm.message.sysevent;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import xlink.cm.message.CMMessage;
import xlink.cm.message.type.CMMessageType;
import xlink.cm.message.type.SysEventMessageType;

public class ServerTimeResponseEventMessage extends CMMessage {

	private short year;
	private byte month;
	private byte day;
	private byte dayOfWeek;
	private byte hour;
	private byte minute;
	private byte second;
	private short zone;

	

	@Override
	public CMMessageType getMessageType() {
		return CMMessageType.SysEvent;
	}

	@Override
	public void parseValue(int version, ByteBuf buf) throws Exception {
//		ByteBuf buf = Unpooled.wrappedBuffer(value);
		year = buf.readShort();
		month = buf.readByte();
		day = buf.readByte();
		dayOfWeek = buf.readByte();
		hour = buf.readByte();
		minute = buf.readByte();
		second = buf.readByte();
		zone = buf.readShort();		
	}

	@Override
	public byte[] toPayload(int version) throws Exception {
		ByteBuf valueBuf = Unpooled.buffer();
		valueBuf.writeShort(year);
		valueBuf.writeByte(month);
		valueBuf.writeByte(day);
		valueBuf.writeByte(dayOfWeek);
		valueBuf.writeByte(hour);
		valueBuf.writeByte(minute);
		valueBuf.writeByte(second);
		valueBuf.writeShort(zone);
		byte[] value = valueBuf.copy().array();
		
		int length = 4 + 4 + value.length;
		ByteBuf buf = Unpooled.buffer(length);
		buf.writeShort(getMessageType().type());
		buf.writeShort(length - 4);
		buf.writeShort(SysEventMessageType.ServerTimeResponse.type());
		buf.writeShort(value.length);
		buf.writeBytes(value);
		return buf.copy().array();
	}

	


	public void setDate(Date dt) {
		DateFormat formatTZ = new SimpleDateFormat("Z");
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		year = (short) cal.get(Calendar.YEAR);
		month = (byte) ( cal.get(Calendar.MONTH) + 1 );
		day = (byte) cal.get(Calendar.DAY_OF_MONTH);
		dayOfWeek = (byte) cal.get(Calendar.DAY_OF_WEEK);
		hour = (byte) cal.get(Calendar.HOUR_OF_DAY);
		minute = (byte) cal.get(Calendar.MINUTE);
		second = (byte) cal.get(Calendar.SECOND);
		String timeZone = formatTZ.format(dt);
		zone = Short.parseShort(timeZone);
	}

	public short getYear() {
		return year;
	}

	public void setYear(short year) {
		this.year = year;
	}

	public byte getMonth() {
		return month;
	}

	public void setMonth(byte month) {
		this.month = month;
	}

	public byte getDay() {
		return day;
	}

	public void setDay(byte day) {
		this.day = day;
	}

	public byte getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(byte dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public byte getHour() {
		return hour;
	}

	public void setHour(byte hour) {
		this.hour = hour;
	}

	public byte getMinute() {
		return minute;
	}

	public void setMinute(byte minute) {
		this.minute = minute;
	}

	public byte getSecond() {
		return second;
	}

	public void setSecond(byte second) {
		this.second = second;
	}

	public short getZone() {
		return zone;
	}

	public void setZone(short zone) {
		this.zone = zone;
	}

}

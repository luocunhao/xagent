package cn.xlink.cmmqttclient.core.utils;

public class MyByteBuf {

	private int size;
	private int avaliableIndex = 0;
	private byte[] array;
	
	private MyByteBuf(int size){
		this.size = size;
		array = new byte[size];
	}
	
	public static MyByteBuf createByteBuf(int size){
		return new MyByteBuf(size);
	}
	
	
	public void writeShort(int data){
		array[avaliableIndex+1] = (byte)(data & 0x00ff);
		array[avaliableIndex] = (byte)((data>>8) & 0x00ff);
		avaliableIndex +=2;
	}
	
	public void writeInt(int data){
		array[avaliableIndex+3] = (byte)(data & 0x00ff);
		array[avaliableIndex+2] = (byte)((data>>8) & 0x00ff);
		array[avaliableIndex+1] = (byte)((data>>16) & 0x00ff);
		array[avaliableIndex] = (byte)((data>>24) & 0x00ff);
		avaliableIndex +=4;
	}
	
	public void writeBytes(byte[] data){
		System.arraycopy(data, 0, array, avaliableIndex, data.length);
	}
	
	public byte[] toArray(){
		return array;
	}
}

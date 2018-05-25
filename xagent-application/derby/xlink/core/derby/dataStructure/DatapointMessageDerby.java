package xlink.core.derby.dataStructure;

import java.sql.Date;

import xlink.core.utils.ByteTools;



public class DatapointMessageDerby {

	private int id;
	private int deviceId;
	private String topic;
	private byte[] data;
	private Date createTime;
	private boolean isSended;
	
	public DatapointMessageDerby(int id, int deviceId,String topic, byte[] data, Date createTime, boolean isSended) {
		super();
		this.id = id;
		this.deviceId = deviceId;
		this.topic = topic;
		this.data = data;
		this.createTime = createTime;
		this.isSended = isSended;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public boolean isSended() {
		return isSended;
	}
	public void setSended(boolean isSended) {
		this.isSended = isSended;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	@Override
	public String toString() {
		return "DatapointMessageDerby [id=" + id + ", deviceId=" + deviceId + ", topic=" + topic + ", data="
				+ ByteTools.printBytes2HexString(data) + ", createTime=" + createTime + ", isSended=" + isSended + "]";
	}
	
	
	
}

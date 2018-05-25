package xlink.cm.message.struct;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.xlink.cmmqttclient.core.utils.CalendarUtils;
import cn.xlink.cmmqttclient.core.utils.ContainerGetter;

public class DatapointSyncStruct {

	private int deviceId;
	private int flags;
	private boolean hasName;
	private String name;
	private boolean hasDatapoint;
	private boolean hasTimestamp;
	private long timestamp;
	private List<Datapoint> datapoint = ContainerGetter.arrayList();
	private int version;
	private boolean unPush;
	
	public DatapointSyncStruct(int deviceId, int flags, boolean hasName, String name, boolean hasDatapoint, boolean hasTimestamp, long timestamp,
			List<Datapoint> datapoint, int version, boolean unPush) {
		super();
		this.deviceId = deviceId;
		this.flags = flags;
		this.hasName = hasName;
		this.name = name;
		this.hasDatapoint = hasDatapoint;
		this.hasTimestamp = hasTimestamp;
		this.timestamp = timestamp;
		this.datapoint = datapoint;
		this.version = version;
		this.unPush = unPush;
	}
	public int getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}
	public int getFlags() {
		return flags;
	}
	public void setFlags(int flags) {
		this.flags = flags;
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
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public boolean isUnPush() {
		return unPush;
	}
	public void setUnPush(boolean unPush) {
		this.unPush = unPush;
	}
	public boolean isHasTimestamp() {
		return hasTimestamp;
	}
	public void setHasTimestamp(boolean hasTimestamp) {
		this.hasTimestamp = hasTimestamp;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	/**
	 * private int deviceId;
	private int flags;
	private boolean hasName;
	private String name;
	private boolean hasDatapoint;
	private boolean hasTimestamp;
	private long timestamp;
	private List<Datapoint> datapoint = ContainerGetter.arrayList();
	private int version;
	private boolean unPush;
	 * @return
	 */
	public JSONObject toJSON(){
		JSONObject root = new JSONObject();
		root.put("id", getDeviceId());
		root.put("nm", getName());
		root.put("t", CalendarUtils.funcGetDate(new Date(getTimestamp())));
		root.put("v", getVersion());
		JSONArray array = new JSONArray();
		for (Datapoint datapoint : getDatapoint()) {
			array.add(datapoint.toJSON());
		}
		root.put("dp", array);
		return root;
	}
	
	
}

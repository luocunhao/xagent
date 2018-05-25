package xlink.core.derby.dataStructure;

import java.sql.Date;

public class DeviceInfo {

	private int deviceId;
	private String productId;
	private String mac;
	private String corpId;
	private String deviceKey;
	private boolean isOnline;
	private Date stateUpdateTime;

	public DeviceInfo(int deviceId, String productId, String mac, String corpId, String deviceKey, boolean isOnline, Date stateUpdateTime) {
		super();
		this.deviceId = deviceId;
		this.productId = productId;
		this.mac = mac;
		this.corpId = corpId;
		this.deviceKey = deviceKey;
		this.isOnline = isOnline;
		this.stateUpdateTime = stateUpdateTime;
	}

	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceKey() {
		return deviceKey;
	}

	public void setDeviceKey(String deviceKey) {
		this.deviceKey = deviceKey;
	}

	public boolean isOnline() {
		return isOnline;
	}

	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}

	public Date getStateUpdateTime() {
		return stateUpdateTime;
	}

	public void setStateUpdateTime(Date stateUpdateTime) {
		this.stateUpdateTime = stateUpdateTime;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getCorpId() {
		return corpId;
	}

	public void setCorpId(String corpId) {
		this.corpId = corpId;
	}

	@Override
	public String toString() {
		return "DeviceInfo [deviceId=" + deviceId + ", productId=" + productId + ", mac=" + mac + ", corpId=" + corpId + ", deviceKey=" + deviceKey + ", isOnline=" + isOnline + ", stateUpdateTime="
				+ stateUpdateTime + "]";
	}

}

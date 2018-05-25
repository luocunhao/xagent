package xlink.cm.message.struct;

public class ActivationResultStruct {

	private String mac;
	private int code;
	private int deviceId;
	private String key;
	public ActivationResultStruct(String mac, int code, int deviceId, String key) {
		super();
		this.mac = mac;
		this.code = code;
		this.deviceId = deviceId;
		this.key = key;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public int getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	
}

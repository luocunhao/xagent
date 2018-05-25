package xlink.cm.agent.ptp.dataStruture;

/**
 * 设备实体
 *
 * @author xlink
 *
 */
public class DDevice {
	/**
	 * xlink平台的MAC，也等于厂商的设备唯一标识字段.
	 */
	private String mac;
	/**
	 * xlink平台的设备唯一标识
	 */
	private int deviceId;
	/**
	 * xlink平台的设备Key
	 */
	private String key;
	/**
	 * xlink平台产品的productId
	 */
	private String productdId;

	public DDevice(String mac, int deviceId, String key, String productId) {
		this.mac = mac;
		this.deviceId = deviceId;
		this.key = key;
		this.productdId = productId;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
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

	public String getProductdId() {
		return productdId;
	}

	public void setProductdId(String productdId) {
		this.productdId = productdId;
	}

	@Override
	public String toString() {
		return "DDevice [mac=" + mac + ", deviceId=" + deviceId + ", key=" + key + "]";
	}

}

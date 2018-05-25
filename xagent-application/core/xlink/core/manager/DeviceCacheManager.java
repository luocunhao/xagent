package xlink.core.manager;

import xlink.core.derby.dataStructure.DeviceInfo;
import xlink.core.derby.service.DeviceInfoService;
import xlink.mqtt.client.utils.LogHelper;

public class DeviceCacheManager {

	private static final  DeviceCacheManager singleton = new DeviceCacheManager();
	public DeviceCacheManager() {
		
	}
	
	public static DeviceCacheManager instance() {
		return singleton;
	}
	
	/**
	 * 保存信息，如果已存在旧数据，先删除后插入
	 * @param ptpId
	 * @param deviceKey
	 */
	public void saveDevicebyProductIdAndMac(String ptpId,DeviceInfo deviceKey) {
		if(deviceKey == null) {
			return ;
		}
		String mac = deviceKey.getMac();
		String productId = deviceKey.getProductId();
		DeviceInfo device = DeviceInfoService.instance().findByProductIdAndMac(ptpId, productId, mac);
		//如果已经存在，删除它
		if(device != null) {
			boolean deleteResult = DeviceInfoService.instance().deleteDeviceInfo(ptpId, productId,mac);
			LogHelper.LOGGER().info("delete old device info, deviceId is {},result is {}", device.getDeviceId(),deleteResult);	
		}
		DeviceInfoService.instance().insert(ptpId, deviceKey);
	}
	
}

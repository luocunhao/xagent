package xlink.xagent.ptp.wind.main;

import xlink.xagent.ptp.wind.domain.MacAddressIdMap;

import java.util.HashMap;
import java.util.Map;

public class DeviceConfig {
    private static final DeviceConfig singleton = new DeviceConfig();
    public static DeviceConfig instance(){
        return singleton;
    }
    private static Map<String, MacAddressIdMap> deviceMap = new HashMap<>();
    public void init(){
        deviceMap.put("000",new MacAddressIdMap("8886",1684318910,"1"));
        deviceMap.put("001",new MacAddressIdMap("8887",1684313978,"2"));
        deviceMap.put("002",new MacAddressIdMap("8888",1684312946,"3"));
    }
    public static Map<String,MacAddressIdMap> getDeviceMap(){
        return deviceMap;
    }
}

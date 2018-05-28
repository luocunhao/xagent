package xlink.xagent.ptp.zr.main;

import xlink.xagent.ptp.zr.domain.MacSlaverIdMap;

import java.util.HashMap;
import java.util.Map;

public class DeviceConfig {
    private static final DeviceConfig singleton = new DeviceConfig();
    public static DeviceConfig instance(){
        return singleton;
    }
    private static Map<Integer,MacSlaverIdMap> deviceMap = new HashMap<>();
    public void init(){
        deviceMap.put(1684312438,new MacSlaverIdMap("5F6D",1,"das"));
        //deviceMap.put(1684316021,new MacSlaverIdMap("13254",2,"ssf"));
    }
    public static Map<Integer,MacSlaverIdMap> getDeviceMap(){
        return deviceMap;
    }
}

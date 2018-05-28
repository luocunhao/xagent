package xlink.xagent.ptp.wind.domain;

public class MacAddressIdMap {

    private String mac;
    private Integer device_id;
    private String sn;

    public MacAddressIdMap(String mac, Integer device_id, String sn) {
        this.mac = mac;
        this.device_id = device_id;
        this.sn = sn;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public Integer getDevice_id() {
        return device_id;
    }

    public void setDevice_id(Integer device_id) {
        this.device_id = device_id;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }
}

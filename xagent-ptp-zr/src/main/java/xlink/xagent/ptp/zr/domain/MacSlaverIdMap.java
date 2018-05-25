package xlink.xagent.ptp.zr.domain;

public class MacSlaverIdMap {
    private String mac;
    private int slaverid;
    private String sn;

    public MacSlaverIdMap(String mac, int slaverid,String sn) {
        this.mac = mac;
        this.slaverid = slaverid;
        this.sn = sn;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getSlaverid() {
        return slaverid;
    }

    public void setSlaverid(int slaverid) {
        this.slaverid = slaverid;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }
}

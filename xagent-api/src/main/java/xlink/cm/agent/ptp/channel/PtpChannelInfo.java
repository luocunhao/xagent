package xlink.cm.agent.ptp.channel;

/**
 * 设备连接通道
 * 
 * @author xlink
 *
 */
public class PtpChannelInfo {

  /**
   * 设备通道的id
   */
  private int socktHash;
  /**
   * xlink平台的设备ID
   */
  private int deviceId;
  /**
   * xlink平台的设备MAC地址，也是厂商的设备ID
   */
  private String mac;

  public PtpChannelInfo(int socktHash, int deviceId, String mac) {
    super();
    this.socktHash = socktHash;
    this.deviceId = deviceId;
    this.mac = mac;
  }

  public int getSocktHash() {
    return socktHash;
  }

  public int getDeviceId() {
    return deviceId;
  }

  public String getMac() {
    return mac;
  }

  public void setDeviceId(int deviceId) {
    this.deviceId = deviceId;
  }

  public void setMac(String mac) {
    this.mac = mac;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + socktHash;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PtpChannelInfo other = (PtpChannelInfo) obj;
    if (socktHash != other.socktHash)
      return false;
    return true;
  }


}

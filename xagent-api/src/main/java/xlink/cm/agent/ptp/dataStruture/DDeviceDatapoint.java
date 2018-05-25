package xlink.cm.agent.ptp.dataStruture;

import java.util.Map;

/**
 * 设备的数据端点
 * 
 * @author xlink
 *
 */
public class DDeviceDatapoint {

  /**
   * xlink平台的设备ID
   */
  private int deviceId;

  private Map<Integer, DPtpDatapoint> datapoints;

  public DDeviceDatapoint(int deviceId, Map<Integer, DPtpDatapoint> datapoints) {
    this.deviceId = deviceId;
    this.datapoints = datapoints;
  }

  public int getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(int deviceId) {
    this.deviceId = deviceId;
  }

  public Map<Integer, DPtpDatapoint> getDatapoints() {
    return datapoints;
  }

  public void setDatapoints(Map<Integer, DPtpDatapoint> datapoints) {
    this.datapoints = datapoints;
  }


}

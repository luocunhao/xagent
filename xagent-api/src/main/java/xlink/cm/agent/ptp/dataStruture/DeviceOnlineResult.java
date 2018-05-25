package xlink.cm.agent.ptp.dataStruture;

public class DeviceOnlineResult {

  /**
   * 返回码说明：
   *  0  上线成功
   *  1   Device Key 不正确
   *  2   证书未授权
   *  3   服务不可用
   *  4   设备不存在
   * 
   */
  
  private int ReturnCode;

  public DeviceOnlineResult(int returnCode) {
    super();
    ReturnCode = returnCode;
  }

  public int getReturnCode() {
    return ReturnCode;
  }

  public void setReturnCode(int returnCode) {
    ReturnCode = returnCode;
  }
  
  
  
}

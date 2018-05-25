package xlink.cm.agent.ptp.dataStruture;

/**
 * 发送设备下线消息的结果
 * 
 * @author xlink
 *
 */
public class DeviceOfflineResult {

  /**
   * 返回码说明： 0 下线成功 -1 失败
   * 
   */
  private int returnCode;

  public int getReturnCode() {
    return returnCode;
  }

  public void setReturnCode(int returnCode) {
    this.returnCode = returnCode;
  }

  public DeviceOfflineResult(int returnCode) {
    super();
    this.returnCode = returnCode;
  }


}

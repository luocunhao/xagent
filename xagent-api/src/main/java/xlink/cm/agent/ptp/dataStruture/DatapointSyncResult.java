package xlink.cm.agent.ptp.dataStruture;

/**
 * 向xlink连接服务器同步设备信息的结果
 * 
 * @author xlink
 *
 */
public class DatapointSyncResult {
  /**
   * 返回码说明： 0 上传成功 -1 失败
   * 
   */
  private int returnCode;

  public DatapointSyncResult(int returnCode) {
    super();
    this.returnCode = returnCode;
  }

  public int getReturnCode() {
    return returnCode;
  }

  public void setReturnCode(int returnCode) {
    this.returnCode = returnCode;
  }


}

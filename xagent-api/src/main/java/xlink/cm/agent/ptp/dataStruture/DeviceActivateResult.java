package xlink.cm.agent.ptp.dataStruture;

/**
 * 设备激活结果
 * 
 * @author xlink
 *
 */
public class DeviceActivateResult {

  /**
   * 激活结果。返回值说明：<br>
   * 0 激活成功<br>
   * 1 激活失败，设备不存在<br>
   * 2 激活失败，激活码错误<br>
   * 3 激活失败，未授权<br>
   * 4 SN字段跟已有设备不对应<br>
   * 5 产品授权配额不足<br>
   * 6 Mac不能重复使用
   */
  private int resultCode;

  /**
   * 激活后获取的设备信息
   */
  private DDevice device;



  public DeviceActivateResult(int resultCode, DDevice device) {
    super();
    this.resultCode = resultCode;
    this.device = device;
  }

  public int getResultCode() {
    return resultCode;
  }

  public void setResultCode(int resultCode) {
    this.resultCode = resultCode;
  }

  public DDevice getDevice() {
    return device;
  }

  public void setDevice(DDevice device) {
    this.device = device;
  }


}

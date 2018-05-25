package xlink.cm.agent.ptp.exception;

/**
 * 设备不存在异常
 * 
 * @author xlink
 *
 */
public class DeviceNotFoundException extends PtpException {

  /**
   * 
   */
  private static final long serialVersionUID = 8647333587736090140L;

  public DeviceNotFoundException(String msg, Throwable cause) {
    super(msg, cause);
    // TODO Auto-generated constructor stub
  }

  public DeviceNotFoundException(String msg) {
    super(msg);
  }
}

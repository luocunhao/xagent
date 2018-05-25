package xlink.cm.agent.ptp.exception;

/**
 * PtP插件运行过程中产生的异常基类
 * 
 * @author xlink
 *
 */
public class PtpException extends Exception {

  private String msg;

  private Throwable cause;

  /**
   * 
   * @param msg 错误信息
   * @param cause 详细的堆栈信息
   */
  public PtpException(String msg, Throwable cause) {
    super();
    this.msg = msg;
    this.cause = cause;
  }


  public PtpException(String msg) {
    super();
    this.msg = msg;
    this.cause = super.getCause();
  }

}

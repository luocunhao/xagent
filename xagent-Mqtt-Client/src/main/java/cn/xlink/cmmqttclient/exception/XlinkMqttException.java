package cn.xlink.cmmqttclient.exception;

@SuppressWarnings("serial")
public class XlinkMqttException extends Exception {

  public static int MQTT_CONNECTION_TIMEOUT = 10001;

  /**
   * 解析 xlink cm 消息错误
   */
  public static int XLINK_CM_MESSAGE_PHASE_ERROR = 10002;
  /**
   * 
   */
  public static int MQTT_CLIENT_CLOSED = 10003;
  /**
   * mqtt token 等待超时
   */
  public static int MQTT_TOKEN_TIME_OUT = 10004;
  /**
   * mqtt connect auth failed
   */
  public static int MQTT_CONNECT_AUTH_FAILED = 10005;
  
  public static int MQTT_TOKEN_UNKNOWN_ERROR = 10006;



  private int reasonCode;
  private Throwable cause;

  public XlinkMqttException(int reasonCode, Throwable e) {
    this.cause = e;
    this.reasonCode = reasonCode;
  }

  public int getReasonCode() {
    return reasonCode;
  }

  public void setReasonCode(int reasonCode) {
    this.reasonCode = reasonCode;
  }

  public Throwable getCause() {
    return cause;
  }

  public void setCause(Throwable cause) {
    this.cause = cause;
  }


}

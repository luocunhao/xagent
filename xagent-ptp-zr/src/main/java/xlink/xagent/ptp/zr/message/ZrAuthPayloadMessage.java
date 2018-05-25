package xlink.xagent.ptp.zr.message;

public class ZrAuthPayloadMessage implements ZrPayloadMessage {

  private String identify;
  private String password;

  public String getIdentify() {
    return identify;
  }

  public void setIdentify(String identify) {
    this.identify = identify;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public ZrAuthPayloadMessage(String identify, String password) {
    super();
    this.identify = identify;
    this.password = password;
  }

  @Override
  public String toString() {
    return "ZrAuthPayloadMessage [identify=" + identify + ", password=" + password + "]";
  }

}

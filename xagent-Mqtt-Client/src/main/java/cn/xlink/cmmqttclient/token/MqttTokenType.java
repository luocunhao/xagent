package cn.xlink.cmmqttclient.token;

/**
 * 

 * @ClassName MqttTokenType

 * @Description token的类型

 * @author linsida@xlink.cn

 * @date 2018年4月20日
 */
public enum MqttTokenType {

  XLINK_CM(1),Mqtt(2);
  
  private int type;
  private MqttTokenType(int type) {
    this.type = type;
  }
  public int getType() {
    return type;
  }
  
  
}

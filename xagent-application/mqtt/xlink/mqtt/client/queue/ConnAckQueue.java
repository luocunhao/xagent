package xlink.mqtt.client.queue;

public class ConnAckQueue implements MqttLogicQueue {

  private String mac;

  private int deviceId;

  private int result;

  private String deviceKey;



  public ConnAckQueue(String mac, int deviceId, int result, String deviceKey) {
    this.mac = mac;
    this.deviceId = deviceId;
    this.result = result;
    this.deviceKey = deviceKey;
  }



  public String getMac() {
    return mac;
  }



  public void setMac(String mac) {
    this.mac = mac;
  }



  public int getDeviceId() {
    return deviceId;
  }



  public void setDeviceId(int deviceId) {
    this.deviceId = deviceId;
  }



  public int getResult() {
    return result;
  }



  public void setResult(int result) {
    this.result = result;
  }



  public String getDeviceKey() {
    return deviceKey;
  }



  public void setDeviceKey(String deviceKey) {
    this.deviceKey = deviceKey;
  }



  @Override
  public QueueType getQueueType() {
    return QueueType.MQTT_CONNACK;
  }


}

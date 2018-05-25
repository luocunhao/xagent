package xlink.mqtt.client.Future;

import xlink.mqtt.client.exception.XlinkMqttException;
import xlink.mqtt.client.token.MqttToken;

public interface IMqttActionListener {

  public void onSucess(MqttToken actionToken);


  public void onFailure(MqttToken actionToken, XlinkMqttException exception);
}

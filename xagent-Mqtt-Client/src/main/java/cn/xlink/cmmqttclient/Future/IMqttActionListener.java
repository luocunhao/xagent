package cn.xlink.cmmqttclient.Future;

import cn.xlink.cmmqttclient.exception.XlinkMqttException;
import cn.xlink.cmmqttclient.token.MqttToken;

public interface IMqttActionListener {

  public void onSucess(MqttToken<?> actionToken);


  public void onFailure(MqttToken<?> actionToken, XlinkMqttException exception);
}

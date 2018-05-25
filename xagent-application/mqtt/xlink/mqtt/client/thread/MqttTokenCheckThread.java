package xlink.mqtt.client.thread;

import java.util.List;

import xlink.mqtt.client.exception.XlinkMqttException;
import xlink.mqtt.client.token.MqttToken;
import xlink.mqtt.client.token.MqttTokenManager;
import xlink.mqtt.client.utils.LogHelper;

public class MqttTokenCheckThread extends Thread {

  public MqttTokenCheckThread() {
    setName("mqtt-token-check-thread");
  }

  @Override
  public void run() {
    for (;;) {
      try {

        List<MqttToken> tokenList = MqttTokenManager.instance().getAllToken();
        // token有效时间是1分钟
        long expiredTime = System.currentTimeMillis() - 60000;
        for (MqttToken token : tokenList) {
          if (token.isCompleted() == false && token.getCreatedTimeStamp() <= expiredTime) {
            MqttTokenManager.instance().remove(token.getMessageID());
            token
                .setException(new XlinkMqttException(XlinkMqttException.MQTT_TOKEN_TIME_OUT, null));
            token.markComplete(false);
            LogHelper.LOGGER().debug("token {} expired, mark it failed.", token.getMessageID());
          }
        }
        Thread.sleep(60000);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

  }
}

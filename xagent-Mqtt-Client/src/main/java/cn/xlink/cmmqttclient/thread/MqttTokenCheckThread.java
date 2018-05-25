package cn.xlink.cmmqttclient.thread;

import java.util.List;
import java.util.concurrent.TimeoutException;

import cn.xlink.cmmqttclient.core.utils.LogHelper;
import cn.xlink.cmmqttclient.exception.XlinkMqttException;
import cn.xlink.cmmqttclient.token.MqttToken;
import cn.xlink.cmmqttclient.token.MqttTokenManager;

public class MqttTokenCheckThread extends Thread {

  public MqttTokenCheckThread() {
    setName("mqtt-token-check-thread");
  }

  @Override
  public void run() {
    for (;;) {
      try {

        List<MqttToken<?>> tokenList = MqttTokenManager.instance().getAllToken();
        // token有效时间是1分钟
        long expiredTime = System.currentTimeMillis() - 60000;
        for (MqttToken<?> token : tokenList) {
          if (token.isCompleted() == false && token.getCreatedTimeStamp() <= expiredTime) {
            MqttTokenManager.instance().remove(token.getMessageID());
            token.completeException(new XlinkMqttException(XlinkMqttException.MQTT_TOKEN_TIME_OUT, new TimeoutException()));
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

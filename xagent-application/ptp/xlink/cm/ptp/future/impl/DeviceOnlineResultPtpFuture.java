package xlink.cm.ptp.future.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import xlink.cm.agent.ptp.dataStruture.DeviceOnlineResult;
import xlink.cm.message.DeviceOnlineResultMessage;
import xlink.mqtt.client.token.MqttToken;

public class DeviceOnlineResultPtpFuture extends AbstractPtpFuture<DeviceOnlineResult> {

  public DeviceOnlineResultPtpFuture(MqttToken mqttToken) {
    super(mqttToken);
  }

  @Override
  public DeviceOnlineResult get() throws InterruptedException, ExecutionException {
    DeviceOnlineResultMessage onlineResult = (DeviceOnlineResultMessage) mqttToken.getResponseMsg();
    return new DeviceOnlineResult(onlineResult.getCode());
  }

  @Override
  public DeviceOnlineResult get(long timeout, TimeUnit unit)
      throws InterruptedException, ExecutionException, TimeoutException {
    DeviceOnlineResultMessage onlineResult = (DeviceOnlineResultMessage) mqttToken.getResponseMsg();
    return new DeviceOnlineResult(onlineResult.getCode());
  }

}

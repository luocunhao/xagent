package xlink.cm.ptp.future.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import xlink.cm.agent.ptp.dataStruture.DeviceOfflineResult;
import xlink.mqtt.client.token.MqttToken;

public class DeviceOfflineResultPtpFuture extends AbstractPtpFuture<DeviceOfflineResult> {

  public DeviceOfflineResultPtpFuture(MqttToken mqttToken) {
    super(mqttToken);
  }

  @Override
  public DeviceOfflineResult get() throws InterruptedException, ExecutionException {
    int returnCode = mqttToken.isCompleted() ? 0 : -1;
    return new DeviceOfflineResult(returnCode);
  }

  @Override
  public DeviceOfflineResult get(long timeout, TimeUnit unit)
      throws InterruptedException, ExecutionException, TimeoutException {
    int returnCode = mqttToken.isCompleted() ? 0 : -1;
    return new DeviceOfflineResult(returnCode);
  }

}

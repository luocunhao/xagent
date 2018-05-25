package xlink.cm.ptp.future.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import xlink.cm.agent.ptp.dataStruture.DatapointSyncResult;
import xlink.mqtt.client.token.MqttToken;

public class DeviceDatapointSyncResultPtpFuture extends AbstractPtpFuture<DatapointSyncResult> {

  public DeviceDatapointSyncResultPtpFuture(MqttToken mqttToken) {
    super(mqttToken);
  }

  @Override
  public DatapointSyncResult get() throws InterruptedException, ExecutionException {
    int resultCode = mqttToken.isSuccess() ? 0 : -1;
    return new DatapointSyncResult(resultCode);
  }

  @Override
  public DatapointSyncResult get(long timeout, TimeUnit unit)
      throws InterruptedException, ExecutionException, TimeoutException {
    int resultCode = mqttToken.isSuccess() ? 0 : -1;
    return new DatapointSyncResult(resultCode);
  }

}

package xlink.cm.ptp.future.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import xlink.cm.agent.ptp.dataStruture.DDevice;
import xlink.cm.agent.ptp.dataStruture.DeviceActivateResult;
import xlink.cm.message.ActivationResultMessage;
import xlink.cm.message.struct.ActivationResultStruct;
import xlink.mqtt.client.token.MqttToken;

public class DDevicePtpFuture extends AbstractPtpFuture<DeviceActivateResult> {
  String productId = null;
  public DDevicePtpFuture(MqttToken mqttToken,String productId) {
    super(mqttToken);
    this.productId = productId;
  }

  @Override
  public DeviceActivateResult get() throws InterruptedException, ExecutionException {
    ActivationResultMessage activationResult = (ActivationResultMessage) mqttToken.getResponseMsg();
    ActivationResultStruct activateDevice = activationResult.getActivationResultList().get(0);
    int resultCode = activateDevice.getCode();
    DDevice device =
        new DDevice(activateDevice.getMac(), activateDevice.getDeviceId(), activateDevice.getKey(),productId);
    return new DeviceActivateResult(resultCode, device);
  }

  @Override
  public DeviceActivateResult get(long timeout, TimeUnit unit)
      throws InterruptedException, ExecutionException, TimeoutException {
    ActivationResultMessage activationResult = (ActivationResultMessage) mqttToken.getResponseMsg();
    ActivationResultStruct activateDevice = activationResult.getActivationResultList().get(0);
    int resultCode = activateDevice.getCode();
    DDevice device =
        new DDevice(activateDevice.getMac(), activateDevice.getDeviceId(), activateDevice.getKey(),productId);
    return new DeviceActivateResult(resultCode, device);
  }



}

package xlink.mqtt.client.queue.subclass;

import xlink.mqtt.client.MqttClient;
import xlink.mqtt.client.queue.AsyncQueue;
import xlink.mqtt.client.queue.QueueType;

public class PtpDeviceOnlineAsyncQueue extends AsyncQueue {

  @Override
  public QueueType getQueueType() {
    return QueueType.ASYNC_PTP_DEVICE_ONLINE;
  }

  private int deviceId;

  private MqttClient client;

  public PtpDeviceOnlineAsyncQueue(int deviceId, MqttClient client) {
    this.deviceId = deviceId;
    this.client = client;
  }

  public int getDeviceId() {
    return deviceId;
  }

  public MqttClient getClient() {
    return client;
  }



}

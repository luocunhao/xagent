package xlink.mqtt.client.queue.subclass;

import xlink.mqtt.client.MqttClient;
import xlink.mqtt.client.queue.AsyncQueue;
import xlink.mqtt.client.queue.QueueType;

public class MqttConnAckAsyncQueue extends AsyncQueue {

  @Override
  public QueueType getQueueType() {
    // TODO Auto-generated method stub
    return QueueType.ASYNC_MQTT_CONNACK;
  }

  private MqttClient client;

  public MqttConnAckAsyncQueue(MqttClient client) {
    super();
    this.client = client;
  }

  public MqttClient getClient() {
    return client;
  }

  public void setClient(MqttClient client) {
    this.client = client;
  }



}

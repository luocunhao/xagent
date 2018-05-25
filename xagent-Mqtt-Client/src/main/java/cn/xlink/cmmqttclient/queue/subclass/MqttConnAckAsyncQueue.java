package cn.xlink.cmmqttclient.queue.subclass;

import cn.xlink.cmmqttclient.client.MqttClient;
import cn.xlink.cmmqttclient.queue.AsyncQueue;
import cn.xlink.cmmqttclient.queue.QueueType;

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

  @Override
  public void run() {
    // TODO Auto-generated method stub
    
  }



}

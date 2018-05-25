package xlink.mqtt.client.queue.subclass;

import io.netty.handler.codec.mqtt.MqttMessage;
import xlink.mqtt.client.MqttComms;
import xlink.mqtt.client.queue.LogicQueue;
import xlink.mqtt.client.queue.QueueType;

public class MqttReceiveLogicQueue extends LogicQueue {

  @Override
  public QueueType getQueueType() {
    // TODO Auto-generated method stub
    return QueueType.LOGIC_MQTT_RECEIVE;
  }

  private MqttComms mqttComms;
  private MqttMessage mqttMessage;

  public MqttReceiveLogicQueue(MqttComms mqttComms, MqttMessage mqttMessage) {
    this.mqttComms = mqttComms;
    this.mqttMessage = mqttMessage;
  }

  public MqttComms getMqttComms() {
    return mqttComms;
  }

  public void setMqttComms(MqttComms mqttComms) {
    this.mqttComms = mqttComms;
  }

  public MqttMessage getMqttMessage() {
    return mqttMessage;
  }

  public void setMqttMessage(MqttMessage mqttMessage) {
    this.mqttMessage = mqttMessage;
  }



}

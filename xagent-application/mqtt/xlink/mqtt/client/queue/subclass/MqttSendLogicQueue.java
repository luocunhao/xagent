package xlink.mqtt.client.queue.subclass;

import io.netty.handler.codec.mqtt.MqttMessage;
import xlink.mqtt.client.MqttComms;
import xlink.mqtt.client.queue.LogicQueue;
import xlink.mqtt.client.queue.QueueType;

public class MqttSendLogicQueue extends LogicQueue {

  @Override
  public QueueType getQueueType() {
    // TODO Auto-generated method stub
    return QueueType.LOGIC_MQTT_SEND;
  }
  private MqttComms mqttComms;
  private MqttMessage msg;



  public MqttSendLogicQueue(MqttComms mqttComms, MqttMessage msg) {

    this.mqttComms = mqttComms;
    this.msg = msg;
  }

  public MqttComms getMqttComms() {
    return mqttComms;
  }

  public void setMqttComms(MqttComms mqttComms) {
    this.mqttComms = mqttComms;
  }

  public MqttMessage getMsg() {
    return msg;
  }

  public void setMsg(MqttMessage msg) {
    this.msg = msg;
  }



}

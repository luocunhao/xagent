package cn.xlink.cmmqttclient.queue.subclass;

import cn.xlink.cmmqttclient.client.MqttComms;
import cn.xlink.cmmqttclient.queue.AsyncQueue;
import cn.xlink.cmmqttclient.queue.QueueType;
import io.netty.handler.codec.mqtt.MqttMessage;

public class MqttSendAsyncQueue extends AsyncQueue {

  @Override
  public QueueType getQueueType() {
    // TODO Auto-generated method stub
    return QueueType.ASYNC_MQTT_SEND;
  }
  private MqttComms mqttComms;
  private MqttMessage msg;



  public MqttSendAsyncQueue(MqttComms mqttComms, MqttMessage msg) {

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

  @Override
  public void run() {
    mqttComms.sendDataDirectly(msg);
    // 消息发送完成,记数减一
    mqttComms.messageCountDecre(1);
    
  }



}

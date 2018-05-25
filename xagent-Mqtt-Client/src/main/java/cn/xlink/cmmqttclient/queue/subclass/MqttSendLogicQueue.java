package cn.xlink.cmmqttclient.queue.subclass;

import cn.xlink.cmmqttclient.client.MqttComms;
import cn.xlink.cmmqttclient.queue.LogicQueue;
import io.netty.handler.codec.mqtt.MqttMessage;

/**
 * 

 * @ClassName MqttSendLogicQueue

 * @Description  mqtt客户端异步发送消息

 * @author linsida@xlink.cn

 * @date 2018年4月21日
 */
public class MqttSendLogicQueue extends LogicQueue {

  private MqttComms mqttComms;
  private MqttMessage msg;



  public MqttSendLogicQueue(int sequenceId,MqttComms mqttComms, MqttMessage msg) {
   super(sequenceId);
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

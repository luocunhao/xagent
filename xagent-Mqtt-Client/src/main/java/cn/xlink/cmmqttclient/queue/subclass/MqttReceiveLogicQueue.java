package cn.xlink.cmmqttclient.queue.subclass;

import cn.xlink.cmmqttclient.client.MqttComms;
import cn.xlink.cmmqttclient.core.utils.LogHelper;
import cn.xlink.cmmqttclient.core.utils.StringTools;
import cn.xlink.cmmqttclient.handler.MqttMessageProcessor;
import cn.xlink.cmmqttclient.queue.LogicQueue;
import cn.xlink.cmmqttclient.token.MqttToken;
import cn.xlink.cmmqttclient.token.MqttTokenManager;
import cn.xlink.cmmqttclient.type.XlinkMqttTopicType;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttSubAckMessage;
import io.netty.handler.codec.mqtt.MqttUnsubAckMessage;

public class MqttReceiveLogicQueue extends LogicQueue {



  private MqttComms mqttComms;
  private MqttMessage mqttMessage;

  public MqttReceiveLogicQueue(int sequenceId,MqttComms mqttComms, MqttMessage mqttMessage) {
    super(sequenceId);
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

  @Override
  public void run() {

    String messageId = null;
    switch (mqttMessage.fixedHeader().messageType()) {
      case CONNACK:
        messageId = mqttComms.mqttClient().certId();
        break;
      case PUBACK:
        messageId = StringTools.getString(((MqttPubAckMessage) mqttMessage).variableHeader().messageId());
        break;
      case PUBLISH: {
        MqttPublishMessage pubMsg = (MqttPublishMessage) mqttMessage;
        String topic = pubMsg.variableHeader().topicName();
        XlinkMqttTopicType topicType = XlinkMqttTopicType.fromType(topic);
        messageId = topicType.getTokenId(mqttComms.certId(), pubMsg);
      }
        break;
      case SUBACK:
        messageId = StringTools.getString(((MqttSubAckMessage) mqttMessage).variableHeader().messageId());
        break;
      case UNSUBACK:
        messageId = StringTools.getString(((MqttUnsubAckMessage) mqttMessage).variableHeader().messageId());
        break;
      case PINGRESP:
        return;
      default:
        break;
    }
    MqttToken token = MqttTokenManager.instance().remove(messageId);
    try {
      MqttMessageProcessor.instance().process(mqttMessage, token, mqttComms);
    } catch (Exception e) {
      LogHelper.LOGGER().error(e, "handler %s message %s failed", mqttComms.certId(),mqttMessage.fixedHeader().messageType());
    }
    // 消息处理完成，计数减一
    mqttComms.messageCountDecre(1);
    
  }



}

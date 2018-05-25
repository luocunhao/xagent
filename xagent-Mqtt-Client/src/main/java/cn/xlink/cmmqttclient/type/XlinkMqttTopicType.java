package cn.xlink.cmmqttclient.type;

import cn.xlink.cmmqttclient.core.XlinkPublishMessageFactory;
import cn.xlink.cmmqttclient.core.utils.MqttTopicMatcher;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import xlink.cm.message.ActivationMessage;
import xlink.cm.message.ActivationResultMessage;
import xlink.cm.message.struct.ActivationResultStruct;
import xlink.cm.message.struct.ActivationStruct;

public enum XlinkMqttTopicType {

  Unknown("unknown", "unknown") {
    @Override
    public String getTokenId(String clientId, MqttPublishMessage message) {
      // TODO Auto-generated method stub
      return "unknown";
    }
  },

  DEVICE_ACTIVE("$xlink/device/activation", "$1") {
    @Override
    public String getTokenId(String clientId, MqttPublishMessage message) {
      ByteBuf bytebuf = message.payload();
      bytebuf.markReaderIndex();
      bytebuf.skipBytes(4);
      ActivationMessage activationResult = new ActivationMessage();
      activationResult.parseValue(XlinkPublishMessageFactory.version, bytebuf);
      ActivationStruct struct = activationResult.getActivationMessageList().get(0);
      bytebuf.resetReaderIndex();
      return "activation-" + clientId + "-" + struct.getMac();
    }
  },

  DEVICE_ACTIVE_RESULT("$xlink/device/activation/result", "$2") {
    @Override
    public String getTokenId(String clientId, MqttPublishMessage message) {
      ByteBuf bytebuf = message.payload();
      bytebuf.markReaderIndex();
      bytebuf.skipBytes(4);
      ActivationResultMessage activationResult = new ActivationResultMessage();
      activationResult.parseValue(XlinkPublishMessageFactory.version, bytebuf);
      ActivationResultStruct struct = activationResult.getActivationResultList().get(0);
      bytebuf.resetReaderIndex();
      return "activation-" + clientId + "-" + struct.getMac();
    }
  },

  DEVICE_ONLINE("$xlink/device/{device_id}/state/online", "$3/{device_id}") {

    @Override
    public String getTokenId(String clientId, MqttPublishMessage message) {
      // TODO Auto-generated method stub
      return Integer.toString(message.variableHeader().messageId());
    }

  },

  DEVICE_OFFLINE("$xlink/device/{device_id}/state/offline", "$5/{device_id}") {

    @Override
    public String getTokenId(String clientId, MqttPublishMessage message) {
      // TODO Auto-generated method stub
      return Integer.toString(message.variableHeader().messageId());
    }

  },

  DEVICE_DATAPOINT_SYNC("$xlink/device/{device_id}/datapoint/sync", "$6/{device_id}") {

    @Override
    public String getTokenId(String clientId, MqttPublishMessage message) {
      // TODO Auto-generated method stub
      return Integer.toString(message.variableHeader().messageId());
    }

  },

  DEVICE_DATAPOINT_SET("$xlink/device/{device_id}/datapoint/set/result", "$7/{device_id}") {

    @Override
    public String getTokenId(String clientId, MqttPublishMessage message) {
      return Integer.toString(message.variableHeader().messageId());
    }

  },

  SYSTEM_EVENT("$xlink/sys/event", "$e") {

    @Override
    public String getTokenId(String clientId, MqttPublishMessage message) {
      // TODO Auto-generated method stub
      return "";
    }

  },
  /**
   * 设备状态
   */
  DEVICE_STATE("$xlink/device/{device_id}/state", "$y/{device_id}") {

    @Override
    public String getTokenId(String clientId, MqttPublishMessage message) {
      return Integer.toString(message.variableHeader().messageId());
    }

  }
  ;

  private String topic;
  private String shortTopic;
  private MqttTopicMatcher topicMatcher;
  private MqttTopicMatcher shortTopicMatcher;

  private XlinkMqttTopicType(String topic, String shortTopic) {
    this.topic = topic;
    this.shortTopic = shortTopic;
    topicMatcher = new MqttTopicMatcher(topic);
    shortTopicMatcher = new MqttTopicMatcher(shortTopic);
  }

  public String type() {
    return this.topic;
  }

  public String shortTopic() {
    return this.shortTopic;
  }

  abstract public String getTokenId(String clientId, MqttPublishMessage message);

  public static XlinkMqttTopicType fromType(String type) {
    for (XlinkMqttTopicType topic : values()) {
      if (topic.topicMatcher.match(type) || topic.shortTopicMatcher.match(type)) {
        return topic;
      }
    }
    return Unknown;
  }

  public String getParmValue(String parmName, String topic) {
    String parmValue = topicMatcher.getParamValue(topic, parmName);
    if (parmValue == null) {
      return shortTopicMatcher.getParamValue(topic, parmName);
    }
    return parmValue;
  }


}

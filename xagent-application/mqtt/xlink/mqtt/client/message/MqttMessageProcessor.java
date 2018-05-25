package xlink.mqtt.client.message;

import java.util.Map;

import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttSubAckMessage;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import xlink.cm.message.CMMessage;
import xlink.cm.message.DatapointSetMessage;
import xlink.cm.message.DeviceStateMessage;
import xlink.cm.message.SysEventMessage;
import xlink.core.utils.ContainerGetter;
import xlink.core.utils.StringTools;
import xlink.mqtt.client.MqttComms;
import xlink.mqtt.client.token.MqttToken;
import xlink.mqtt.client.type.XlinkMqttTopicType;
import xlink.mqtt.client.utils.LogHelper;
import xlink.mqtt.client.utils.MqttUtils;



public class MqttMessageProcessor {

  private static final MqttMessageProcessor singleton = new MqttMessageProcessor();
  private Map<MqttMessageType, Processer> map = ContainerGetter.concurHashMap(7);

  private MqttMessageProcessor() {
    map.put(MqttMessageType.CONNACK, new ConnAckProcesser());
    map.put(MqttMessageType.PUBACK, new PubAckProcesser());
    map.put(MqttMessageType.PINGRESP, new PingRespProcesser());
    map.put(MqttMessageType.SUBACK, new SubAckProcesser());
    map.put(MqttMessageType.PUBLISH, new PublishProcesser());
  }

  public static MqttMessageProcessor instance() {
    return singleton;
  }

  public void process(MqttMessage msg, MqttToken token, MqttComms mqttComms) throws Exception {
    Processer processer = map.get(msg.fixedHeader().messageType());
    if (processer == null) {
      LogHelper.LOGGER().error("can not found processer. message type %s",
          msg.fixedHeader().messageType());
      return;
    }
    processer.proc(msg, token, mqttComms);
  }


  public interface Processer {
    public void proc(MqttMessage msg, MqttToken token, MqttComms mqttComms) throws Exception;
  }

  private class ConnAckProcesser implements Processer {

    @Override
    public void proc(MqttMessage msg, MqttToken token, MqttComms mqttComms) throws Exception {
      MqttConnAckMessage connAckMessage = (MqttConnAckMessage) msg;
      MqttConnectReturnCode connectReturnCode = connAckMessage.variableHeader().connectReturnCode();
      if (connectReturnCode == MqttConnectReturnCode.CONNECTION_ACCEPTED) {
        // 连接授权成功
        token.markComplete(true);
        mqttComms.changeToConnectedState();
        LogHelper.LOGGER().info("mqtt connection successfully, certId is {}, socket is {}, clientId is {}",
            mqttComms.clientId(), mqttComms.channelId(),mqttComms.mqttClientId());
        // 订阅激活主题
        mqttComms.subscribe(MqttUtils.getMessageId(),
            new String[] {"$xlink/device/activation/result"}, new int[] {1});
      } else {
        // TODO 连接授权失败
        LogHelper.LOGGER().info("connect failed,certId is {}, socket is {}, clientId is {} reson code is: {}", mqttComms.clientId(), mqttComms.channelId(),mqttComms.mqttClientId(), connectReturnCode);
        token.markComplete(false);
        mqttComms.changeConnStateNotConn();
      }

    }
  }

  private class PubAckProcesser implements Processer {

    @Override
    public void proc(MqttMessage msg, MqttToken token, MqttComms mqttComms) throws Exception {
      MqttPubAckMessage pukAckMsg = (MqttPubAckMessage) msg;
      LogHelper.LOGGER().info("pubAck messageId is: {}", pukAckMsg.variableHeader().messageId());
      if (token != null) {
        token.markComplete(true);
      }

    }
  }



  private class PingRespProcesser implements Processer {

    @Override
    public void proc(MqttMessage msg, MqttToken token, MqttComms mqttComms) throws Exception {

    }

  }



  private class SubAckProcesser implements Processer {

    @Override
    public void proc(MqttMessage msg, MqttToken token, MqttComms mqttComms) throws Exception {
      MqttSubAckMessage subAckMsg = (MqttSubAckMessage) msg;
      MqttSubscribeMessage subMsg = (MqttSubscribeMessage) token.getMessage();
      LogHelper.LOGGER().info("topic {} subscribe,  result is: {}",
          subMsg.payload().topicSubscriptions(),
          subAckMsg.payload().grantedQoSLevels());
      token.markComplete(true);
    }

  }

  private class UnsubAckProcesser implements Processer {

    @Override
    public void proc(MqttMessage msg, MqttToken token, MqttComms mqttComms) throws Exception {
      // TODO Auto-generated method stub

    }

  }

  private class PublishProcesser implements Processer {

    @Override
    public void proc(MqttMessage msg, MqttToken token, MqttComms mqttComms) throws Exception {

      MqttPublishMessage pubMsg = (MqttPublishMessage) msg;
      CMMessage cmMsg =
          CMMessage.funcParseMessage(XlinkPublishMessageFactory.version, pubMsg.payload());
      if (token != null) {
        token.setResponseMsg(cmMsg);
        token.markComplete(true);
      }

      String topic = pubMsg.variableHeader().topicName();
      LogHelper.LOGGER().debug("publish message, topic is {}", topic);
      XlinkMqttTopicType topicType =
          XlinkMqttTopicType.fromType(topic);
      switch (topicType) {
        case DEVICE_DATAPOINT_SET: {
          int deviceId = StringTools.getInt(topicType.getParmValue("device_id", topic));
          DatapointSetMessage dpSetMsg = (DatapointSetMessage) cmMsg;
          mqttComms.notifyDatapiontSetMessage(deviceId, dpSetMsg);
        }
          break;
        case SYSTEM_EVENT: {
          SysEventMessage sysEventMsg = (SysEventMessage) cmMsg;
          LogHelper.LOGGER().info("system event, type is {}", sysEventMsg.getEventType());
        }
          break;
        case DEVICE_STATE: {
          int deviceId = StringTools.getInt(topicType.getParmValue("device_id", topic));
          DeviceStateMessage deviceState = (DeviceStateMessage) cmMsg;
          LogHelper.LOGGER().info("device state, device id: {}, type is {}", deviceId,
              deviceState.getStateType());
          mqttComms.notifyDeviceStateMessage(deviceId, deviceState);
        }
        default:
          break;
      }

      if (pubMsg.fixedHeader().qosLevel() == MqttQoS.AT_LEAST_ONCE) {
        int messageId = pubMsg.variableHeader().messageId();
        MqttPubAckMessage pubAck = new MqttPubAckMessage(new MqttFixedHeader(MqttMessageType.PUBACK,
            false, pubMsg.fixedHeader().qosLevel(), false, 0),
            MqttMessageIdVariableHeader.from(messageId));
        mqttComms.sendDataDirectly(pubAck);
      }

      /*
       * XlinkMqttTopicType topicType =
       * XlinkMqttTopicType.fromType(pubMsg.variableHeader().topicName());
       */
      /*
       * switch (topicType) { case DEVICE_ACTIVE_RESULT: { ActivationResultMessage activationResult
       * = (ActivationResultMessage) cmMsg; DeviceInfoService.instance().insert(deviceKey);
       * 
       * } break; default: return; }
       */



    }

  }

}

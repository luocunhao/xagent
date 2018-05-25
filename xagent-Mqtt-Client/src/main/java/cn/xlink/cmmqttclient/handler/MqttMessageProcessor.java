package cn.xlink.cmmqttclient.handler;

import java.util.Arrays;
import java.util.Map;

import cn.xlink.cmmqttclient.client.MqttComms;
import cn.xlink.cmmqttclient.core.XlinkPublishMessageFactory;
import cn.xlink.cmmqttclient.core.utils.ContainerGetter;
import cn.xlink.cmmqttclient.core.utils.LogHelper;
import cn.xlink.cmmqttclient.core.utils.MqttUtils;
import cn.xlink.cmmqttclient.core.utils.StringTools;
import cn.xlink.cmmqttclient.token.MqttCmMessageToken;
import cn.xlink.cmmqttclient.token.MqttMessageToken;
import cn.xlink.cmmqttclient.token.MqttToken;
import cn.xlink.cmmqttclient.type.XlinkMqttTopicType;
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
import xlink.cm.message.CMMessage;
import xlink.cm.message.DatapointSetMessage;
import xlink.cm.message.DatapointSyncMessage;
import xlink.cm.message.DeviceStateMessage;
import xlink.cm.message.SysEventMessage;



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

  public void process(MqttMessage msg, MqttToken<?> token, MqttComms mqttComms) throws Exception {
    Processer processer = map.get(msg.fixedHeader().messageType());
    if (processer == null) {
      LogHelper.LOGGER().error("can not found processer. message type {}",
          msg.fixedHeader().messageType());
      return;
    }
    processer.proc(msg, token, mqttComms);
  }


  public interface Processer {
    public void proc(MqttMessage msg, MqttToken<?> token, MqttComms mqttComms) throws Exception;
  }

  private class ConnAckProcesser implements Processer {

    @Override
    public void proc(MqttMessage msg, MqttToken<?> token, MqttComms mqttComms) throws Exception {
      MqttConnAckMessage connAckMessage = (MqttConnAckMessage) msg;
      MqttConnectReturnCode connectReturnCode = connAckMessage.variableHeader().connectReturnCode();
      ((MqttMessageToken)token).markComplete(connAckMessage);
      if (connectReturnCode == MqttConnectReturnCode.CONNECTION_ACCEPTED) {
        // 连接授权成功  
        mqttComms.changeToConnectedState();
        LogHelper.LOGGER().info("mqtt connection successfully, cert id is {}, socket is {} ", mqttComms.certId(),
            mqttComms.channelId());
        mqttComms.notifyMqttConnectionSuccess();
        // 订阅激活主题
        mqttComms.subscribe(MqttUtils.getMessageId(),
            new String[] {"$xlink/device/activation/result"}, new int[] {1});
      } else {
        // TODO 连接授权失败
        LogHelper.LOGGER().info("mqtt connection failed,cert id is {}, reson code is: {}",mqttComms.certId(), connectReturnCode);
        mqttComms.changeConnStateNotConn();
      }

    }
  }

  private class PubAckProcesser implements Processer {

    @Override
    public void proc(MqttMessage msg, MqttToken<?> token, MqttComms mqttComms) throws Exception {
      MqttPubAckMessage pukAckMsg = (MqttPubAckMessage) msg;
      LogHelper.LOGGER().info("pubAck messageId is: {}", pukAckMsg.variableHeader().messageId());
      if (token != null) {
        token.markComplete(null);
      }

    }
  }



  private class PingRespProcesser implements Processer {

    @Override
    public void proc(MqttMessage msg, MqttToken<?> token, MqttComms mqttComms) throws Exception {

    }

  }



  private class SubAckProcesser implements Processer {

    @Override
    public void proc(MqttMessage msg, MqttToken<?> token, MqttComms mqttComms) throws Exception {
      MqttSubAckMessage subAckMsg = (MqttSubAckMessage) msg;
      LogHelper.LOGGER().info("topic {} subscribe,  result is: {}",
          Arrays.toString(token.getTopics()), subAckMsg.payload().grantedQoSLevels());
      if (token != null) {
        if (token instanceof MqttMessageToken) {
          ((MqttMessageToken) token).markComplete(subAckMsg);
        } else {
          LogHelper.LOGGER().warn("SubAckProcesser mqttToken is not MqttMessageToken, type is {}",
              token.getClass());
        }
      }
    }
  }

  private class UnsubAckProcesser implements Processer {

    @Override
    public void proc(MqttMessage msg, MqttToken<?> token, MqttComms mqttComms) throws Exception {
      // TODO Auto-generated method stub

    }

  }

  private class PublishProcesser implements Processer {

    @Override
    public void proc(MqttMessage msg, MqttToken<?> token, MqttComms mqttComms) throws Exception {

      MqttPublishMessage pubMsg = (MqttPublishMessage) msg;
      CMMessage cmMsg =
          CMMessage.funcParseMessage(XlinkPublishMessageFactory.version, pubMsg.payload());
      if (token != null) {
        if (token instanceof MqttCmMessageToken) {
          ((MqttCmMessageToken) token).markComplete(cmMsg);
        } else {
          LogHelper.LOGGER().warn("PublishProcesser mqttToken is not cmMessageToken, type is {}",
              token.getClass());
        }

      }

      String topic = pubMsg.variableHeader().topicName();
      LogHelper.LOGGER().debug("publish message, topic is {}", topic);
      XlinkMqttTopicType topicType = XlinkMqttTopicType.fromType(topic);
      switch (topicType) {
        case DEVICE_DATAPOINT_SET: {
          int deviceId = StringTools.getInt(topicType.getParmValue("device_id", topic));
          DatapointSetMessage dpSetMsg = (DatapointSetMessage) cmMsg;
          //mqttComms.notifyPublishMessageListener(deviceId, topicType, dpSetMsg);
          mqttComms.notifyDatapointSet(deviceId, dpSetMsg);
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
          //mqttComms.notifyPublishMessageListener(deviceId, topicType, deviceState);
          mqttComms.notifyDeviceState(deviceId, deviceState);
          break;
        }
        case DEVICE_DATAPOINT_SYNC: {
          int deviceId = StringTools.getInt(topicType.getParmValue("device_id", topic));
          DatapointSyncMessage datapointSync = (DatapointSyncMessage) cmMsg;
          LogHelper.LOGGER().info("device sync, device id: {}", deviceId);
         // mqttComms.notifyPublishMessageListener(deviceId, topicType, datapointSync);
          mqttComms.notifyMqttDatapointSync(deviceId, datapointSync);
          break;
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

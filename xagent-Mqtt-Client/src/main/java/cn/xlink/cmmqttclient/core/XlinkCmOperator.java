package cn.xlink.cmmqttclient.core;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import cn.xlink.cmmqttclient.Future.DatapointSetListener;
import cn.xlink.cmmqttclient.Future.DatapointSyncListener;
import cn.xlink.cmmqttclient.Future.DeviceStateListener;
import cn.xlink.cmmqttclient.Future.IMqttActionListener;
import cn.xlink.cmmqttclient.client.MqttClient;
import cn.xlink.cmmqttclient.client.MqttClientManager;
import cn.xlink.cmmqttclient.core.utils.ByteTools;
import cn.xlink.cmmqttclient.core.utils.LogHelper;
import cn.xlink.cmmqttclient.core.utils.MqttUtils;
import cn.xlink.cmmqttclient.core.utils.NoProguard;
import cn.xlink.cmmqttclient.core.utils.StringTools;
import cn.xlink.cmmqttclient.core.utils.tuple.TowTuple;
import cn.xlink.cmmqttclient.exception.XlinkMqttException;
import cn.xlink.cmmqttclient.queue.subclass.PtpDeviceOnlineAsyncQueue;
import cn.xlink.cmmqttclient.token.MqttCmMessageToken;
import cn.xlink.cmmqttclient.token.MqttMessageToken;
import cn.xlink.cmmqttclient.token.MqttToken;
import cn.xlink.cmmqttclient.type.XlinkMqttTopicType;
import io.netty.handler.codec.mqtt.MqttSubAckMessage;
import xlink.cm.message.ActivationResultMessage;
import xlink.cm.message.struct.ActivationResultStruct;
import xlink.cm.message.struct.Datapoint;

@NoProguard
public class XlinkCmOperator {

  public static MqttCmMessageToken deviceOnline(MqttClient mqttClient, final int deviceId,
      String ip)  {

    TowTuple<String, byte[]> tuple =
        XlinkPublishMessageFactory.createDeviceOnlinePayLoad(deviceId, ip);
    MqttCmMessageToken token =
        mqttClient.publish(MqttUtils.getMessageId(), tuple.first, tuple.second, 1);
    token.setCallbackListener(new IMqttActionListener() {
      @Override
      public void onSucess(MqttToken actionToken) {
        MqttClientManager.instance()
            .putAsyncQueue(new PtpDeviceOnlineAsyncQueue(deviceId, mqttClient));
      }

      @Override
      public void onFailure(MqttToken actionToken, XlinkMqttException exception) {
        // TODO Auto-generated method stub

      }

    });
    return token;
  }

  public static MqttCmMessageToken datapointSync(MqttClient mqttClient, int deviceId,
      Date updateTime, List<Datapoint> datapoints) throws Exception {
    TowTuple<String, byte[]> tuple =
        XlinkPublishMessageFactory.createDatapointSync(deviceId, datapoints, updateTime);
    if (updateTime != null) {
      LogHelper.LOGGER().info("[datapointSync] timestamp {}, deviceId {}, payload: {}",
          updateTime.getTime(), deviceId, ByteTools.printBytes2HexString(tuple.second));
    } else {
      LogHelper.LOGGER().info("[datapointSync] no timestamp, deviceId {}, payload: {}", deviceId,
          ByteTools.printBytes2HexString(tuple.second));
    }
    MqttCmMessageToken token = mqttClient.publishSequence(MqttUtils.getMessageId(), tuple.first,
        tuple.second, 1, deviceId);
    return token;
  }

  public static MqttCmMessageToken datapointSync(MqttClient mqttClient, int deviceId,byte[] payload) throws Exception {
    String topic = XlinkPublishMessageFactory.createDatapointSyncTopic(deviceId);
      LogHelper.LOGGER().info("[datapointSync] no timestamp, deviceId {}, payload: {}", deviceId,
          ByteTools.printBytes2HexString(payload));
    MqttCmMessageToken token = mqttClient.publishSequence(MqttUtils.getMessageId(), topic,payload, 1, deviceId);
    return token;
  }

  public static MqttCmMessageToken deviceOffline(MqttClient mqttClient, int deviceId)
      throws Exception {
    TowTuple<String, byte[]> tuple =
        XlinkPublishMessageFactory.createDeviceOfflinePayLoad(deviceId);
    MqttCmMessageToken token =
        mqttClient.publish(MqttUtils.getMessageId(), tuple.first, tuple.second, 1);
    return token;
  }


  public static MqttCmMessageToken replyDatapointSet(MqttClient mqttClient, int deviceId,
      int messageId, int result) throws Exception {
    TowTuple<String, byte[]> tuple =
        XlinkPublishMessageFactory.createDatapointSetResult(deviceId, messageId, result);
    MqttCmMessageToken token =
        mqttClient.publish(MqttUtils.getMessageId(), tuple.first, tuple.second, 0);
    return token;

  }

  /**
   * 监听设备状态
   *
   */
  public static boolean subscribeDeviceState(MqttClient mqttClient, final int deviceId) {
    final String topic = XlinkMqttTopicType.DEVICE_STATE.shortTopic().replaceAll("\\{device_id\\}",
        StringTools.getString(deviceId));
    MqttMessageToken mqttToken =
        mqttClient.subscribe(MqttUtils.getMessageId(), new String[] {topic}, new int[] {0});
    try {
      MqttSubAckMessage ack = (MqttSubAckMessage) mqttToken.get(5, TimeUnit.SECONDS);
      return ack.payload().grantedQoSLevels().get(0) != 0x80;
    } catch (Exception e) {
      LogHelper.LOGGER().error("subscribeDeviceState " + deviceId + " failed", e);
      return false;
    }

  }

  /**
   * 获取数据端点变化
   * 
   * @param mqttClient
   * @param deviceId
   * @param listener
   * @throws Exception
   */
  public static MqttMessageToken subscribeDatapointSync(MqttClient mqttClient, final int deviceId) {
    final String topic = XlinkMqttTopicType.DEVICE_DATAPOINT_SYNC.shortTopic()
        .replaceAll("\\{device_id\\}", StringTools.getString(deviceId));
    MqttMessageToken mqttToken =
        mqttClient.subscribe(MqttUtils.getMessageId(), new String[] {topic}, new int[] {0});
    return mqttToken;
  }

  /**
   * 获取数据端点的控制指令
   * 
   * @param mqttClient
   * @param deviceId
   * @param listener
   * @throws Exception
   */
  public static MqttMessageToken subscribeDatapointSet(MqttClient mqttClient, final int deviceId) {
    final String topic = XlinkMqttTopicType.DEVICE_DATAPOINT_SET.shortTopic()
        .replaceAll("\\{device_id\\}", StringTools.getString(deviceId));
    MqttMessageToken mqttToken =
        mqttClient.subscribe(MqttUtils.getMessageId(), new String[] {topic}, new int[] {0});
    mqttToken.setCallbackListener(new IMqttActionListener() {
      @Override
      public void onSucess(MqttToken<?> actionToken) {
        LogHelper.LOGGER().info("subscribe topic {} success.add to listener", topic);
      }

      @Override
      public void onFailure(MqttToken<?> actionToken, XlinkMqttException exception) {
        LogHelper.LOGGER().info("subscribe topic {} failed.", topic);
      }

    });
    return mqttToken;
  }

  /**
   * 向XLINK平台激活设备
   * 
   * @param productId xlink的产品ID
   * @param identify 设备的识别号，厂商自定义
   * @param wifiFirmware wifi mode，可选(不需要时设置为null)
   * @param wifiVersion wifi固件版本号，可选(不需要时设置为0)
   * @param mcuVersion mcu mode，可选(不需要时设置为null)
   * @param mcuVersion mcu固件版本号，可选(不需要时设置为0)
   * @param sn 设备序列号，可选(不需要时设置为null)
   * @param gatewayDeviceId 网关ID，可选(不需要时设置为0)
   * @param activeIp 激活时设备的IP地址，可选(不需要时设置为null)
   * @return
   */

  public static MqttCmMessageToken activateDevice(MqttClient mqttClient, final String productId,
      final String identify, byte wifiFirmware, int wifiVersion, byte mcuFirmware, int mcuVersion,
      String sn, int gatewayDeviceId, String activeIp) throws Exception {
    TowTuple<String, byte[]> tuple =
        XlinkPublishMessageFactory.createDeviceActivePayload(productId, identify, wifiFirmware,
            wifiVersion, mcuFirmware, mcuVersion, sn, gatewayDeviceId, activeIp);
    MqttCmMessageToken  token =  mqttClient.publish(MqttUtils.getMessageId(), tuple.first, tuple.second, 1);
    token.setCallbackListener(new  IMqttActionListener() {
      @Override
      public void onSucess(MqttToken<?> actionToken) {
        ActivationResultMessage result;
        try {
          result = (ActivationResultMessage) ((MqttCmMessageToken)actionToken).get();
          ActivationResultStruct activeStruct = result.getActivationResultList().get(0);
          LogHelper.LOGGER().info("active device {} result {}", identify,activeStruct.getCode());
        } catch (Throwable e) {
          LogHelper.LOGGER().error("", e);
        }
       
      }

      @Override
      public void onFailure(MqttToken<?> actionToken, XlinkMqttException exception) {
        LogHelper.LOGGER().info("active device {} failed.",identify );
      }

    });
    return token;
  }
}

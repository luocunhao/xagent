package cn.xlink.cmmqttclient.core;

import java.util.Date;
import java.util.List;

import cn.xlink.cmmqttclient.core.utils.ContainerGetter;
import cn.xlink.cmmqttclient.core.utils.StringTools;
import cn.xlink.cmmqttclient.core.utils.tuple.TowTuple;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import xlink.cm.message.ActivationMessage;
import xlink.cm.message.DatapointSetResultMessage;
import xlink.cm.message.DatapointSyncMessage;
import xlink.cm.message.DeviceOfflineMessage;
import xlink.cm.message.DeviceOnlineMessage;
import xlink.cm.message.struct.ActivationStruct;
import xlink.cm.message.struct.Datapoint;


public class XlinkPublishMessageFactory {

  public static int version = 0;

  private static String ACTIVE_TOPIC = "$xlink/device/activation";
  private static String ONLINE_TOPIC = "$3/{device_id}";
  private static String OFFLINE_TOPIC = "$5/{device_id}";
  private static String DATAPOINT_SYNC = "$6/{device_id}";
  private static String DATAPOINT_SET_RESULT = "$8/{device_id}";

  /**
   * 创建设备激活的MQTT publish payload数据和主题
   */
  public static TowTuple<String, byte[]> createDeviceActivePayload(String productId, String mac,
      byte wifiFirmware, int wifiVersion, byte mcuFirmware, int mcuVersion, String sn,
      int gatewayDeviceId, String activeIp) {
    boolean isWifiFirmWare = wifiFirmware > 0;

    boolean isWifiVersion = wifiVersion > 0;
    boolean isMcuFirmware = mcuFirmware > 0;
    boolean isMcuVersion = mcuVersion > 0;
    boolean isSn = sn != null;
    boolean isGatewayDeviceId = gatewayDeviceId > 0;
    byte[] ipBytes = StringTools.getbytesFromIpString(activeIp);
    boolean isHasActiveIp = ipBytes != null;

    ActivationMessage activationMessage = new ActivationMessage();
    List<ActivationStruct> activationStructs = ContainerGetter.arrayList();
    activationStructs.add(new ActivationStruct(productId, mac, isWifiFirmWare, isWifiVersion,
        isMcuFirmware, isMcuVersion, isSn, isGatewayDeviceId,
        wifiFirmware, wifiVersion, mcuFirmware, mcuVersion, sn, gatewayDeviceId, false, (byte) 0,
        isHasActiveIp, ipBytes));
    activationMessage.setActivationMessageList(activationStructs);

    return new TowTuple<String, byte[]>(ACTIVE_TOPIC, activationMessage.toPayload(version));
  }

  /**
   * 创建设备上线的MQTT publish payload数据和主题
   */
  public static TowTuple<String, byte[]> createDeviceOnlinePayLoad(int deviceId, String ip) {
    DeviceOnlineMessage deviceOnlineMessage = new DeviceOnlineMessage();

    byte[] ipBytes = StringTools.getbytesFromIpString(ip);
    boolean hasIp = (ipBytes != null);
    deviceOnlineMessage.setHasOnlineIp(hasIp);
    if (hasIp) {
      deviceOnlineMessage.setOnlineIp(ipBytes);
    }

    String topic = ONLINE_TOPIC.replaceAll("\\{device_id\\}", StringTools.getString(deviceId));
    return new TowTuple<String, byte[]>(topic, deviceOnlineMessage.toPayload(version));
  }

  /**
   * 创建设备下线的MQTT publish payload数据和主题
   */
  public static TowTuple<String,byte[]> createDeviceOfflinePayLoad(int deviceId){
    DeviceOfflineMessage deviceOfflineMessage = new DeviceOfflineMessage();
    String topic = OFFLINE_TOPIC.replaceAll("\\{device_id\\}", StringTools.getString(deviceId));
    return new TowTuple<String, byte[]>(topic, deviceOfflineMessage.toPayload(version));
  }

  /**
   * 创建设备数据端点同步的MQTT publish payload数据和主题
   */
  public static TowTuple<String, byte[]> createDatapointSync(int deviceId,
      List<Datapoint> datapoints,Date syncTime) throws Exception {
    
    String topic = DATAPOINT_SYNC.replaceAll("\\{device_id\\}", StringTools.getString(deviceId));
    
    DatapointSyncMessage datapointSyncMessage = new DatapointSyncMessage();
    int isHasDatapointData = (datapoints != null && datapoints.size() > 0) ? 1 : 0;
    int isHasTimeStamp = syncTime == null?0:1;
    ByteBuf buf = Unpooled.buffer();
    byte flag = 0;
    flag |= isHasDatapointData<<6;
    flag |= 0x20;
    flag |= isHasTimeStamp<<3;
    buf.writeByte(flag);
    if(isHasTimeStamp>0){
      buf.writeLong(syncTime.getTime());
    }
    buf.writeBytes(datapointSyncMessage.encode(datapoints));
    datapointSyncMessage.setDatapoint(buf);
   return new TowTuple<String,byte[]>(topic, datapointSyncMessage.toPayload(version));
  }

  /**
   * 数据端点设置结果
   * 
   * @param deviceId
   * @param resultCode 设置结果
   * @param messageId 数据端点设置的消息id
   * @return
   * @throws Exception
   */
  public static TowTuple<String, byte[]> createDatapointSetResult(int deviceId, int messageId,
      int resultCode) throws Exception {
    DatapointSetResultMessage resultMsg = new DatapointSetResultMessage();
    resultMsg.setCode(resultCode);
    resultMsg.setMessageId(messageId);
    String topic =
        DATAPOINT_SET_RESULT.replaceAll("\\{device_id\\}", StringTools.getString(deviceId));
    return new TowTuple<String, byte[]>(topic, resultMsg.toPayload(version));
  }
  
  public static String createDatapointSyncTopic(int deviceId) {
    return DATAPOINT_SYNC.replaceAll("\\{device_id\\}", StringTools.getString(deviceId));
  }
}

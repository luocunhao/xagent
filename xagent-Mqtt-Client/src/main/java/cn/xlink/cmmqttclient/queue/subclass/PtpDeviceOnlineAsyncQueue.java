package cn.xlink.cmmqttclient.queue.subclass;

import cn.xlink.cmmqttclient.client.MqttClient;
import cn.xlink.cmmqttclient.core.utils.LogHelper;
import cn.xlink.cmmqttclient.core.utils.MqttUtils;
import cn.xlink.cmmqttclient.queue.AsyncQueue;
import cn.xlink.cmmqttclient.queue.QueueType;
import cn.xlink.cmmqttclient.type.XlinkMqttTopicType;

public class PtpDeviceOnlineAsyncQueue extends AsyncQueue {

  @Override
  public QueueType getQueueType() {
    return QueueType.ASYNC_PTP_DEVICE_ONLINE;
  }

  private int deviceId;

  private MqttClient client;

  public PtpDeviceOnlineAsyncQueue(int deviceId, MqttClient client) {
    this.deviceId = deviceId;
    this.client = client;
  }

  public int getDeviceId() {
    return deviceId;
  }

  public MqttClient getClient() {
    return client;
  }

  @Override
  public void run() {
    LogHelper.LOGGER().info("subscribe datapoint topic for {}", deviceId);
    String topic = XlinkMqttTopicType.DEVICE_DATAPOINT_SET.shortTopic()
        .replaceAll("\\{device_id\\}", Integer.toString(deviceId));
    client.subscribe(MqttUtils.getMessageId(), new String[] {topic},
        new int[] {1});
    
  }



}

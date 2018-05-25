package xlink.mqtt.client.queue.subclass;

import xlink.cm.message.DatapointSetMessage;
import xlink.mqtt.client.Future.DatapointSetListener;
import xlink.mqtt.client.queue.AsyncQueue;
import xlink.mqtt.client.queue.QueueType;

public class MqttDatapointSetNotifyQueue extends AsyncQueue{

  private DatapointSetListener listener;
  private int deviceId;
  private DatapointSetMessage dpSetMsg;
  @Override
  public QueueType getQueueType() {
    // TODO Auto-generated method stub
    return QueueType.ASYNC_MQTT_DATAPPINTSET_NOTIFY;
  }
  public DatapointSetListener getListener() {
    return listener;
  }
  public void setListener(DatapointSetListener listener) {
    this.listener = listener;
  }
  public int getDeviceId() {
    return deviceId;
  }
  public void setDeviceId(int deviceId) {
    this.deviceId = deviceId;
  }
  public DatapointSetMessage getDpSetMsg() {
    return dpSetMsg;
  }
  public void setDpSetMsg(DatapointSetMessage dpSetMsg) {
    this.dpSetMsg = dpSetMsg;
  }
  public MqttDatapointSetNotifyQueue(DatapointSetListener listener, int deviceId,
      DatapointSetMessage dpSetMsg) {
    super();
    this.listener = listener;
    this.deviceId = deviceId;
    this.dpSetMsg = dpSetMsg;
  }
  
  
  
}

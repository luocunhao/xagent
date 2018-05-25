package xlink.mqtt.client.thread;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import xlink.core.derby.dataStructure.DeviceInfo;
import xlink.core.derby.service.DeviceInfoService;
import xlink.mqtt.client.queue.AsyncQueue;
import xlink.mqtt.client.queue.DelayQueue;
import xlink.mqtt.client.queue.subclass.MqttConnAckAsyncQueue;
import xlink.mqtt.client.queue.subclass.MqttDatapointSetNotifyQueue;
import xlink.mqtt.client.queue.subclass.MqttSendAsyncQueue;
import xlink.mqtt.client.queue.subclass.PtpDeviceOfflineAsyncQueue;
import xlink.mqtt.client.queue.subclass.PtpDeviceOnlineAsyncQueue;
import xlink.mqtt.client.type.XlinkMqttTopicType;
import xlink.mqtt.client.utils.LogHelper;
import xlink.mqtt.client.utils.MqttUtils;


public class AsyncThread extends Thread{
	private final Logger logger = LoggerFactory.getLogger(AsyncThread.class);
	private final ConcurrentLinkedQueue<AsyncQueue> queue;
	private final LogicThreadPool logicThreadPool;
	private final List<DelayQueue> delayQueue;
	public AsyncThread(int index, ConcurrentLinkedQueue<AsyncQueue> queue, LogicThreadPool logicThreadPool, List<DelayQueue> delayQueue) {
		super();
		setName("Async-Thread-" + index);
		this.queue = queue;
		this.logicThreadPool = logicThreadPool;
		this.delayQueue = delayQueue;
	}
	
	@Override
	public void run() {
		long start = 0;
		
		
		for (;;) {
			try {
				while (queue.peek() != null) {
					try {
						AsyncQueue asyncQueue = queue.poll();
						if (asyncQueue == null) {
							continue;
						}
						start = System.currentTimeMillis();
						processAsyncQueue(asyncQueue);
						long cost = System.currentTimeMillis() - start;
						if (cost > 200) {
							logger.warn(String.format("Process Async Queue [%s] cost %s ms.", asyncQueue.getQueueType(), cost));
						}
					} catch (Throwable e) {
						logger.error("AsyncThread.run.while:", e);
					}
				}
				
				sleep(10);
				
			} catch (Throwable e) {
				logger.error("AsyncThread.run:", e);
			}
		}
	}

	private void processAsyncQueue(AsyncQueue asyncQueue) throws Exception {
		switch (asyncQueue.getQueueType()) {
      case ASYNC_MQTT_SEND:
        funcHandlerMqttSend((MqttSendAsyncQueue) asyncQueue);
        break;
      case ASYNC_PTP_DEVICE_ONLINE:
        funcHandlerPtpDeviceOnline((PtpDeviceOnlineAsyncQueue) asyncQueue);
        break;
      case ASYNC_PTP_DEVICE_OFFLINE:
        funcHandlerPtpDeviceOffline((PtpDeviceOfflineAsyncQueue) asyncQueue);
        break;
      case ASYNC_MQTT_CONNACK:
        funcHandlerMqttConnAck((MqttConnAckAsyncQueue) asyncQueue);
        break;
      case ASYNC_MQTT_DATAPPINTSET_NOTIFY:
        funcHandlerDatapointSetNotify((MqttDatapointSetNotifyQueue) asyncQueue);
        break;
        default:
          LogHelper.LOGGER().warn("AsyncThread processAsyncQueue unkown AsyncQueueType {}", asyncQueue.getQueueType());
          break;
		}
	}
	
  /**
   * mqtt客户端异步发送消息
   * 
   * @param asyncMqttSendQueue
   * @throws InterruptedException
   */
  private void funcHandlerMqttSend(MqttSendAsyncQueue asyncMqttSendQueue)
      throws InterruptedException {
    // asyncMqttSendQueue.getMqttComms().send(asyncMqttSendQueue.getMsg());
    MqttMessage message = asyncMqttSendQueue.getMsg();

    // 如果Mqtt处于非连接状态，把消息放进缓冲队列。
    if (message.fixedHeader().messageType() != MqttMessageType.CONNECT
        && asyncMqttSendQueue.getMqttComms().isConnected() == false) {
      queue.offer(asyncMqttSendQueue);
      return;
    }
    asyncMqttSendQueue.getMqttComms().sendDataDirectly(asyncMqttSendQueue.getMsg());
    // 消息发送完成,记数减一
    asyncMqttSendQueue.getMqttComms().messageCountDecre(1);
  }


  private void funcHandlerPtpDeviceOnline(PtpDeviceOnlineAsyncQueue devicOnlineQueue)
      throws InterruptedException {
    DeviceInfoService.instance().updateDeviceState(devicOnlineQueue.getClient().getPtpId(),
        devicOnlineQueue.getDeviceId(), true, new Date(System.currentTimeMillis()));
    LogHelper.LOGGER().info("subscribe datapoint topic for {}", devicOnlineQueue.getDeviceId());
    String topic = XlinkMqttTopicType.DEVICE_DATAPOINT_SET.shortTopic()
        .replaceAll("\\{device_id\\}", Integer.toString(devicOnlineQueue.getDeviceId()));
    try {
      devicOnlineQueue.getClient().subscribe(MqttUtils.getMessageId(), new String[] {topic},
          new int[] {1});
    } catch (InterruptedException e) {
      LogHelper.LOGGER().info("subscribe failed, {}", e.getMessage());
    }
  }

  private void funcHandlerPtpDeviceOffline(PtpDeviceOfflineAsyncQueue devicOnlineQueue)
      throws InterruptedException {
    DeviceInfoService.instance().updateDeviceState(devicOnlineQueue.getClient().getPtpId(),
        devicOnlineQueue.getDeviceId(), false, new Date(System.currentTimeMillis()));

  }

  private void funcHandlerMqttConnAck(MqttConnAckAsyncQueue connAck) {
    String ptpId = connAck.getClient().getPtpId();
    int limit = 1000;
    int offset = 0;
    int[] qos = new int[20];
    Arrays.fill(qos, 1);
    List<DeviceInfo> deviceList = DeviceInfoService.instance().findAllOnline(ptpId, offset, limit);
    while (deviceList.size() > 0) {

      for (int i = 0; i < 20; i = i + 20) {

        String[] topics = deviceList.stream().skip(i).limit(20).map(deviceInfo -> {
          return XlinkMqttTopicType.DEVICE_DATAPOINT_SET.shortTopic().replaceAll("\\{device_id\\}",
              Integer.toString(deviceInfo.getDeviceId()));
        }).toArray(String[]::new);
        try {
          if (topics.length != 20) {
            qos = new int[topics.length];
            Arrays.fill(qos, 1);
          }
          connAck.getClient().subscribe(MqttUtils.getMessageId(), topics, qos);
        } catch (InterruptedException e) {
          LogHelper.LOGGER().info("subscribe failed, {}", e.getMessage());
        }

      }
      offset += limit;
      deviceList = DeviceInfoService.instance().findAllOnline(ptpId, offset, limit);

    }

  }

  private void funcHandlerDatapointSetNotify(MqttDatapointSetNotifyQueue dpSetNotify) {
    dpSetNotify.getListener().messageNotify(dpSetNotify.getDeviceId(), dpSetNotify.getDpSetMsg());
  }
}

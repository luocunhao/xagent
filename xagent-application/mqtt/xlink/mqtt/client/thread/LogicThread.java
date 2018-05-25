package xlink.mqtt.client.thread;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttSubAckMessage;
import io.netty.handler.codec.mqtt.MqttUnsubAckMessage;
import xlink.cm.ptp.queue.PtpChannelMessageLogicQUeue;
import xlink.cm.ptp.queue.PtpClientChannelMessageLogicQUeue;
import xlink.core.utils.StringTools;
import xlink.mqtt.client.MqttComms;
import xlink.mqtt.client.message.MqttMessageProcessor;
import xlink.mqtt.client.queue.LogicQueue;
import xlink.mqtt.client.queue.subclass.MqttReceiveLogicQueue;
import xlink.mqtt.client.queue.subclass.MqttSendLogicQueue;
import xlink.mqtt.client.token.MqttToken;
import xlink.mqtt.client.token.MqttTokenManager;
import xlink.mqtt.client.type.XlinkMqttTopicType;



public class LogicThread extends Thread {
	private final Logger logger = LoggerFactory.getLogger(LogicThread.class);
	private final ConcurrentLinkedQueue<LogicQueue> logicQueues;

	public LogicThread(int index, ConcurrentLinkedQueue<LogicQueue> logicQueues) {
		super();
		setName("Logic-Thread-" + index);
		this.logicQueues = logicQueues;
	}

	@Override
	public void run() {
		long start = 0;
		for (;;) {
			try {
				while (logicQueues.peek() != null) {
					try {
						LogicQueue logicQueue = logicQueues.poll();
						start = System.currentTimeMillis();
						processLogicQueue(logicQueue);
						long cost = System.currentTimeMillis() - start;
						if (cost > 200) {
							logger.warn(String.format("Process Logic Queue [%s] cost %s ms.", logicQueue.getQueueType() ,  cost));
						}
					} catch (Throwable e) {
						logger.error("LogicThread.run.sleep:", e);
					}
				}
				Thread.sleep(10);
			} catch (Throwable e) {
				logger.error("LogicThread.run:", e);
			}
			
		}
	}

	private void processLogicQueue(LogicQueue logicQueue) throws Exception {
		switch (logicQueue.getQueueType()) {
      case LOGIC_MQTT_RECEIVE:
        funcHandleMqttMessageArriveQueue((MqttReceiveLogicQueue) logicQueue);
        break;
      case LOGIC_PTP_CHANNEL_MESSAGE:
        funcHandlePtpChannelMessage((PtpChannelMessageLogicQUeue) logicQueue);
        break;
      case LOGIC_MQTT_SEND:
        funcHandlerMqttSend((MqttSendLogicQueue) logicQueue);
        break;
      case LOGIC_PTP_CLIENT_CHANNEL_MESSAGE:
        funcHandlePtpClientChannelMessage((PtpClientChannelMessageLogicQUeue) logicQueue);
        break;
      default:
    }
  }

  private void funcHandleMqttMessageArriveQueue(MqttReceiveLogicQueue mqttReceiveLogicQueue)
      throws Exception {
    MqttComms mqttComms = mqttReceiveLogicQueue.getMqttComms();
    MqttMessage msg = mqttReceiveLogicQueue.getMqttMessage();


    String messageId = null;
    switch (msg.fixedHeader().messageType()) {
      case CONNACK:
        messageId = mqttComms.mqttClient().clientId();
        break;
      case PUBACK:
        messageId = StringTools.getString(((MqttPubAckMessage) msg).variableHeader().messageId());
        break;
      case PUBLISH: {
        MqttPublishMessage pubMsg = (MqttPublishMessage) msg;
        String topic = pubMsg.variableHeader().topicName();
        XlinkMqttTopicType topicType = XlinkMqttTopicType.fromType(topic);
        messageId = topicType.getTokenId(mqttComms.clientId(), pubMsg);
      }
        break;
      case SUBACK:
        messageId = StringTools.getString(((MqttSubAckMessage) msg).variableHeader().messageId());
        break;
      case UNSUBACK:
        messageId = StringTools.getString(((MqttUnsubAckMessage) msg).variableHeader().messageId());
        break;
      case PINGRESP:
        return;
      default:
        break;
    }
    MqttToken token = MqttTokenManager.instance().remove(messageId);
    MqttMessageProcessor.instance().process(msg, token, mqttComms);
    // 消息处理完成，计数减一
    mqttComms.messageCountDecre(1);
	}

  private void funcHandlePtpChannelMessage(PtpChannelMessageLogicQUeue PtpChannelMsgqueue) {
    PtpChannelMsgqueue.getPtpServer().getProcessor().process(PtpChannelMsgqueue.getPtpServer(),
        PtpChannelMsgqueue.getHashcode(), PtpChannelMsgqueue.getMessage());
  }
  
  private void funcHandlePtpClientChannelMessage(PtpClientChannelMessageLogicQUeue PtpChannelMsgqueue) {
    PtpChannelMsgqueue.getPtpClient().getProcessor().process(PtpChannelMsgqueue.getPtpClient(), PtpChannelMsgqueue.getMessage());
  }

  /**
   * mqtt客户端异步发送消息
   * 
   * @param asyncMqttSendQueue
   * @throws InterruptedException
   */
  private void funcHandlerMqttSend(MqttSendLogicQueue logicMqttSendQueue) {
    MqttMessage message = logicMqttSendQueue.getMsg();
    // 如果Mqtt处于非连接状态，把消息放进缓冲队列。
    if (message.fixedHeader().messageType() != MqttMessageType.CONNECT
        && logicMqttSendQueue.getMqttComms().isConnected() == false) {
      logicQueues.offer(logicMqttSendQueue);
      return;
    }
    logicMqttSendQueue.getMqttComms().sendDataDirectly(logicMqttSendQueue.getMsg());
    // 消息发送完成,记数减一
    logicMqttSendQueue.getMqttComms().messageCountDecre(1);
  }

}

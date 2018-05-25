package cn.xlink.cmmqttclient.thread;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.xlink.cmmqttclient.client.MqttComms;
import cn.xlink.cmmqttclient.core.utils.StringTools;
import cn.xlink.cmmqttclient.handler.MqttMessageProcessor;
import cn.xlink.cmmqttclient.queue.LogicQueue;
import cn.xlink.cmmqttclient.queue.subclass.MqttReceiveLogicQueue;
import cn.xlink.cmmqttclient.queue.subclass.MqttSendLogicQueue;
import cn.xlink.cmmqttclient.token.MqttToken;
import cn.xlink.cmmqttclient.token.MqttTokenManager;
import cn.xlink.cmmqttclient.type.XlinkMqttTopicType;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttSubAckMessage;
import io.netty.handler.codec.mqtt.MqttUnsubAckMessage;



public class LogicThread extends Thread {
	private final Logger logger = LoggerFactory.getLogger(LogicThread.class);
	private final ConcurrentLinkedQueue<Runnable> logicQueues;

	public LogicThread(int index, ConcurrentLinkedQueue<Runnable> logicQueues) {
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
						Runnable logicQueue = logicQueues.poll();
						start = System.currentTimeMillis();
						//processLogicQueue(logicQueue);
						logicQueue.run();
						long cost = System.currentTimeMillis() - start;
						if (cost > 200) {
							logger.warn("Process Logic Queue {} cost {} ms.",  cost);
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
}

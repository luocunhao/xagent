package xlink.mqtt.client.thread;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xlink.mqtt.client.queue.LogicQueue;

public class LogicThreadPool {
	private static final Logger logger = LoggerFactory.getLogger(LogicThreadPool.class);

	private final LogicThread[] logicThreadList;
	private final ConcurrentLinkedQueue<LogicQueue>[] queueList;
	private final int size;
	private final Random random = new Random();
	
	@SuppressWarnings("unchecked")
	public LogicThreadPool(int threadSize, int queueSize){
		this.size = threadSize;
		logicThreadList = new LogicThread[threadSize];
		queueList = new ConcurrentLinkedQueue[threadSize];
		for (int i = 0; i < threadSize; i++) {
			queueList[i] = new ConcurrentLinkedQueue<LogicQueue>();
			logicThreadList[i] = new LogicThread(i, queueList[i]);
			logicThreadList[i].start();
		}
	}
	
	/**
	 * 根据socket Hash求模
	 * @param socketHash
	 * @param logicQueue
	 * @throws InterruptedException
	 */
	public void offer(int socketHash, LogicQueue logicQueue) throws InterruptedException{
		queueList[socketHash % size].offer(logicQueue);
	}
	
	/**
	 * 随机
	 * @param logicQueue
	 * @throws InterruptedException 
	 */
	public void offer(LogicQueue logicQueue) throws InterruptedException{
		queueList[random.nextInt(size)].offer(logicQueue);
	}
	
	public int queueSize(){
		int size = 0;
		for (ConcurrentLinkedQueue<LogicQueue> queue : queueList) {
			size += queue.size();
		}
		return size;
	}
	
	
	public void print(){
		for (int i = 0; i < queueList.length; i++) {
			ConcurrentLinkedQueue<LogicQueue> queue = queueList[i];
			if (queue.size() > 0) {
				logger.warn("Logic Queue-" + i + ", Size " + queue.size());
			}
		}
	}
	
}

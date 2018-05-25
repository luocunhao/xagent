package xlink.mqtt.client.thread;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xlink.core.utils.ContainerGetter;
import xlink.mqtt.client.queue.AsyncQueue;
import xlink.mqtt.client.queue.DelayQueue;



public class AsyncThreadPool {
	private static final Logger logger = LoggerFactory.getLogger(AsyncThreadPool.class);
	private final AsyncThread[] asyncThreadList;
	private final ConcurrentLinkedQueue<AsyncQueue> queue;
	public AsyncThreadPool(int threadSize, int queueSize, LogicThreadPool logicThreadPool, List<DelayQueue> delayQueue){
		asyncThreadList = new AsyncThread[threadSize];
		queue = ContainerGetter.concurLinkedQueue();
		for (int i = 0; i < threadSize; i++) {
			asyncThreadList[i] = new AsyncThread(i, queue, logicThreadPool, delayQueue);
			asyncThreadList[i].start();
		}
		
	}
	
	public void offer(AsyncQueue asyncQueue) throws InterruptedException{
		queue.offer(asyncQueue);
	}
	
	public int queueSize(){
		return queue.size();
	}
	
	public void print(){
		if (queue.size() > 0) {
			logger.warn("Async Queue Size: " + queueSize());
		}
	}
}

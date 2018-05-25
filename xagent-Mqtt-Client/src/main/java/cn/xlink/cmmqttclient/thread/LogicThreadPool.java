package cn.xlink.cmmqttclient.thread;

import java.util.List;
import java.util.Random;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.xlink.cmmqttclient.queue.LogicQueue;

public class LogicThreadPool extends AbstractExecutorService{
	private static final Logger logger = LoggerFactory.getLogger(LogicThreadPool.class);

	private final LogicThread[] logicThreadList;
	private final ConcurrentLinkedQueue<Runnable>[] queueList;
	private final int size;
	private final Random random = new Random();
	
	@SuppressWarnings("unchecked")
	public LogicThreadPool(int threadSize, int queueSize){
		this.size = threadSize;
		logicThreadList = new LogicThread[threadSize];
		queueList = new ConcurrentLinkedQueue[threadSize];
		for (int i = 0; i < threadSize; i++) {
			queueList[i] = new ConcurrentLinkedQueue<Runnable>();
			logicThreadList[i] = new LogicThread(i, queueList[i]);
			logicThreadList[i].start();
		}
	}
	

	
	public int queueSize(){
		int size = 0;
		for (ConcurrentLinkedQueue<Runnable> queue : queueList) {
			size += queue.size();
		}
		return size;
	}
	
	
	public void print(){
		for (int i = 0; i < queueList.length; i++) {
			ConcurrentLinkedQueue<Runnable> queue = queueList[i];
			if (queue.size() > 0) {
				logger.warn("Logic Queue-" + i + ", Size " + queue.size());
			}
		}
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Runnable> shutdownNow() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isShutdown() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isTerminated() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void execute(Runnable command) {
		// TODO Auto-generated method stub
		if(command==null){
	    	throw new NullPointerException();	
	    }
		if(command instanceof LogicQueue) {
		  LogicQueue logicRunnable = (LogicQueue)command;
		  if(logicRunnable.getSequenceId()>0) {
		    queueList[logicRunnable.getSequenceId() % size].offer(command);
		    return;
		  }
		}
		queueList[random.nextInt(size)].offer(command);
	}
	/**
	 * 根据socket Hash求模
	 * @param socketHash
	 * @param logicQueue
	 * @throws InterruptedException
	 */
	public void executeByOrder(int socketHash,Runnable command) {
		// TODO Auto-generated method stub
		if(command==null){
	    	throw new NullPointerException();	
	    }
		queueList[socketHash % size].offer(command);
	}
	
}

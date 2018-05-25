package cn.xlink.cmmqttclient.thread;


import java.util.List;
import java.util.concurrent.AbstractExecutorService;

import java.util.concurrent.ConcurrentLinkedQueue;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.xlink.cmmqttclient.core.utils.ContainerGetter;
import cn.xlink.cmmqttclient.queue.DelayQueue;



public class AsyncThreadPool extends AbstractExecutorService{
  
	private static final Logger logger = LoggerFactory.getLogger(AsyncThreadPool.class);
	private volatile boolean interrupted;
	private final AsyncThread[] asyncThreadList;
	private final ConcurrentLinkedQueue<Runnable> queue;
	public AsyncThreadPool(int threadSize, int queueSize){
		asyncThreadList = new AsyncThread[threadSize];
		queue = ContainerGetter.concurLinkedQueue();
		for (int i = 0; i < threadSize; i++) {
			asyncThreadList[i] = new AsyncThread(i, queue);
			asyncThreadList[i].start();
		}

	}
	
	public int queueSize(){
		return queue.size();
	}
	
	public void print(){
		if (queue.size() > 0) {
			logger.warn("Async Queue Size: " + queueSize());
		}
	}

  @Override
  public void shutdown() {
    interrupted = true;
    
  }

  @Override
  public List<Runnable> shutdownNow() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isShutdown() {
    // TODO Auto-generated method stub
    return interrupted;
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
    if(command == null) {
      throw new NullPointerException();
    }
    this.queue.offer(command);
  }

 
}

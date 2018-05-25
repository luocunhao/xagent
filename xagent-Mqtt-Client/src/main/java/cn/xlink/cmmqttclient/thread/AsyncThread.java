package cn.xlink.cmmqttclient.thread;

import java.util.concurrent.ConcurrentLinkedQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncThread extends Thread{
	private final Logger logger = LoggerFactory.getLogger(AsyncThread.class);
	private final ConcurrentLinkedQueue<Runnable> queue;

	public AsyncThread(int index, ConcurrentLinkedQueue<Runnable> queue) {
		super();
		setName("Async-Thread-" + index);
		this.queue = queue;

	}
	
	@Override
	public void run() {
		long start = 0;
		for (;;) {
			try {
				while (queue.peek() != null) {
					try {
					  Runnable asyncQueue = queue.poll();
						if (asyncQueue == null) {
							continue;
						}
						start = System.currentTimeMillis();
						asyncQueue.run();
						long cost = System.currentTimeMillis() - start;
						if (cost > 200) {
							logger.warn("Process Async thread cost {} ms.",  cost);

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
}

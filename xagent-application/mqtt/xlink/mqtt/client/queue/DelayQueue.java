package xlink.mqtt.client.queue;


/**
 * 延时队列
 * @author shenweiran
 * create at 2017年3月29日 下午3:54:21
 */
public class DelayQueue extends Queue{
	/**
	 * 时间点
	 */
	private final long time;
	private final Queue queue;
	/**
	 * 是否循环，默认为false
	 */
	private final boolean isLoop;
	
	public DelayQueue(Queue queue, long delay_mills){
		this(queue, delay_mills, false);
	}

	public DelayQueue(Queue queue, long delay_mills, boolean isLoop) {
		super();
		this.isLoop = false;
		this.queue = queue;
		this.time = System.currentTimeMillis() + delay_mills;
	}


	public Queue getQueue() {
		return queue;
	}

	public long getTime() {
		return time;
	}
	
	public boolean isLoop() {
		return isLoop;
	}


	public boolean isOk(long now){
		return now > getTime();
	}

	@Override
	public QueueType getQueueType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ( ( queue == null ) ? 0 : queue.hashCode() );
		result = prime * result + (int) ( time ^ ( time >>> 32 ) );
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DelayQueue other = (DelayQueue) obj;
		if (queue == null) {
			if (other.queue != null)
				return false;
		} else if (!queue.equals(other.queue))
			return false;
		if (time != other.time)
			return false;
		return true;
	}
}

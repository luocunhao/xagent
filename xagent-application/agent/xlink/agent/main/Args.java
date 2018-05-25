package xlink.agent.main;

import org.slf4j.LoggerFactory;

public class Args {
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Args.class);

	public static int LOGIC_THREAD_SIZE = 4;
	public static int LOGIC_QUEUE_SIZE = 200000;
	public static int ASYNC_THREAD_SIZE = 16;
	public static int ASYNC_QUEUE_SIZE = 200000;
	public static String XLINK_HOST = "https://api2.xlink.cn";

	public static void loadArgs(String[] args) {
		for (int ix = 0, len = args.length; ix < len; ix++) {
			String param = args[ix].split("=")[0];
			String value = args[ix].split("=")[1];
			switch (param.trim()) {
				case "-logic_queue":
					LOGIC_QUEUE_SIZE = Integer.parseInt(value);
					break;
				case "-async_thread":
					ASYNC_THREAD_SIZE = Integer.parseInt(value);
					break;
				case "-async_queue":
					ASYNC_QUEUE_SIZE = Integer.parseInt(value);
					break;
			}
		}
	}

	public static final void print() {
		logger.info("Args\tLOGIC_THREAD\t:\t" + LOGIC_THREAD_SIZE);
		logger.info("Args\tLOGIC_QUEUE\t:\t" + LOGIC_QUEUE_SIZE);
		logger.info("Args\tASYNC_THREAD\t:\t" + ASYNC_THREAD_SIZE);
		logger.info("Args\tASYNC_QUEUE\t:\t" + ASYNC_QUEUE_SIZE);
	}
}
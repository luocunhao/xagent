package cn.xlink.cmmqttclient.core.utils;

public class MqttUtils {

	private static int messageId = 0;
	
	public synchronized static int getMessageId(){
    if (messageId > 40000) {
			messageId = 1;
		}else{
			messageId++;
		}
		return messageId;
	}
	
	
}

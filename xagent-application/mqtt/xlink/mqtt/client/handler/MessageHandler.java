package xlink.mqtt.client.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MessageHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);
	
	private static final MessageHandler singleton = new MessageHandler();
	
	

	
	private MessageHandler(){

	}
	
	public static MessageHandler instance(){
		return singleton;
	}

	/*public static final String DEVICE_ONLINE_PATTERN = "\\$xlink\\/device\\/{device_id}\\/state\\/online\\/result";*/
	
	public static final String DEVICE_ONLINE_PATTERN = "\\$4\\/{device_id}";
	
	public static final String DEVICE_OFFLINE_PATTERN = "\\$xlink\\/device\\/{device_id}\\/state\\/offline\\/result";
		
	//public static final String DATAPOINT_SYNC = "$xlink/device/{device_id}/datapoint/sync";
	public static final String DATAPOINT_SYNC = "$6/{device_id}";
	
	public void handleMqttMessage(String topic,byte[] data){

	}
	
	
	
	public void handlerOfflineTopic(int deviceId){
		
	}
	
	
	
}

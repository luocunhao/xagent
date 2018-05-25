package cn.xlink.cmmqttclient.token;

import io.netty.handler.codec.mqtt.MqttMessage;

public class MqttMessageToken extends MqttToken<MqttMessage>{

	protected MqttMessageToken(String messageID) {
		super(messageID);
		// TODO Auto-generated constructor stub
	}

}

package cn.xlink.cmmqttclient.handler;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class PingReqHandler extends ChannelInboundHandlerAdapter{

	private static final Logger logger = LoggerFactory.getLogger(PingReqHandler.class);
	
	 @Override
	 public void userEventTriggered(ChannelHandlerContext ctx,Object event) throws Exception{
		 if(event instanceof IdleStateEvent){
			 IdleState idle = ((IdleStateEvent) event).state();
			 if(idle == IdleState.ALL_IDLE || idle == IdleState.WRITER_IDLE){
				 logger.info("sending mqtt ping, sockecthash is{}",ctx.channel().hashCode());
				 MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PINGREQ, false, MqttQoS.AT_MOST_ONCE, false, 0);
			     ctx.channel().writeAndFlush(new MqttMessage(fixedHeader));

			 }
		 }
	 }
}

package tcp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class TcpPingReqHandler extends ChannelInboundHandlerAdapter {

//	private static final Logger logger = LoggerFactory.getLogger(PingReqHandler.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object event) throws Exception {
        if (event instanceof IdleStateEvent) {
            IdleState idle = ((IdleStateEvent) event).state();
            if (idle == IdleState.ALL_IDLE || idle == IdleState.WRITER_IDLE) {
//				logger.info("sending tcp ping... ");
                ctx.channel().writeAndFlush("sending tcp ping... ");
            }
        }
    }
}
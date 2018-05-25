package xlink.cm.ptp.server.pipelineInitializer;



import java.util.Map;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import xlink.cm.agent.ptp.IPtpDecoder;
import xlink.cm.agent.ptp.IPtpEncoder;
import xlink.cm.agent.ptp.IPtpProtocolProcessor;
import xlink.cm.agent.ptp.channel.PtpChannelInfo;
import xlink.cm.ptp.platform.PtpFramework;
import xlink.cm.ptp.queue.PtpChannelMessageLogicQUeue;
import xlink.cm.ptp.server.DefaultPtpServer;
import xlink.core.utils.PtpUtil;
import xlink.mqtt.client.thread.LogicThreadPool;
import xlink.mqtt.client.utils.LogHelper;

public class DataCollectionPipleLineInitializer extends ChannelInitializer<SocketChannel>{

  private IPtpProtocolProcessor processor;
  private IPtpDecoder decoder;
  private IPtpEncoder encoder;
  private DefaultPtpServer server;
  private int keepAlive;
  private Map<Integer, Channel> channelMap;
  private LogicThreadPool logicThreadPool;
	
	public static final String IDEL_HANDLER_NAME = "idelStateHandler";
	
	public DataCollectionPipleLineInitializer(IPtpProtocolProcessor processor, IPtpDecoder decoder,
      IPtpEncoder encoder, DefaultPtpServer server, int keepAlive,
      Map<Integer, Channel> channelMap) {
    super();
    this.processor = processor;
    this.decoder = decoder;
    this.encoder = encoder;
    this.server = server;
    this.keepAlive = keepAlive;
    this.channelMap = channelMap;
    this.logicThreadPool = PtpFramework.instance().getLogicThreadPool();
  }

  @Override
	protected void initChannel(SocketChannel ch) throws Exception {

    ch.pipeline().addFirst(IDEL_HANDLER_NAME, new IdleStateHandler(0, 0, keepAlive))
					 .addAfter(IDEL_HANDLER_NAME, "idleEventHandler", new TimeOutHandler())
        .addLast("decoder", new PtpDecoder(this.decoder)).addLast("encoder", new PtpEncoder(this.encoder))
					 .addLast("handler", new DataCollectionHandler());
	}


  public class DataCollectionHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
      int hashsocket = ctx.channel().hashCode();
      PtpChannelInfo channelInfo = new PtpChannelInfo(hashsocket, -1, null);
      channelMap.put(hashsocket, ctx.channel());
      processor.channelBuild(hashsocket);
      ctx.fireChannelActive();
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
      try {
        logicThreadPool.offer(PtpUtil.getSocketHash(ctx.channel()),
            new PtpChannelMessageLogicQUeue(server, ctx.channel().hashCode(), msg));
      } catch (InterruptedException e) {
        LogHelper.LOGGER().error("", e);
      }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
      ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
      // Close the connection when an exception is raised.
      cause.printStackTrace();
      ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      processor.channelClose(ctx.channel().hashCode());
      channelMap.remove(ctx.channel().hashCode());
    }
  }

  public class TimeOutHandler extends ChannelDuplexHandler {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object event) throws Exception {
      if (event instanceof IdleStateEvent) {
        IdleState idle = ((IdleStateEvent) event).state();

        if (idle == IdleState.ALL_IDLE) {

          // ctx.fireChannelInactive();
          // ChannelManager.instance().removeChannel(ctx.channel().hashCode());
          // channelMap.remove(ctx.channel().hashCode());
          LogHelper.LOGGER()
              .info(String.format("socket %d is time out.", ctx.channel().hashCode()));
          ctx.channel().close();
        }
      }
    }
  }

}

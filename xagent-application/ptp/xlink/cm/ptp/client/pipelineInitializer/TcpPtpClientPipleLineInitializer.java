package xlink.cm.ptp.client.pipelineInitializer;

import javax.net.ssl.SSLEngine;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslHandler;
import xlink.cm.agent.ptp.IPtpClientProtocolProcessor;
import xlink.cm.agent.ptp.IPtpDecoder;
import xlink.cm.agent.ptp.IPtpEncoder;
import xlink.cm.ptp.client.TcpPtpClient;
import xlink.cm.ptp.platform.PtpFramework;
import xlink.cm.ptp.queue.PtpClientChannelMessageLogicQUeue;
import xlink.cm.ptp.server.pipelineInitializer.PtpDecoder;
import xlink.cm.ptp.server.pipelineInitializer.PtpEncoder;
import xlink.core.utils.PtpUtil;
import xlink.mqtt.client.thread.LogicThreadPool;
import xlink.mqtt.client.utils.LogHelper;

public class TcpPtpClientPipleLineInitializer extends ChannelInitializer<SocketChannel>{

  private IPtpClientProtocolProcessor processor;
  private IPtpDecoder decoder;
  private IPtpEncoder encoder;
  private TcpPtpClient ptpClient;
  private SSLEngine sslEngine;

  private LogicThreadPool logicThreadPool;
	
	public static final String IDEL_HANDLER_NAME = "idelStateHandler";
	
	public TcpPtpClientPipleLineInitializer(IPtpClientProtocolProcessor processor, IPtpDecoder decoder,
      IPtpEncoder encoder, TcpPtpClient tcpPtpClient,SSLEngine sslEngine) {
    this.processor = processor;
    this.decoder = decoder;
    this.encoder = encoder;
    this.ptpClient = tcpPtpClient;
    this.sslEngine = sslEngine;
    this.logicThreadPool = PtpFramework.instance().getLogicThreadPool();
  }

  @Override
	protected void initChannel(SocketChannel ch) throws Exception {
    ChannelPipeline p = ch.pipeline();
    if (sslEngine != null) {
      sslEngine.setUseClientMode(true);
      p.addLast("ssl", new SslHandler(sslEngine));
    }  
    p.addLast("decoder", new PtpDecoder(this.decoder)).addLast("encoder", new PtpEncoder(this.encoder))
					 .addLast("handler", new DataCollectionHandler());
	}


  public class DataCollectionHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
      int hashsocket = ctx.channel().hashCode();
      processor.channelBuild(hashsocket);
      ctx.fireChannelActive();
    }
    


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      int hashsocket = ctx.channel().hashCode();
      processor.channelClose(hashsocket);
      super.channelInactive(ctx);
    }



    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
      try {
        logicThreadPool.offer(PtpUtil.getSocketHash(ctx.channel()),
            new PtpClientChannelMessageLogicQUeue(ptpClient, msg));
      } catch (InterruptedException e) {
        LogHelper.LOGGER().error("TcpPtpClientPipleLineInitializer channelRead error, channel id is "+ctx.channel().hashCode(), e);
      }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
      LogHelper.LOGGER().error("DataCollectionHandler exceptionCaught, channel id is "+ctx.channel().hashCode(), cause);
      ctx.close();
    }
  }
}

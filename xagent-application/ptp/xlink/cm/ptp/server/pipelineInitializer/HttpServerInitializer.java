package xlink.cm.ptp.server.pipelineInitializer;

import java.util.Map;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;
import xlink.cm.agent.ptp.IPtpProtocolProcessor;
import xlink.cm.ptp.platform.PtpFramework;
import xlink.cm.ptp.queue.PtpChannelMessageLogicQUeue;
import xlink.cm.ptp.server.DefaultPtpServer;
import xlink.core.utils.PtpUtil;
import xlink.mqtt.client.thread.LogicThreadPool;
import xlink.mqtt.client.utils.LogHelper;


public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {
  private static final int DEFAULT_MAX_CONTENT_LENGTH = 20480;
  private int maxContentLength = DEFAULT_MAX_CONTENT_LENGTH;
  private final SslContext sslCtx;
  private IPtpProtocolProcessor processor;
  private DefaultPtpServer server;
  private LogicThreadPool logicThreadPool;
  private Map<Integer, Channel> channelMap;

  public HttpServerInitializer(SslContext sslCtx, IPtpProtocolProcessor processor,
      DefaultPtpServer server, Map<Integer, Channel> channelMap) {
    super();
    this.sslCtx = sslCtx;
    this.processor = processor;
    this.server = server;
    this.logicThreadPool = PtpFramework.instance().getLogicThreadPool();
    this.channelMap = channelMap;
  }

  @Override
  public void initChannel(SocketChannel ch) {
    ChannelPipeline p = ch.pipeline();
    if (sslCtx != null) {
      p.addLast(sslCtx.newHandler(ch.alloc()));
    }

    // Inbound handlers
    p.addLast("decoder", new HttpRequestDecoder());
    p.addLast("inflater", new HttpContentDecompressor());
    //
    // // Outbound handlers
    p.addLast("encoder", new HttpResponseEncoder());
    p.addLast("chunkWriter", new ChunkedWriteHandler());
    p.addLast("deflater", new HttpContentCompressor());
    //
    // // Aggregator MUST be added last, otherwise results are not correct
    p.addLast("aggregator", new HttpObjectAggregator(maxContentLength));


    /*
     * p.addLast(new HttpServerCodec()); p.addLast("http-aggregator", new
     * HttpObjectAggregator(65536));
     */
    p.addLast(new HttpServerHandler());
  }

  public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
      int hashsocket = ctx.channel().hashCode();
      channelMap.put(hashsocket, ctx.channel());
    }


    /*
     * @Override public void channelRead(ChannelHandlerContext ctx, Object msg) { try {
     * logicThreadPool.offer(PtpUtil.getSocketHash(ctx.channel()), new
     * PtpChannelMessageLogicQUeue(server, ctx.channel().hashCode(), msg)); } catch
     * (InterruptedException e) { LogHelper.LOGGER().error("http server channelRead error", e); } }
     */





    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      processor.channelClose(ctx.channel().hashCode());
      channelMap.remove(ctx.channel().hashCode());
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
      try {

        byte[] contentBytes = new byte[msg.content().readableBytes()];
        msg.content().readBytes(contentBytes);
        FullHttpRequest duplicatMsg = msg.replace(Unpooled.wrappedBuffer(contentBytes));
        logicThreadPool.offer(PtpUtil.getSocketHash(ctx.channel()),
            new PtpChannelMessageLogicQUeue(server, ctx.channel().hashCode(), duplicatMsg));
      } catch (InterruptedException e) {
        LogHelper.LOGGER().error("http server channelRead error", e);
      }
    }
  }
}

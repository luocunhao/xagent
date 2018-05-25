package xlink.agent.shell;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import xlink.agent.shell.command.CommandManager;
import xlink.agent.shell.command.CommandType;
import xlink.cm.ptp.platform.PtpFramework;

public class ShellServer {

  private static final ShellServer singleton = new ShellServer();

  private ShellServer() {

  }

  public static ShellServer instance() {
    return singleton;
  }

  private ServerBootstrap createBootstrap() {
    ServerBootstrap b = new ServerBootstrap();
    b.group(PtpFramework.instance().getBossGroup(), PtpFramework.instance().getWorkerGroup())
        .channel(NioServerSocketChannel.class).childOption(ChannelOption.SO_KEEPALIVE, true)
        .option(ChannelOption.SO_REUSEADDR, true).option(ChannelOption.SO_RCVBUF, 1024 * 8)
        .childOption(ChannelOption.SO_REUSEADDR, true)
        .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator()); // ByteBuf容量动态调整
    return b;
  }

  public void start(int port) {
    // ServerBootstrap bootstrap = createBootstrap();
    ServerBootstrap bootstrap = new ServerBootstrap();
    NioEventLoopGroup group = new NioEventLoopGroup();
    bootstrap.group(group).channel(NioServerSocketChannel.class)
        .childHandler(new ShellPipleLineInitializer()).bind(port);
  }


  class ShellPipleLineInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
      ch.pipeline().addLast(new ShellHandler());

    }

  }

  class ShellHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
      ctx.fireChannelActive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

      ByteBuf buf = (ByteBuf) msg;
      byte[] data = new byte[buf.readableBytes()];
      buf.readBytes(data);
      String commandLine = new String(data);
      int commandIndex = commandLine.indexOf(" ");
      String commandStr = commandLine.substring(0, commandIndex);
      String params = commandLine.substring(commandIndex + 1, commandLine.length());
      CommandType cmdType = CommandType.fromType(commandStr);
      CommandManager.instance().execute(ctx.channel(), cmdType, params);

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
    }

  }
}

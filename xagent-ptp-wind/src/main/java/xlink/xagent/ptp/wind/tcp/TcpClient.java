package xlink.xagent.ptp.wind.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class TcpClient {


    private Bootstrap boostrap;
    private static Channel channel;
    private static boolean IS_NONBLOCK = true;
    private boolean init = false;
    private boolean isClosed = false;
    private CompletableFuture<Boolean> result = new CompletableFuture<Boolean>();
    private String host;
    private int port;
    private volatile boolean isShutDown = false;
    private static final Logger logger = LoggerFactory.getLogger(TcpClient.class);
    public ClientHandler clientHandler = new ClientHandler();
    public long timeout;
    public boolean isNonblock;


    public TcpClient(String host, int port) {
        this.host = host;
        this.port = port;
    }


    public boolean connect(long timeout, boolean isNonblock) {
        IS_NONBLOCK = isNonblock;
        EventLoopGroup group = new NioEventLoopGroup(3);
        boostrap = new Bootstrap();
        boostrap.group(group).channel(NioSocketChannel.class).option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) timeout).option
                (ChannelOption.TCP_NODELAY, true).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline p = ch.pipeline();
                p.addFirst("idelstate", new IdleStateHandler(0, 20, 20));
                p.addLast("ping", new TcpPingReqHandler());
                p.addLast("MessageDecoder", new ByteArrayDecoder());
                p.addLast("MessageEncoder", new ByteArrayEncoder());
                p.addLast(new ClientHandler());
            }
        });
        if (isNonblock) {
            result.complete(true);
        }
        boostrap.connect(this.host, this.port).addListener(new ConnectionListener());

        try {
            return result.get(timeout, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.error("tcpClient connect error.", e);
            return false;
        }
    }


    public void destroy() {
        synchronized (this) {
            this.isShutDown = true;
        }
        int channelId = this.hashCode();
        try {
            channel.close().sync();
        } catch (InterruptedException e) {
            //LogHelper.LOGGER().error(e, "client %s channel %d closed failed.", channelId);
        } finally {
            //LogHelper.LOGGER().info("client {} channel {} closed.", channelId);
        }
    }

    class ConnectionListener implements ChannelFutureListener {


        @Override
        public void operationComplete(ChannelFuture channelFuture) throws Exception {
            if (channelFuture.isSuccess()) {
                System.out.println("connection successfully.");

                channel = channelFuture.channel();
                //Thread.sleep(500);
                clientHandler.setPort(port);
                clientHandler.setHost(host);
                clientHandler.setTimeout(timeout);
                clientHandler.setNonblock(isNonblock);
                TcpClient.this.result.complete(true);
            } else {
                System.out.println("connection failed, retry again.");
                final EventLoop loop = channelFuture.channel().eventLoop();
                loop.schedule(new Runnable() {
                    @Override
                    public void run() {
                        TcpClient.this.boostrap.connect(TcpClient.this.host, TcpClient.this.port).addListener(new ConnectionListener());
                    }

                }, 5L, TimeUnit.SECONDS);
            }
        }
    }

    /**
     * 发送消息
     *
     * @param length 消息长度
     * @param bytes  消息内容
     * @return
     * @throws Exception
     */
    public boolean send(int length, byte[] bytes) throws Exception {
        if (isClosed || init) {
            throw new RuntimeException("client has been closed!");
        }
        final ByteBuf buffer = Unpooled.buffer(length);
        buffer.writeBytes(bytes);
        Thread.sleep(200);
        return channel.writeAndFlush(buffer.array()).sync().isSuccess();
    }
}


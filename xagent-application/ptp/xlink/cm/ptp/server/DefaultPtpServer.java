package xlink.cm.ptp.server;



import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import xlink.cm.agent.ptp.IPtpDecoder;
import xlink.cm.agent.ptp.IPtpEncoder;
import xlink.cm.agent.ptp.IPtpProtocolProcessor;
import xlink.cm.agent.ptp.PtpServer;
import xlink.cm.ptp.platform.PtpFramework;
import xlink.core.utils.ContainerGetter;

/**
 * PtpServer
 * 
 * @author xlink
 *
 */
public abstract class DefaultPtpServer implements PtpServer {

  public static final AttributeKey<String> REMOTE_IP_KEY = AttributeKey.newInstance("remote_ip");
  public static final AttributeKey<Boolean> HAD_READ_PROXY =
      AttributeKey.newInstance("had_read_proxy");
  protected boolean SSL = false;
  protected int serverPort;
  protected EventLoopGroup bossGroup;
  protected EventLoopGroup workerGroup;
  protected IPtpProtocolProcessor processor;
  protected IPtpDecoder decoder;
  protected IPtpEncoder encoder;
  protected int keepAlive = 60;
  protected String serverId;
  protected ChannelFuture f;
  protected Map<Integer, Channel> channelMap;
  /**
   * xlink平台的证书ID
   */
  private String xlinkCertId;
  /**
   * xlink平台的证书Key
   */
  private String xlinkCertKey;



  /**
   * 默认心跳间隔为3分钟
   */
  public static int KEEP_ALIVE = 180 * 2;


  public DefaultPtpServer(String serverId, boolean isUsedSSL, final int serverPort, int keepAlive,
      EventLoopGroup bossGroup, EventLoopGroup workerGroup, IPtpProtocolProcessor processor,
      IPtpDecoder decoder, IPtpEncoder encoder, String certId, String certKey) {
    this.serverPort = serverPort;
    this.SSL = isUsedSSL;
    this.serverId = serverId;
    this.bossGroup = bossGroup;
    this.workerGroup = workerGroup;
    this.processor = processor;
    this.decoder = decoder;
    this.encoder = encoder;
    this.channelMap = ContainerGetter.concurHashMap();
    this.xlinkCertId = certId;
    this.xlinkCertKey = certKey;
    this.keepAlive = keepAlive;

  }

  @Override
  public abstract void startServer() throws Exception;


  @Override
  public String serverId() {
    return this.serverId;
  }

  @Override
  public void sendData(int channelId, Object msg) {
    Channel channel = channelMap.get(channelId);
    if (channel != null) {
      channel.write(msg);
    }

  }

  @Override
  public void sendDataAndFlush(int channelId, Object msg) {
    Channel channel = channelMap.get(channelId);
    if (channel != null) {
      channel.writeAndFlush(msg);
    }

  }

  @Override
  public void sendDataAndFlushAndClose(int channelId, Object msg) {
    Channel channel = channelMap.get(channelId);
    if (channel != null) {
      channel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) {
          channelMap.remove(future.channel().hashCode());
          future.channel().close();
        }
      });
    }

  }

  public String getXlinkCertId() {
    return xlinkCertId;
  }

  public void setXlinkCertId(String xlinkCertId) {
    this.xlinkCertId = xlinkCertId;
  }

  public String getXlinkCertKey() {
    return xlinkCertKey;
  }

  public void setXlinkCertKey(String xlinkCertKey) {
    this.xlinkCertKey = xlinkCertKey;
  }

  public IPtpProtocolProcessor getProcessor() {
    return processor;
  }

  @Override
  public void closeChannel(int channelId) throws Exception {
    Channel channel = channelMap.remove(channelId);
    if (channel != null) {
      channel.close().sync();
    }

  }

  public void close() {
    for (Channel channel : channelMap.values()) {
      channel.close();
    }
    f.channel().close();
  }

  @Override
  public boolean schedule(Runnable command, long delay, TimeUnit unit) {
    java.util.concurrent.ScheduledFuture<?> schedule =
        PtpFramework.instance().getScheduleService().schedule(command, delay, unit);
    return true;
  }

  @Override
  public String getSocketRemoteIp(int channelId) {
    Channel channel  = channelMap.get(channelId);
    if(channel != null){
      Attribute<String> attribute = channel.attr(REMOTE_IP_KEY);
      if (attribute != null && attribute.get() != null) {
        return attribute.get();
      }
      InetSocketAddress inetSocketAddress = (InetSocketAddress) channel.remoteAddress();
      if (inetSocketAddress != null) {
        return inetSocketAddress.getHostString();
      }
    }

    return "";
    }


}
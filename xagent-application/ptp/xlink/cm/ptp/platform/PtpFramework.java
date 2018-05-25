package xlink.cm.ptp.platform;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.net.ssl.SSLEngine;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.ResourceLeakDetector;
import xlink.cm.agent.ptp.IPtpClientProtocolProcessor;
import xlink.cm.agent.ptp.IPtpDecoder;
import xlink.cm.agent.ptp.IPtpEncoder;
import xlink.cm.agent.ptp.IPtpProtocolProcessor;
import xlink.cm.agent.ptp.PtpClient;
import xlink.cm.agent.ptp.PtpClientStrategy;
import xlink.cm.agent.ptp.PtpServer;
import xlink.cm.agent.ptp.PtpServerStrategy;
import xlink.cm.ptp.client.TcpPtpClient;
import xlink.cm.ptp.server.DefaultPtpServer;
import xlink.cm.ptp.server.HttpPtpServer;
import xlink.cm.ptp.server.TcpPtpServer;
import xlink.core.derby.DerbyTableTools;
import xlink.mqtt.client.thread.LogicThreadPool;

public class PtpFramework {

  private EventLoopGroup bossGroup;
  private EventLoopGroup workerGroup;
  private Map<String, DefaultPtpServer> serverMap;
  private static final PtpFramework singleton = new PtpFramework();
  private LogicThreadPool logicThreadPool;
  private ScheduledExecutorService scheduleService;
  private EventLoopGroup clientGroup;
  private Map<String,PtpClient> clientMap;

  private ServerBootstrap boostrap;
  private Bootstrap clientboostrap;

  private PtpFramework() {
    serverMap = new ConcurrentHashMap<String, DefaultPtpServer>();
    clientMap = new ConcurrentHashMap<String, PtpClient>();
    boostrap = new ServerBootstrap();
    scheduleService = Executors.newScheduledThreadPool(1);
    clientboostrap = new Bootstrap();
  }



  public static PtpFramework instance() {
    return singleton;
  }

  public PtpFramework group(EventLoopGroup boss, EventLoopGroup worker,EventLoopGroup clientWorker) {
    this.bossGroup = boss;
    this.workerGroup = worker;
    boostrap.group(this.bossGroup, this.workerGroup).channel(NioServerSocketChannel.class)
        .childOption(ChannelOption.SO_KEEPALIVE, true).option(ChannelOption.SO_REUSEADDR, true)
        .option(ChannelOption.SO_RCVBUF, 1024 * 8).childOption(ChannelOption.SO_REUSEADDR, true)
        .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator()); // ByteBuf容量动态调整
    this.clientGroup = clientWorker;
    
    return this;
  }

  public PtpFramework setLogicThreadPool(LogicThreadPool logicThreadPool) {
    this.logicThreadPool = logicThreadPool;
    return this;
  }

  public LogicThreadPool getLogicThreadPool() {
    return logicThreadPool;
  }



  public ServerBootstrap getBoostrap() {
    return boostrap;
  }

  public PtpServer createPtpServer(String serverId,String cId, String cKey, int port, IPtpDecoder decoder,
      IPtpEncoder encoder, IPtpProtocolProcessor processor, int keepAlive,
      PtpServerStrategy ptpServerStrategy) {
    DefaultPtpServer server = null;
    if(ptpServerStrategy == PtpServerStrategy.HTTP){
      server =
          new HttpPtpServer(serverId, false, port, bossGroup, workerGroup, processor, cId, cKey);
    } else {
      server = new TcpPtpServer(serverId, false, port, keepAlive, bossGroup, workerGroup, processor,
          decoder, encoder, cId, cKey);
    }
   

    serverMap.put(serverId, server);
    DerbyTableTools.instance().createDeviceInfoIfAbsent(serverId);
    return server;
  }

  /**
   * 删除PTP server
   * 
   * @param serverId ptpServer Id
   */
  public DefaultPtpServer destroyPtpServer(String serverId) {
    DefaultPtpServer server = serverMap.remove(serverId);
    server.close();
    return server;
  }



  public EventLoopGroup getBossGroup() {
    return bossGroup;
  }



  public EventLoopGroup getWorkerGroup() {
    return workerGroup;
  }

  public ScheduledExecutorService getScheduleService() {
    return scheduleService;
  }

  public PtpClient createPtpClient(String serverId,String hostName, int port, IPtpDecoder decoder,
                                   IPtpEncoder encoder, IPtpClientProtocolProcessor processor, int keepAlive,PtpClientStrategy ptpClientStrategy,SSLEngine sslEngine) {
                                 PtpClient client = new TcpPtpClient(clientboostrap,serverId,clientGroup,sslEngine,processor,decoder,encoder,hostName,port, keepAlive);
                                 clientMap.put(serverId, client);
                                 DerbyTableTools.instance().createDeviceInfoIfAbsent(serverId);
                                 return client;
                               }

}

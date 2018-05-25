package xlink.cm.ptp.client;

import javax.net.ssl.SSLEngine;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import xlink.cm.agent.ptp.IPtpClientProtocolProcessor;
import xlink.cm.agent.ptp.IPtpDecoder;
import xlink.cm.agent.ptp.IPtpEncoder;
import xlink.cm.agent.ptp.IPtpProtocolProcessor;
import xlink.cm.ptp.client.pipelineInitializer.TcpPtpClientPipleLineInitializer;
import xlink.mqtt.client.utils.LogHelper;


public class TcpPtpClient extends AbstractPtpClient{

  private Channel channel;
  private EventLoopGroup clientGroup;
  private SSLEngine sslEngine;
  private  IPtpClientProtocolProcessor processor;
  private IPtpDecoder decoder;
  private IPtpEncoder encoder;
  private String serverHost;
  private int serverPort;
  Bootstrap b;


  public TcpPtpClient(Bootstrap boostrap,String serverId, EventLoopGroup clientGroup,
      SSLEngine sslEngine, IPtpClientProtocolProcessor processor, IPtpDecoder decoder,
      IPtpEncoder encoder, String serverHost, int serverPort, int keepAlive) {
    super();
    this.clientGroup = clientGroup;
    this.sslEngine = sslEngine;
    this.processor = processor;
    this.decoder = decoder;
    this.encoder = encoder;
    this.serverHost = serverHost;
    this.serverPort = serverPort;
    this.b = boostrap;
    b.group(clientGroup).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
    .handler(new TcpPtpClientPipleLineInitializer( processor,  decoder,encoder, this,sslEngine));
  }



  @Override
  public void connect() throws Exception {
    channel = b.connect(serverHost, serverPort).sync().channel();  
  }



  @Override
  public boolean sendData(Object msg) {
    // TODO Auto-generated method stub
    try {
      return channel.write(msg).sync().isSuccess();
    } catch (InterruptedException e) {
      LogHelper.LOGGER().error("TcpPtpClient sendData", e);
      return false;
    }
  }



  @Override
  public boolean sendDataAndFlush(Object msg) {
    try {
      return channel.writeAndFlush(msg).sync().isSuccess();
    } catch (InterruptedException e) {
      LogHelper.LOGGER().error("TcpPtpClient sendDataAndFlush", e);
      return false;
    }
  }



  @Override
  public IPtpClientProtocolProcessor getProcessor() {
    // TODO Auto-generated method stub
    return this.processor;
  }
}

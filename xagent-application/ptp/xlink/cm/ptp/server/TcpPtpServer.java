package xlink.cm.ptp.server;



import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import xlink.cm.agent.ptp.IPtpDecoder;
import xlink.cm.agent.ptp.IPtpEncoder;
import xlink.cm.agent.ptp.IPtpProtocolProcessor;
import xlink.cm.ptp.platform.PtpFramework;
import xlink.cm.ptp.server.pipelineInitializer.DataCollectionPipleLineInitializer;

/**
 * PtpServer
 * 
 * @author xlink
 *
 */
public class TcpPtpServer extends DefaultPtpServer {



  public TcpPtpServer(String serverId, boolean isUsedSSL, int serverPort, int keepAlive,
      EventLoopGroup bossGroup, EventLoopGroup workerGroup, IPtpProtocolProcessor processor,
      IPtpDecoder decoder, IPtpEncoder encoder, String certId, String certKey) {
    super(serverId, isUsedSSL, serverPort, keepAlive, bossGroup, workerGroup, processor, decoder,
        encoder, certId, certKey);
  }

  @Override
  public void startServer() throws Exception {
    // Configure SSL.
    final SslContext sslCtx;
    if (SSL) {
      SelfSignedCertificate ssc = new SelfSignedCertificate();
      sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
    } else {
      sslCtx = null;
    }
    ServerBootstrap b = PtpFramework.instance().getBoostrap();
      b.childHandler(
        new DataCollectionPipleLineInitializer(processor, decoder, encoder, this, keepAlive,
            this.channelMap));

      // Start the server.
      f = b.bind(this.serverPort).sync();
  }

  public void close() {
    for (Channel channel : channelMap.values()) {
      channel.close();
    }
    f.channel().close();
  }






}

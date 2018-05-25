package xlink.cm.ptp.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import xlink.cm.agent.ptp.IPtpProtocolProcessor;
import xlink.cm.ptp.platform.PtpFramework;
import xlink.cm.ptp.server.pipelineInitializer.HttpServerInitializer;

public class HttpPtpServer extends DefaultPtpServer {

  public HttpPtpServer(String serverId, boolean isUsedSSL, int serverPort, EventLoopGroup bossGroup,
      EventLoopGroup workerGroup, IPtpProtocolProcessor processor, String certId, String certKey) {
    super(serverId, isUsedSSL, serverPort, 0, bossGroup, workerGroup, processor, null, null, certId,
        certKey);
    // TODO Auto-generated constructor stub
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
    b.childHandler(new HttpServerInitializer(sslCtx, processor, this, channelMap));

    // Start the server.
    f = b.bind(this.serverPort).sync();

  }

}

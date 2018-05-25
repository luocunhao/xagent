package xlink.cm.ptp.server.pipelineInitializer;

import java.nio.charset.Charset;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.Attribute;
import xlink.cm.agent.ptp.IPtpDecoder;
import xlink.cm.agent.ptp.exception.PtpException;
import xlink.cm.ptp.server.DefaultPtpServer;
import xlink.mqtt.client.utils.LogHelper;

public final class PtpDecoder extends ByteToMessageDecoder {

  private IPtpDecoder decoder;

  private static final byte[] NGINX_PROXY_PREFIX = "PR".getBytes(Charset.forName("UTF-8"));

  public PtpDecoder(IPtpDecoder decoder) {
    this.decoder = decoder;
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    // *********************************************************************************************
    Attribute<Boolean> had_read_proxy = ctx.channel().attr(DefaultPtpServer.HAD_READ_PROXY);
    if (had_read_proxy == null || had_read_proxy.get() == null || had_read_proxy.get() == false) {
      // 附加代码，解析Nginx Proxy IP数据
      in.markReaderIndex();
      byte[] header_bytes = new byte[2];
      header_bytes[0] = in.readByte();
      header_bytes[1] = in.readByte();
      in.resetReaderIndex();

      // String prefix = new String(header_bytes, Charset.forName("UTF-8"));
      if (NGINX_PROXY_PREFIX[0] == header_bytes[0] && NGINX_PROXY_PREFIX[1] == header_bytes[1]) {
        byte by_r = 0;
        byte by_n = 0;
        int readerIndex = 0;
        do {
          by_r = in.readByte();
          // 字符"/r"
          if (by_r == (byte) 13) {
            by_n = in.readByte();
            // 字符"/n"
            if (by_n == (byte) 10) {
              readerIndex = in.readerIndex();
              break;
            }
          }
        } while (true);
        in.resetReaderIndex();
        byte[] proxy_bytes = new byte[readerIndex];
        in.readBytes(proxy_bytes);
        String proxy = new String(proxy_bytes, Charset.forName("UTF-8"));
        String[] splt = proxy.split(" ");
        String ip = splt[2];
        ctx.channel().attr(DefaultPtpServer.REMOTE_IP_KEY).set(ip);
        ctx.channel().attr(DefaultPtpServer.HAD_READ_PROXY).set(true);
        LogHelper.LOGGER().info("Nginx Proxy : {}", proxy);
        return;
      } else {
        ctx.channel().attr(DefaultPtpServer.HAD_READ_PROXY).set(true);
      }
    }
    // *************************************************************************************************


    in.markReaderIndex();
    try {
      decoder.doDecode(in, out);

    } catch (PtpException ke) {
      in.clear();

    } catch (Exception e) {
      in.resetReaderIndex();
    }
  }

}

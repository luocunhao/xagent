package xlink.cm.ptp.server.pipelineInitializer;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import xlink.cm.agent.ptp.IPtpEncoder;
import xlink.cm.agent.ptp.dataStruture.IPtpMessage;


public class PtpEncoder extends MessageToMessageEncoder<IPtpMessage> {
  private IPtpEncoder encoder;

  public PtpEncoder(IPtpEncoder encoder) {
    this.encoder = encoder;
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, IPtpMessage washingCar, List<Object> out)
      throws Exception {
    // TODO Auto-generated method stub
    ByteBuf buf = encoder.doEncode(ctx.alloc(), washingCar,out);
    buf.markReaderIndex();
    int size = buf.readableBytes();
    byte[] bufCopy = new byte[size];
    buf.readBytes(bufCopy);
    buf.resetReaderIndex();
    // out.add(buf);

  }
}

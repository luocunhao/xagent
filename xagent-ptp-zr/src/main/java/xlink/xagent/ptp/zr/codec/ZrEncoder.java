package xlink.xagent.ptp.zr.codec;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import xlink.cm.agent.ptp.IPtpEncoder;
import xlink.cm.agent.ptp.dataStruture.IPtpMessage;
import xlink.xagent.ptp.zr.message.ZrMessage;

public class ZrEncoder implements IPtpEncoder {
  private static final Logger logger = LoggerFactory.getLogger(ZrEncoder.class);

  @Override
  public ByteBuf doEncode(ByteBufAllocator byteBufAllocator, IPtpMessage message,
      List<Object> out) {
    logger.info("start to encode");
    ZrMessage demoMsg = (ZrMessage) message;
    ByteBuf buf = byteBufAllocator.buffer();
    ByteBuf payloadBuf = encodePaylaod(demoMsg.getHeader().getMessageType(), demoMsg);
    buf.writeByte(demoMsg.getHeader().getMessageType().type());
    buf.writeShort(payloadBuf.readableBytes());
    buf.writeBytes(payloadBuf);
    out.add(buf);
    return buf;
  }


  public ByteBuf encodePaylaod(ZrMessageType type, ZrMessage message) {

    switch (type) {
      case Auth: {
        ByteBuf buf = Unpooled.buffer();
        buf.writeByte(0);
        return buf;
      }
      case Offline: {
        ByteBuf buf = Unpooled.buffer();
        buf.writeByte(0);
        return buf;
      }
      case Datapoint: {
        ByteBuf buf = Unpooled.buffer();
        buf.writeByte(0);
        return buf;
      }
      case HeartBeat: {
        ByteBuf buf = Unpooled.buffer();
        buf.writeByte(0);
        return buf;
      }
      default: {
        ByteBuf buf = Unpooled.buffer();
        buf.writeByte(0);
        return buf;
      }
    }
  }
}

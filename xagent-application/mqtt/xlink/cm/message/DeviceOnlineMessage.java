package xlink.cm.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import xlink.cm.message.type.CMMessageType;

public class DeviceOnlineMessage extends CMMessage {


  private boolean hasOnlineIp = false;
  private byte[] onlineIp;

  @Override
  public CMMessageType getMessageType() {
    return CMMessageType.DeviceOnline;
  }

  @Override
  public void parseValue(int version, ByteBuf buf) {

    byte onlineFlag = buf.readByte();
    hasOnlineIp = (onlineFlag & 0x80) > 0;
    if (hasOnlineIp) {
      onlineIp = new byte[4];
      buf.readBytes(onlineIp);
    }
  }

  @Override
  public byte[] toPayload(int version) {
    ByteBuf body = Unpooled.buffer();
    byte onlineFlag = 0;
    if (hasOnlineIp) {
      onlineFlag |= 0x80;
    }
    body.writeByte(onlineFlag);
    if (hasOnlineIp) {
      body.writeBytes(onlineIp);
    }

    int length = 4 + body.readableBytes();
    ByteBuf buf = Unpooled.buffer(length);
    buf.writeShort(getMessageType().type());
    buf.writeShort(body.readableBytes());
    buf.writeBytes(body, body.readableBytes());
    return buf.array();
  }

  public boolean isHasOnlineIp() {
    return hasOnlineIp;
  }

  public void setHasOnlineIp(boolean hasOnlineIp) {
    this.hasOnlineIp = hasOnlineIp;
  }

  public byte[] getOnlineIp() {
    return onlineIp;
  }

  public void setOnlineIp(byte[] onlineIp) {
    this.onlineIp = onlineIp;
  }

  public String getOnlineIpString() {
    if (hasOnlineIp) {
      // x.x.x.x
      return (onlineIp[0] & 0xFF) + "." + (onlineIp[1] & 0xFF) + "." + (onlineIp[2] & 0xFF) + "."
          + (onlineIp[3] & 0xFF);
    } else {
      return null;
    }
  }

}

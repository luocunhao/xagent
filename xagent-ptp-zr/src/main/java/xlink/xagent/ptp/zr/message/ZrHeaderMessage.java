package xlink.xagent.ptp.zr.message;

import xlink.xagent.ptp.zr.codec.ZrMessageType;

public class ZrHeaderMessage {

  private ZrMessageType messageType;
  private int payloadLength;

  public ZrHeaderMessage(ZrMessageType messageType, int payloadLength) {
    super();
    this.messageType = messageType;
    this.payloadLength = payloadLength;
  }

  public ZrMessageType getMessageType() {
    return messageType;
  }

  public void setMessageType(ZrMessageType messageType) {
    this.messageType = messageType;
  }

  public int getPayloadLength() {
    return payloadLength;
  }

  public void setPayloadLength(int payloadLength) {
    this.payloadLength = payloadLength;
  }

  @Override
  public String toString() {
    return "ZrHeaderMessage [messageType=" + messageType + ", payloadLength=" + payloadLength
        + "]";
  }



}

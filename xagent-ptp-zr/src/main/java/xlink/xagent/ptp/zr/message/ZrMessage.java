package xlink.xagent.ptp.zr.message;

import xlink.cm.agent.ptp.dataStruture.IPtpMessage;

public class ZrMessage implements IPtpMessage {

  private ZrHeaderMessage header;
  private ZrPayloadMessage payload;

  public ZrHeaderMessage getHeader() {
    return header;
  }

  public void setHeader(ZrHeaderMessage header) {
    this.header = header;
  }

  public ZrPayloadMessage getPayload() {
    return payload;
  }

  public void setPayload(ZrPayloadMessage payload) {
    this.payload = payload;
  }

  public ZrMessage(ZrHeaderMessage header, ZrPayloadMessage payload) {
    super();
    this.header = header;
    this.payload = payload;
  }



}

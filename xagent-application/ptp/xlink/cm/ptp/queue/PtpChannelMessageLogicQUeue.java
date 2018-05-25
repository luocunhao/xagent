package xlink.cm.ptp.queue;

import xlink.cm.ptp.server.DefaultPtpServer;
import xlink.mqtt.client.queue.LogicQueue;
import xlink.mqtt.client.queue.QueueType;

public class PtpChannelMessageLogicQUeue extends LogicQueue {

  private DefaultPtpServer ptpServer;

  private Object message;

  private int hashcode;

  @Override
  public QueueType getQueueType() {
    // TODO Auto-generated method stub
    return QueueType.LOGIC_PTP_CHANNEL_MESSAGE;
  }

  public PtpChannelMessageLogicQUeue(DefaultPtpServer ptpServer, int hashcode, Object message) {
    this.ptpServer = ptpServer;
    this.message = message;
    this.hashcode = hashcode;
  }

  public DefaultPtpServer getPtpServer() {
    return ptpServer;
  }

  public void setPtpServer(DefaultPtpServer ptpServer) {
    this.ptpServer = ptpServer;
  }

  public Object getMessage() {
    return message;
  }

  public void setMessage(Object message) {
    this.message = message;
  }

  public int getHashcode() {
    return hashcode;
  }

  public void setHashcode(int hashcode) {
    this.hashcode = hashcode;
  }



}

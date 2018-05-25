package xlink.cm.ptp.queue;

import xlink.cm.ptp.client.AbstractPtpClient;
import xlink.mqtt.client.queue.LogicQueue;
import xlink.mqtt.client.queue.QueueType;

public class PtpClientChannelMessageLogicQUeue extends LogicQueue {

  private AbstractPtpClient ptpClient;

  private Object message;

  @Override
  public QueueType getQueueType() {
    // TODO Auto-generated method stub
    return QueueType.LOGIC_PTP_CLIENT_CHANNEL_MESSAGE;
  }

  public PtpClientChannelMessageLogicQUeue(AbstractPtpClient ptpClient, Object message) {
    this.ptpClient = ptpClient;
    this.message = message;
  }
  
  public AbstractPtpClient getPtpClient() {
    return ptpClient;
  }

  public void setPtpClient(AbstractPtpClient ptpClient) {
    this.ptpClient = ptpClient;
  }

  public Object getMessage() {
    return message;
  }

  public void setMessage(Object message) {
    this.message = message;
  }
}

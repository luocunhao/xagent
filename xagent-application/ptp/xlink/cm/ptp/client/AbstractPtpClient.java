package xlink.cm.ptp.client;
import xlink.cm.agent.ptp.IPtpClientProtocolProcessor;
import xlink.cm.agent.ptp.PtpClient;

public abstract class AbstractPtpClient implements PtpClient {

  public abstract IPtpClientProtocolProcessor getProcessor();
  
}

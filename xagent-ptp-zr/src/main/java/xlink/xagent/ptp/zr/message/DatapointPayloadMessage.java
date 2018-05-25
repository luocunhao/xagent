package xlink.xagent.ptp.zr.message;

import java.util.List;

import xlink.cm.agent.ptp.dataStruture.DPtpDatapoint;

public class DatapointPayloadMessage implements ZrPayloadMessage {

  private int deviceId;
  private List<DPtpDatapoint> datapoints;

  public List<DPtpDatapoint> getDatapoints() {
    return datapoints;
  }

  
  public void setDatapoints(List<DPtpDatapoint> datapoints) {
    this.datapoints = datapoints;
  }

  
  
  public int getDeviceId() {
	return deviceId;
}


public void setDeviceId(int deviceId) {
	this.deviceId = deviceId;
}


public DatapointPayloadMessage(int deviceId,List<DPtpDatapoint> datapoints) {
	  this.deviceId = deviceId;
    this.datapoints = datapoints;
  }


@Override
public String toString() {
	return "DatapointPayloadMessage [deviceId=" + deviceId + ", datapoints=" + datapoints + "]";
}





}

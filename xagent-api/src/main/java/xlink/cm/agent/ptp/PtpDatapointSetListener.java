package xlink.cm.agent.ptp;

import java.util.Map;

import xlink.cm.agent.ptp.dataStruture.DPtpDatapoint;

/**
 * 数据端点设置的监听器， 用于接收xlink平台下发的数据
 * 
 * @author xlink
 *
 */
public interface PtpDatapointSetListener {

  /**
   * 数据端点消息通知
   * 
   * @param deviceId xlink设备Id
   * @param messageId 数据端点设置的消息ID
   * @param datapointValue 数据端点内容
   */
  public void datapointNotify(int deviceId, int messageId,
      Map<Integer, DPtpDatapoint> datapointValue);
}

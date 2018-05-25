package xlink.cm.agent.ptp;

public interface IHeartBeatHandler {

  /**
   * 心跳超时通知
   * 
   * @param channelSocketHash
   */
  public void timeoutNotify(int channelSocketHash);
}

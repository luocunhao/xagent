package xlink.cm.agent.ptp;

public interface IPtpClientProtocolProcessor {

  /**
   * 处理消息
   * 
   * @param client
   * @param msg
   */
  public void process(PtpClient client, Object msg);

  /**
   * 客户端链接后的回调
   * 
   * @param channelInfo
   */
  public void channelBuild(int channelId);

  /**
   * 客户端链接断开后的回调
   */
  public void channelClose(int channelId);

}

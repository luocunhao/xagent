package xlink.cm.agent.ptp;

public interface IPtpProtocolProcessor {

  /**
   * 处理消息
   * 
   * @param server
   * @param channelId
   * @param msg
   */
  public void process(PtpServer server, int channelId, Object msg);

  /**
   * ptpServer产生一条客户端链接后的回调
   * 
   * @param channelInfo
   */
  public void channelBuild(int channelId);

  /**
   * 客户端链接断开后的回调
   */
  public void channelClose(int channelId);

}

package xlink.cm.agent.ptp;

import java.util.concurrent.TimeUnit;

/**
 * ptp的服务端，用于连接设备和收发数据
 * 
 * @author xlink
 *
 */
public interface PtpServer {

  /**
   * ptpServer的唯一标识
   * 
   * @return
   */
  public String serverId();

  /**
   * 启动ptpServer
   * 
   * @throws Exception
   */
  public void startServer() throws Exception;

  /**
   * 向设备发送消息
   * 
   * @param channelId 设备连接通道的唯一标识
   * @param msg 需要发送的数据
   */
  public void sendData(int channelId, Object msg);

  /**
   * 向设备发送消息，立即刷新缓冲区
   * 
   * @param channelId 设备连接通道的唯一标识
   * @param msg 需要发送的数据
   */
  public void sendDataAndFlush(int channelId, Object msg);

  /**
   * 向设备发送消息，立即刷新缓冲区,发送完成后关闭通道。
   * 
   * @param channelId 设备连接通道的唯一标识
   * @param msg 需要发送的数据
   */
  public void sendDataAndFlushAndClose(int channelId, Object msg);

  /**
   * 关闭设备连接
   * 
   * @param channelId
   */
  public void closeChannel(int channelId) throws Exception;

  /**
   * 延时任务
   * 
   * @param command 需要延时执行的任务
   * @param delay 延时时长
   * @param unit 时间单位
   * @return 是否添加成功
   */
  public boolean schedule(Runnable command, long delay, TimeUnit unit);

  /**
   * 获取设备端的ip地址
   * 
   * @param channelId
   * @return
   */
  public String getSocketRemoteIp(int channelId);
}

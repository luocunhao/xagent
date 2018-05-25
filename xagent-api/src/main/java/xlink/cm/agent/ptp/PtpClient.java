package xlink.cm.agent.ptp;

public interface PtpClient {
  /**
   * 
  
   * @Method: connect 
  
   * @Description:Ptp客户端连接
  
   * @param 
  
   * @return void
   * 
   * @date 2018年5月9日 下午5:54:12
   */
  public void connect() throws Exception;

/**
 * 

 * @Method: sendData 

 * @Description: 发送数据

 * @param @param msg 发送内容
 * @param @return

 * @return boolean
 
 * @date 2018年5月9日 下午5:51:32
 */
  public boolean sendData( Object msg);
  /**
   * 

   * @Method: sendDataAndFlush 

   * @Description: 发送数据，并且立即刷新缓冲区

   * @param @param msg 发送内容
   * @param @return

   * @return boolean
   
   * @date 2018年5月9日 下午5:51:32
   */
  public boolean sendDataAndFlush(Object msg);

  
  
}

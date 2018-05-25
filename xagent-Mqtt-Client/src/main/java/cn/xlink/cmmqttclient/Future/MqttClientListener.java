package cn.xlink.cmmqttclient.Future;

import xlink.cm.message.DatapointSetMessage;
import xlink.cm.message.DatapointSyncMessage;
import xlink.cm.message.DeviceStateMessage;

/**
 * 

 * @ClassName MqttConnectionListener

 * @Description 重连通知

 * @author linsida@xlink.cn

 * @date 2018年4月25日
 */
public interface MqttClientListener {

  /**
   * 
  
   * @Method: connectionNotify 
  
   * @Description: 成功连接
  
   * @param @param pluginId
  
   * @return void
  
   * @throws 
    
   * @author linsida@xlink.cn
   
   * @date 2018年4月25日 下午3:27:27
   */
  public void connectionNotify(String pluginId);
  
  /**
   * 
  
   * @Method: connectionLost 
  
   * @Description: 连接丢失
  
   * @param @param pluginId
  
   * @return void
  
   * @throws 
    
   * @author linsida@xlink.cn
   
   * @date 2018年4月25日 下午3:27:40
   */
  public void connectionLost(String pluginId);
  /**
   * 
  
   * @Method: messageDatapointSyncNotify 
  
   * @Description: 收到来自cm server的数据上报消息
  
   * @param @param deviceId
   * @param @param message
  
   * @return void
  
   * @throws 
    
   * @author linsida@xlink.cn
   
   * @date 2018年4月25日 下午3:45:41
   */
  public void messageDatapointSyncNotify(int deviceId, DatapointSyncMessage syncMessage);
  
  /**
   * 
  
   * @Method: messageDatapointSetNotify 
  
   * @Description: 收到来自cm server的控制数据端点消息
  
   * @param @param deviceId
   * @param @param datapointSetMessage
  
   * @return void
  
   * @throws 
    
   * @author linsida@xlink.cn
   
   * @date 2018年4月25日 下午3:48:40
   */
  public void messageDatapointSetNotify(int deviceId,DatapointSetMessage datapointSetMessage);
  
  /**
   * 
  
   * @Method: messageDeviceStateNotify 
  
   * @Description: 收到来自cm server的设备状态消息
  
   * @param @param deviceId
   * @param @param deviceStateMessage
  
   * @return void
  
   * @throws 
    
   * @author linsida@xlink.cn
   
   * @date 2018年4月25日 下午3:51:17
   */
  public void messageDeviceStateNotify(int deviceId,DeviceStateMessage deviceStateMessage);
  
  
}

package cn.xlink.cmmqttclient.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.xlink.cmmqttclient.Future.MqttClientListener;
import cn.xlink.cmmqttclient.exception.XlinkMqttException;
import cn.xlink.cmmqttclient.queue.AsyncQueue;
import cn.xlink.cmmqttclient.thread.AsyncThreadPool;
import cn.xlink.cmmqttclient.thread.LogicThreadPool;
import io.netty.channel.EventLoopGroup;

/**
 * Mqtt客户端管理器
 * 
 * @author xlink
 *
 */
public class MqttClientManager {

  private static final MqttClientManager singleton = new MqttClientManager();

  private Map<String, MqttClient> clientMap = new ConcurrentHashMap<String, MqttClient>();

  private AsyncThreadPool asyncThreadPool;
  private LogicThreadPool logicThreadPool;
  private EventLoopGroup nettyEventgroup;

  private MqttClientManager() {

  }

  public void setAsyncThreadPool(AsyncThreadPool asyncThreadPool) {
    this.asyncThreadPool = asyncThreadPool;
  }

  public void setLogicThreadPool(LogicThreadPool logicThreadPool) {
    this.logicThreadPool = logicThreadPool;
  }

  public void setNettyEventgroup(EventLoopGroup nettyEventgroup) {
    this.nettyEventgroup = nettyEventgroup;
  }

  public static MqttClientManager instance() {
    return singleton;
  }

  public void restorMqttClient(String id, MqttClient client) {
    clientMap.put(id, client);
  }

  /**
   * 关闭一个MQTT客户端
   * 
   * @param id
   */
  public void deleteMqttClient(String certId) {
    MqttClient client = clientMap.remove(certId);
    client.destroy();
  }

  public void putAsyncQueue(AsyncQueue queue) {
      asyncThreadPool.execute(queue);
  }

  /**
   * 创建一个MQTT客户端，如果certId已存在，则返回已创建的MQTT客户端
   * 
   * @param pluginId MqttClient的索引建
   * @param certId 建立连接的证书ID
   * @param certKey 建立连接的证书的秘钥
   * @param clientId MQTT连接时指定的客户端标识
   * @return 
   */
  public synchronized MqttClient createMqttClient(String pluginId,String certId, String certKey, String clientId) throws XlinkMqttException{
    return createMqttClient( pluginId, certId,  certKey,  clientId, null);
  }
  
  
  /**
   * 创建一个MQTT客户端，如果certId已存在，则返回已创建的MQTT客户端
   * 
   * @param pluginId MqttClient的索引建
   * @param certId 建立连接的证书ID
   * @param certKey 建立连接的证书的秘钥
   * @param clientId MQTT连接时指定的客户端标识
   * @param listenr 回调函数
   * @return 
   */
  public synchronized MqttClient createMqttClient(String pluginId,String certId, String certKey, String clientId,MqttClientListener listenr) throws XlinkMqttException{
    if (clientMap.containsKey(pluginId)) {
      return clientMap.get(pluginId);
    } else {
      MqttClient client = new MqttClient(asyncThreadPool, logicThreadPool, nettyEventgroup, certId,
          certKey, pluginId, clientId);
      if(listenr != null) {
        client.addMqttClientListener(listenr);
      }
      boolean isAuth = client.syncAndGetConnResult();
      if(isAuth == false) {
        throw new XlinkMqttException(XlinkMqttException.MQTT_CONNECT_AUTH_FAILED,null);
      }
      clientMap.put(pluginId, client);
      return client;
    }
  }

  /**
   * 获取一个已经存在的MQTT客户端
   * 
   * @param certId
   * @return
   */
  public MqttClient getMqttClient(String pluginId) {
    return clientMap.get(pluginId);
  }

}

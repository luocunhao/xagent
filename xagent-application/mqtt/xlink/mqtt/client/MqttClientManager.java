package xlink.mqtt.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.EventLoopGroup;
import xlink.cm.agent.ptp.PtpCertificateType;
import xlink.mqtt.client.queue.AsyncQueue;
import xlink.mqtt.client.thread.AsyncThreadPool;
import xlink.mqtt.client.thread.LogicThreadPool;
import xlink.mqtt.client.utils.LogHelper;

/**
 * Mqtt客户端管理器
 * 
 * @author xlink
 *
 */
public class MqttClientManager {

  private static final MqttClientManager singleton = new MqttClientManager();

  private Map<String, MqttClient> clientMap = new ConcurrentHashMap<String, MqttClient>();
  private Map<PtpCertificateType,String> mqttClientMap = new ConcurrentHashMap<PtpCertificateType,String> ();

  private AsyncThreadPool asyncThreadPool;
  private LogicThreadPool logicThreadPool;
  private EventLoopGroup nettyEventgroup;

  private MqttClientManager() {
	  mqttClientMap.put(PtpCertificateType.Product, "X:DEVICE;A:2;V:1;");
	  mqttClientMap.put(PtpCertificateType.Certificate, "X:DEVICE;A:3;V:1;");
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

  public void deleteMqttClient(String id) {
    MqttClient client = clientMap.remove(id);
    client.destroy();
  }

  public void putAsyncQueue(AsyncQueue queue) {
    try {
      asyncThreadPool.offer(queue);
    } catch (InterruptedException e) {
      LogHelper.LOGGER().error("add to async queue failed.", e);
    }
  }

  public synchronized MqttClient getMqttClient(String certId, String certKey, String pluginId,PtpCertificateType certType) {
    if (clientMap.containsKey(certId)) {
      return clientMap.get(certId);
    } else {
      MqttClient client = new MqttClient(asyncThreadPool, logicThreadPool, nettyEventgroup, certId,certKey, pluginId,mqttClientMap.get(certType));
      clientMap.put(certId, client);
      return client;
    }
  }


}

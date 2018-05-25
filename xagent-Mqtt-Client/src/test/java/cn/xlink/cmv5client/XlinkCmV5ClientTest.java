package cn.xlink.cmv5client;

import cn.xlink.cmmqttclient.Future.DatapointSetListener;
import cn.xlink.cmmqttclient.Future.MqttClientListener;
import cn.xlink.cmmqttclient.client.MqttClient;
import cn.xlink.cmmqttclient.client.MqttClientManager;
import cn.xlink.cmmqttclient.config.MqttConfig;
import cn.xlink.cmmqttclient.core.XlinkCmOperator;
import cn.xlink.cmmqttclient.thread.AsyncThreadPool;
import cn.xlink.cmmqttclient.thread.LogicThreadPool;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import xlink.cm.message.DatapointSetMessage;
import xlink.cm.message.DatapointSyncMessage;
import xlink.cm.message.DeviceStateMessage;

public class XlinkCmV5ClientTest {

  public static void main(String[] args) throws Exception {

    // 加载配置文件
    MqttConfig.initConfig("config/mqtt.properties");
    
    //初始化线程池
    final LogicThreadPool logicThreadPool = new LogicThreadPool(4, 200000);
    final AsyncThreadPool asyncThreadPool = new AsyncThreadPool(16, 200000);
    EventLoopGroup workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 4);
    
  //初始化Mqtt客户端
    MqttClientManager.instance().setAsyncThreadPool(asyncThreadPool);
    MqttClientManager.instance().setLogicThreadPool(logicThreadPool);
    MqttClientManager.instance().setNettyEventgroup(workerGroup);
    
    //创建一个MQTT连接
    //证书ID
    String certId = "5a55a97b4abd1f4fc5516c4c-1";
    //证书Key
    String certKey = "d4355b83-8789-4a1c-a3e8-f2f8e8b28e51";
    //唯一标识
        String pluginId= "test";
    //MQTT客户端ID
    String clientId = "X:DEVICE;A:3;V:1;";
    
    int deviceId = 1860237890;
    MqttClient mqttClient =
        MqttClientManager.instance().createMqttClient(certId, certKey, pluginId, clientId);

    //CM操作

    try {
      // 设备上线
      XlinkCmOperator.deviceOnline(mqttClient, deviceId, null);
      // 设备下线
      XlinkCmOperator.deviceOffline(mqttClient, deviceId).get();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // 当设备上线后可以订阅应用设置主题
    mqttClient.addMqttClientListener(new MqttClientListener() {

      @Override
      public void connectionNotify(String pluginId) {
        // TODO Auto-generated method stub
        
      }

      @Override
      public void connectionLost(String pluginId) {
        // TODO Auto-generated method stub
        
      }

      @Override
      public void messageDatapointSyncNotify(int deviceId, DatapointSyncMessage syncMessage) {
        // TODO Auto-generated method stub
        
      }

      @Override
      public void messageDatapointSetNotify(int deviceId, DatapointSetMessage datapointSetMessage) {
        // TODO Auto-generated method stub
        
      }

      @Override
      public void messageDeviceStateNotify(int deviceId, DeviceStateMessage deviceStateMessage) {
        // TODO Auto-generated method stub
        
      }

     

    });
    
    // 当收到应用设置的数据后，需要回复给CM服务器. messageId从DatapointSetMessage中获取
    try {
      XlinkCmOperator.replyDatapointSet(mqttClient, 1, 1002, 0);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }



}

package org.gateway.pf4j.application;

import io.netty.channel.nio.NioEventLoopGroup;
import xlink.agent.main.Args;
import xlink.cm.agent.ptp.PtpCertificateType;
import xlink.cm.message.DeviceLoginMessage;
import xlink.core.utils.ByteTools;
import xlink.core.utils.MD5Tool;
import xlink.mqtt.client.MqttClient;
import xlink.mqtt.client.MqttClientManager;
import xlink.mqtt.client.config.MqttConfig;
import xlink.mqtt.client.thread.AsyncThreadPool;
import xlink.mqtt.client.thread.LogicThreadPool;

public class MqttClientTest {


  public static void main(String[] args) throws Exception {

    final LogicThreadPool logicThreadPool =
        new LogicThreadPool(Args.LOGIC_THREAD_SIZE, Args.LOGIC_QUEUE_SIZE);
    final AsyncThreadPool asyncThreadPool =
        new AsyncThreadPool(Args.ASYNC_THREAD_SIZE, Args.ASYNC_QUEUE_SIZE, logicThreadPool, null);
    MqttConfig.BROKER_HOST = "127.0.0.1";
    MqttConfig.BROKER_PORT = 1883;
    String mqttClientId = "X:DEVICE;A:2;V:1;";
    System.out.println(
        MD5Tool.MD5("1607d2aed9bfc6001607d2aed9bfc601" + "e70ad8a5e5a354d870e5205beabe476d"));
    // 初始化Mqtt客户端
    MqttClientManager.instance().setAsyncThreadPool(asyncThreadPool);
    MqttClientManager.instance().setLogicThreadPool(logicThreadPool);
    MqttClientManager.instance().setNettyEventgroup(new NioEventLoopGroup());
    DeviceLoginMessage deviceLoginMsg = new DeviceLoginMessage();
    byte[] payload = deviceLoginMsg.toPayload(1);
    ByteTools.printBytes2HexString(payload);

    MqttClient mqttClient = MqttClientManager.instance()
        .getMqttClient("1607d2aed9bfc6001607d2aed9bfc601", "e70ad8a5e5a354d870e5205beabe476d",
            "test",PtpCertificateType.Product);



    mqttClient.publish(123, "$11", payload, 0);

  }
}

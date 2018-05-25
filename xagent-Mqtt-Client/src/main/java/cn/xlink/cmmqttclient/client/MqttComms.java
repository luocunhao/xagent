package cn.xlink.cmmqttclient.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import cn.xlink.cmmqttclient.Future.MqttClientListener;
import cn.xlink.cmmqttclient.Future.PublishMessageListener;
import cn.xlink.cmmqttclient.config.MqttConfig;
import cn.xlink.cmmqttclient.core.utils.ContainerGetter;
import cn.xlink.cmmqttclient.core.utils.LogHelper;
import cn.xlink.cmmqttclient.core.utils.MD5Tool;
import cn.xlink.cmmqttclient.core.utils.StringTools;
import cn.xlink.cmmqttclient.exception.XlinkMqttException;
import cn.xlink.cmmqttclient.queue.subclass.MqttReceiveLogicQueue;
import cn.xlink.cmmqttclient.queue.subclass.MqttSendAsyncQueue;
import cn.xlink.cmmqttclient.queue.subclass.MqttSendLogicQueue;
import cn.xlink.cmmqttclient.thread.AsyncThreadPool;
import cn.xlink.cmmqttclient.thread.LogicThreadPool;
import cn.xlink.cmmqttclient.token.MqttCmMessageToken;
import cn.xlink.cmmqttclient.token.MqttMessageToken;
import cn.xlink.cmmqttclient.token.MqttToken;
import cn.xlink.cmmqttclient.token.MqttTokenManager;
import cn.xlink.cmmqttclient.token.MqttTokenType;
import cn.xlink.cmmqttclient.type.XlinkMqttTopicType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttConnectPayload;
import io.netty.handler.codec.mqtt.MqttConnectVariableHeader;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import io.netty.handler.codec.mqtt.MqttSubscribePayload;
import io.netty.handler.codec.mqtt.MqttTopicSubscription;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;
import io.netty.handler.codec.mqtt.MqttVersion;
import io.netty.util.concurrent.GenericFutureListener;
import xlink.cm.message.CMMessage;
import xlink.cm.message.DatapointSetMessage;
import xlink.cm.message.DatapointSyncMessage;
import xlink.cm.message.DeviceStateMessage;

public class MqttComms {

  private MqttClient mqttClient;
  private Object connectLock = new Object();
  private AsyncThreadPool asyncThreadPool;
  private LogicThreadPool logicThreadPool;
  //private Map<XlinkMqttTopicType, PublishMessageListener<? extends CMMessage>> publishMsgListener;
  private List<MqttClientListener> mqttClientListener = null;
  // 剩余消息处理数
  private AtomicInteger messageCount;

  enum ConnState {
    NotConn, Connecting, Connected, Closing, Closed
  }

  private ConnState connState;


  public MqttComms(MqttClient mqttClient, AsyncThreadPool asyncThreadPool,
      LogicThreadPool logicThreadPool) {
    super();
    this.mqttClient = mqttClient;
    this.connState = ConnState.NotConn;
    this.asyncThreadPool = asyncThreadPool;
    this.logicThreadPool = logicThreadPool;
   // publishMsgListener = ContainerGetter.hashMap();
    mqttClientListener = ContainerGetter.arrayList();
    messageCount = new AtomicInteger();
  }

  public MqttClient mqttClient() {
    return this.mqttClient;
  }

  /*public void addTopicListener(XlinkMqttTopicType topicTemplate,
      PublishMessageListener<? extends CMMessage> listener) {
    if (publishMsgListener.containsKey(topicTemplate) == false) {
      publishMsgListener.put(topicTemplate, listener);
    }
  }*/

 public void addMqttClientListener(MqttClientListener listener) {
   mqttClientListener.add(listener);
 }
 
 public void removeMqttClientListener(MqttClientListener listener) {
   mqttClientListener.remove(listener);
 }
 
 public void notifyMqttConnectionSuccess() {
   for(MqttClientListener listener:mqttClientListener) {
     asyncThreadPool.execute(new Runnable() {
       @Override
       public void run() {
         listener.connectionNotify(mqttClient.getPtpId());
       }
     });
   }
 }
 
 public void notifyMqttDatapointSync(int deviceId,DatapointSyncMessage syncMessage) {
   for(MqttClientListener listener:mqttClientListener) {
     asyncThreadPool.execute(new Runnable() {
       @Override
       public void run() {
         listener.messageDatapointSyncNotify(deviceId, syncMessage);
       }
     });
   }
 }
 
 public void notifyDatapointSet(int deviceId,DatapointSetMessage datapointSetMessage) {
   for(MqttClientListener listener:mqttClientListener) {
     asyncThreadPool.execute(new Runnable() {
       @Override
       public void run() {
         listener.messageDatapointSetNotify(deviceId, datapointSetMessage);
       }
     });
   }
 }
 public void notifyDeviceState(int deviceId,DeviceStateMessage deviceStateMessage) {
   for(MqttClientListener listener:mqttClientListener) {
     asyncThreadPool.execute(new Runnable() {
       @Override
       public void run() {
         listener.messageDeviceStateNotify(deviceId, deviceStateMessage);
       }
     });
   }
 }
 
 public void notifyMqttDatapointSync() {
   
 }

/*  public void removePublishMessageListener(PublishMessageListener<? extends CMMessage> listener) {
    publishMsgListener.remove(listener);
  }*/

  public void removeAllListener() {
    mqttClientListener.clear();
  }

  public String clientId() {
    return mqttClient.clientId();
  }

  public String certId() {
    return mqttClient.certId();
  }

  public int channelId() {
    return mqttClient.getChannel().hashCode();
  }

 /* public void notifyPublishMessageListener(int deviceId, XlinkMqttTopicType topicTemplate,
      CMMessage cmmsg) {
    asyncThreadPool.execute(new Runnable() {
      @Override
      public void run() {
        PublishMessageListener listener = publishMsgListener.get(topicTemplate);
        listener.messageNotify(deviceId, cmmsg); 
      }
    });
   

  }*/

  public void changeConnStateNotConn() {
    synchronized (connectLock) {
      this.connState = ConnState.NotConn;
    }

  }

  public void changeToConnectedState() {
    synchronized (connectLock) {
      this.connState = ConnState.Connected;
    }
  }

  public void changeToClosing() {
    synchronized (connectLock) {
      this.connState = ConnState.Closing;
    }
  }

  public void changeToClosed() {
    synchronized (connectLock) {
      this.connState = ConnState.Closed;
    }
  }

  public boolean isNotConnect() {
    return connState == ConnState.NotConn;
  }

  public boolean isConnected() {
    return connState == ConnState.Connected;
  }

  public boolean isClosing() {
    return connState == ConnState.Closing;
  }

  public boolean isClosed() {
    return connState == ConnState.Closed;
  }

  public void setMqttClient(MqttClient mqttClient) {
    this.mqttClient = mqttClient;
  }

  /**
   * 连接Mqtt服务器
   * 
   * @throws InterruptedException
   */
  public synchronized MqttMessageToken connectServer(String clientId, String certId, String certKey)
      throws InterruptedException, Exception {
    if (isNotConnect()) {
      synchronized (connectLock) {
        this.connState = ConnState.Connecting;
      }
      LogHelper.LOGGER().info("mqtt connection....");
      MqttVersion mqttVersion = MqttVersion.MQTT_3_1_1;
      MqttFixedHeader mqttFixedHeader =
          new MqttFixedHeader(MqttMessageType.CONNECT, false, MqttQoS.AT_MOST_ONCE, false, 0);
      MqttConnectVariableHeader mqttConnectVariableHeader =
          new MqttConnectVariableHeader(mqttVersion.protocolName(), mqttVersion.protocolLevel(),
              true, true, false, 1, false, MqttConfig.IS_CLEAN_SESSION, MqttConfig.KEEP_ALIVE);
      String password = null;
      if (certKey != null) {
        password = MD5Tool.MD5(certId + certKey);
      }
      MqttConnectPayload mqttConnectPayload =
          new MqttConnectPayload(clientId, null, null, certId, password);
      MqttConnectMessage connMsg =
          new MqttConnectMessage(mqttFixedHeader, mqttConnectVariableHeader, mqttConnectPayload);
      MqttMessageToken token = (MqttMessageToken)send(connMsg,MqttTokenType.Mqtt);
      //  MqttConnAckMessage connAckMsg = (MqttConnAckMessage)token.get(10,TimeUnit.SECONDS);
      return token;
    }

    throw new Exception("mqtt is not NotConnect state,no need call [connectServer] method.");

  }

  /**
   * mqtt订阅
   * 
   * @param messageId
   * @param topicFilters
   * @param qos
   * @return
   * @throws InterruptedException
   */
  public MqttMessageToken subscribe(int messageId, String[] topicFilters, int[] qos) {
    List<MqttTopicSubscription> subscriptions = new ArrayList<MqttTopicSubscription>();
    for (int i = 0; i < topicFilters.length; i++) {
      subscriptions.add(new MqttTopicSubscription(topicFilters[i], MqttQoS.valueOf(qos[i])));
    }
    MqttFixedHeader mqttFixedHeader =
        new MqttFixedHeader(MqttMessageType.SUBSCRIBE, false, MqttQoS.AT_LEAST_ONCE, false, 0);
    MqttMessageIdVariableHeader subVarHeader = MqttMessageIdVariableHeader.from(messageId);
    MqttSubscribePayload subPayload = new MqttSubscribePayload(subscriptions);
    MqttSubscribeMessage subMsg =
        new MqttSubscribeMessage(mqttFixedHeader, subVarHeader, subPayload);
    MqttMessageToken token = (MqttMessageToken)send(subMsg,MqttTokenType.Mqtt);
    token.setTopics(topicFilters);
    return token;

  }

  /**
   * mqtt发布消息
   * 
   * @param messageId
   * @param topic
   * @param data
   * @param mqttQos
   * @return
   * @throws InterruptedException
   */
  public MqttCmMessageToken publish(int messageId, String topic, byte[] data, MqttQoS mqttQos) {
    MqttFixedHeader mqttFixedHeader =
        new MqttFixedHeader(MqttMessageType.PUBLISH, false, mqttQos, false, 0);
    MqttPublishVariableHeader pubVarHeader = new MqttPublishVariableHeader(topic, messageId);
    ByteBuf payload = Unpooled.buffer();
    payload.writeBytes(data);
    MqttPublishMessage pubMsg = new MqttPublishMessage(mqttFixedHeader, pubVarHeader, payload);
    LogHelper.LOGGER().info("client {} publish message topic {},messageId is {}",
        mqttClient.getPtpId(), topic, messageId);
    MqttCmMessageToken token = (MqttCmMessageToken)send(pubMsg,MqttTokenType.XLINK_CM);
    token.setTopics(new String[] {topic});
    return token;
  }

  /**
   * 顺序发送消息
   * 
   * @param messageId
   * @param topic
   * @param data
   * @param mqttQos
   * @param sequenceId 同一个id下，保证顺序发送
   * @return
   * @throws InterruptedException
   */
  public MqttCmMessageToken publishSequence(int messageId, String topic, byte[] data, MqttQoS mqttQos,
      int sequenceId) throws InterruptedException {
    MqttFixedHeader mqttFixedHeader =
        new MqttFixedHeader(MqttMessageType.PUBLISH, false, mqttQos, false, 0);
    MqttPublishVariableHeader pubVarHeader = new MqttPublishVariableHeader(topic, messageId);
    ByteBuf payload = Unpooled.buffer();
    payload.writeBytes(data);
    MqttPublishMessage pubMsg = new MqttPublishMessage(mqttFixedHeader, pubVarHeader, payload);
    LogHelper.LOGGER().info("ptp {} publish message topic {},messageId is {}",
        mqttClient.getPtpId(), topic, messageId);
    MqttCmMessageToken token =  (MqttCmMessageToken)sendSequence(sequenceId, pubMsg,MqttTokenType.XLINK_CM);
    token.setTopics(new String[] {topic});
    return token;
  }


  public void disconnection() {
    MqttMessage message = new MqttMessage(
        new MqttFixedHeader(MqttMessageType.DISCONNECT, false, MqttQoS.AT_LEAST_ONCE, false, 0));
    try {
      mqttClient.getChannel().writeAndFlush(message).sync();
      changeToClosing();
    } catch (InterruptedException e) {
      LogHelper.LOGGER().error("send disconnect message failed.", e);
    }
  }

  private MqttToken<?> send(MqttMessage msg,MqttTokenType type) {

    // 如果处于关闭,不继续发送消息。
    if (isClosing() || isClosed()) {
      return MqttTokenManager.instance().createMqttFailedResultToken(
          new XlinkMqttException(XlinkMqttException.MQTT_CLIENT_CLOSED, null),type);
    }
    String messageId = genMqttTokenId(msg);
    MqttToken<?> token = MqttTokenManager.instance().createMqttToken(messageId,type);
    MqttTokenManager.instance().put(token.getMessageID(), token);
    //asyncThreadPool.offer(new MqttSendAsyncQueue(this, msg));
    CompletableFuture.runAsync(()->{
      if (msg.fixedHeader().messageType() != MqttMessageType.CONNECT
          && this.isConnected() == false) {
       
        return ;
      }
      this.sendDataDirectly(msg);
      // 消息发送完成,记数减一
      this.messageCountDecre(1);
    }, asyncThreadPool);
    // 把消息放进异步线程，计数加一
    messageCountAdd(1);
    return token;
  }

  /**
   * 顺序发送消息
   * 
   * @param sequenceId 同一个id下，保证顺序发送
   * @param msg
   * @return
   * @throws InterruptedException
   */
  public MqttToken<?> sendSequence(int sequenceId, MqttMessage msg,MqttTokenType tokenType) throws InterruptedException {

    // 如果处于关闭,不继续发送消息。
    if (isClosing() || isClosed()) {
      return (MqttCmMessageToken)MqttTokenManager.instance().createMqttFailedResultToken(
          new XlinkMqttException(XlinkMqttException.MQTT_CLIENT_CLOSED, null),tokenType);
    }
    String messageId = genMqttTokenId(msg);
    MqttToken<?> token = MqttTokenManager.instance().createMqttToken(messageId,tokenType);
    MqttTokenManager.instance().put(token.getMessageID(), token);
    logicThreadPool.execute(new MqttSendLogicQueue(sequenceId,this, msg));
    // 把消息放进异步线程，计数加一
    messageCountAdd(1);
    return token;
  }


  /**
   * 直接发送Mqtt Message
   * 
   * @param msg
   */
  public void sendDataDirectly(final MqttMessage msg) {
    // 如果客户端处于关闭状态，不发送消息
    if (isClosed()) {

      return;
    }
    mqttClient.getChannel().writeAndFlush(msg)
        .addListener(new GenericFutureListener<ChannelFuture>() {
          @Override
          public void operationComplete(ChannelFuture future) throws Exception {
            // 发送失败
            if (future.isSuccess() == false) {
              LogHelper.LOGGER().warn("mqtt send direct failed.cert id is: {}",
                  MqttComms.this.certId());
              asyncThreadPool.execute(new MqttSendAsyncQueue(MqttComms.this, msg));
              // 把消息放进异步线程，计数加一
              messageCountAdd(1);
            }
          }
        });
  }


  /**
   * 处理接收到的Mqtt消息
   * 
   * @param msg
   * @throws InterruptedException
   */
  public void messageArrive(MqttMessage msg) throws InterruptedException {
    if (msg.fixedHeader().messageType() == MqttMessageType.PUBLISH) {
      ((MqttPublishMessage) msg).payload().retain();
    }
    logicThreadPool.execute(new MqttReceiveLogicQueue(Math.abs(channelId()),this, msg));
    // 把消息放进异步线程，计数加一
    messageCountAdd(1);
  }

  public void messageCountAdd(int num) {
    messageCount.getAndAdd(num);
  }

  public void messageCountDecre(int num) {
    messageCount.getAndAdd(-num);
  }

  /**
   * 为每一个发送的MqttMessage 创建一个Token Id。
   * 
   * @param msg
   * @return
   */
  public String genMqttTokenId(MqttMessage msg) {
    String messageId = null;
    switch (msg.fixedHeader().messageType()) {
      case CONNECT:
        messageId = mqttClient.certId();
        break;
      case PUBLISH: {
        MqttPublishMessage pubMsg = (MqttPublishMessage) msg;
        XlinkMqttTopicType topic = XlinkMqttTopicType.fromType(pubMsg.variableHeader().topicName());
        messageId = topic.getTokenId(mqttClient.certId(), pubMsg);
      }
        break;
      case SUBSCRIBE:
        messageId =
            StringTools.getString(((MqttSubscribeMessage) msg).variableHeader().messageId());
        break;
      case UNSUBSCRIBE:
        messageId =
            StringTools.getString(((MqttUnsubscribeMessage) msg).variableHeader().messageId());
        break;
      default:
        messageId = "";
    }
    return messageId;
  }
}

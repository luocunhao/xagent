package xlink.mqtt.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
import xlink.cm.message.DatapointSetMessage;
import xlink.cm.message.DeviceStateMessage;
import xlink.core.utils.ContainerGetter;
import xlink.core.utils.MD5Tool;
import xlink.core.utils.StringTools;
import xlink.mqtt.client.Future.DatapointSetListener;
import xlink.mqtt.client.Future.DeviceStateListener;
import xlink.mqtt.client.config.MqttConfig;
import xlink.mqtt.client.exception.XlinkMqttException;
import xlink.mqtt.client.queue.subclass.MqttConnAckAsyncQueue;
import xlink.mqtt.client.queue.subclass.MqttDatapointSetNotifyQueue;
import xlink.mqtt.client.queue.subclass.MqttReceiveLogicQueue;
import xlink.mqtt.client.queue.subclass.MqttSendAsyncQueue;
import xlink.mqtt.client.queue.subclass.MqttSendLogicQueue;
import xlink.mqtt.client.thread.AsyncThreadPool;
import xlink.mqtt.client.thread.LogicThreadPool;
import xlink.mqtt.client.token.MqttToken;
import xlink.mqtt.client.token.MqttTokenManager;
import xlink.mqtt.client.type.XlinkMqttTopicType;
import xlink.mqtt.client.utils.LogHelper;

public class MqttComms {

  private MqttClient mqttClient;
  private Object connectLock = new Object();
  private AsyncThreadPool asyncThreadPool;
  private LogicThreadPool logicThreadPool;
  private List<DatapointSetListener> dpSetListener;
  private List<DeviceStateListener> deviceOnlineResultListener;
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
    dpSetListener = ContainerGetter.arrayList();
    deviceOnlineResultListener = ContainerGetter.arrayList();
    messageCount = new AtomicInteger();
  }

  public MqttClient mqttClient() {
    return this.mqttClient;
  }

  public void addDatapointSetListener(DatapointSetListener listener) {
    dpSetListener.add(listener);
  }

  public void addDeviceOnlineResultListener(DeviceStateListener listener) {
    deviceOnlineResultListener.add(listener);
  }

  public void removeDatapointSetListener(DatapointSetListener listener) {
    dpSetListener.remove(listener);
  }

  public void removeAllListener() {
    dpSetListener.clear();
  }

  public String clientId() {
    return mqttClient.clientId();
  }

  public int channelId() {
    return mqttClient.getChannel().hashCode();
  }

  public String mqttClientId() {
	  return mqttClient.getMqttClientId();
  }
  
  public void notifyDatapiontSetMessage(int deviceId, DatapointSetMessage dpSetMsg) {
    for (DatapointSetListener listener : dpSetListener) {
      try {
        asyncThreadPool.offer(new MqttDatapointSetNotifyQueue(listener,deviceId,dpSetMsg));
      } catch (InterruptedException e) {
        LogHelper.LOGGER().error("MqttComms notifyDatapiontSetMessage error", e);
      }
     // listener.messageNotify(deviceId, dpSetMsg);
    }
  }

  public void notifyDeviceStateMessage(int deviceId,
      DeviceStateMessage deviceStateMsg) {
    for (DeviceStateListener listener : deviceOnlineResultListener) {
      listener.messageNotify(deviceId, deviceStateMsg);
    }
  }

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
  public synchronized boolean connectServer(String certId, String certKey)
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
      String password = MD5Tool.MD5(certId + certKey);
      MqttConnectPayload mqttConnectPayload =
          new MqttConnectPayload(mqttClient.getMqttClientId(), null, null, certId, password);

      MqttConnectMessage connMsg =
          new MqttConnectMessage(mqttFixedHeader, mqttConnectVariableHeader, mqttConnectPayload);
      // MqttToken token = send(connMsg).waitforResponse(60 * 1000);
      String messageId = genMqttTokenId(connMsg);
      MqttToken token = MqttTokenManager.instance().createMqttToken(messageId);
      token.setMessage(connMsg);
      MqttTokenManager.instance().put(token.getMessageID(), token);
      sendDataDirectly(connMsg);
      token.waitforResponse(5 * 1000);
      if (token.getException() != null) {
        // 如果是连接超时,重新连接。
        if (token.getException().getReasonCode() == XlinkMqttException.MQTT_CONNECTION_TIMEOUT) {
          connectServer(certId, certKey);
          // 如果是验证失败，返回false
        } else if (token.getException()
            .getReasonCode() == XlinkMqttException.MQTT_CONNECT_AUTH_FAILED) {
          return false;
        }
      }

      // if (token.isSuccess()) {
        this.asyncThreadPool.offer(new MqttConnAckAsyncQueue(this.mqttClient));
      // }
      return token.isSuccess();

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
  public MqttToken subscribe(int messageId, String[] topicFilters, int[] qos)
      throws InterruptedException {
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
    return send(subMsg);

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
  public MqttToken publish(int messageId, String topic, byte[] data, MqttQoS mqttQos)
      throws InterruptedException {
    MqttFixedHeader mqttFixedHeader =
        new MqttFixedHeader(MqttMessageType.PUBLISH, false, mqttQos, false, 0);
    MqttPublishVariableHeader pubVarHeader = new MqttPublishVariableHeader(topic, messageId);
    ByteBuf payload = Unpooled.buffer();
    payload.writeBytes(data);
    MqttPublishMessage pubMsg = new MqttPublishMessage(mqttFixedHeader, pubVarHeader, payload);
    LogHelper.LOGGER().info("ptp {} publish message topic {},messageId is {}",
        mqttClient.getPtpId(), topic, messageId);
    return send(pubMsg);
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
  public MqttToken publishSequence(int messageId, String topic, byte[] data, MqttQoS mqttQos,
      int sequenceId) throws InterruptedException {
    MqttFixedHeader mqttFixedHeader =
        new MqttFixedHeader(MqttMessageType.PUBLISH, false, mqttQos, false, 0);
    MqttPublishVariableHeader pubVarHeader = new MqttPublishVariableHeader(topic, messageId);
    ByteBuf payload = Unpooled.buffer();
    payload.writeBytes(data);
    MqttPublishMessage pubMsg = new MqttPublishMessage(mqttFixedHeader, pubVarHeader, payload);
    LogHelper.LOGGER().info("ptp {} publish message topic {},messageId is {}",
        mqttClient.getPtpId(), topic, messageId);
    return sendSequence(sequenceId, pubMsg);
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

  public MqttToken send(MqttMessage msg) throws InterruptedException {

    // 如果处于关闭,不继续发送消息。
    if (isClosing() || isClosed()) {
      return MqttTokenManager.instance().createFailedResultToken(
          new XlinkMqttException(XlinkMqttException.MQTT_CLIENT_CLOSED, null));
    }
    String messageId = genMqttTokenId(msg);
    MqttToken token = MqttTokenManager.instance().createMqttToken(messageId);
    token.setMessage(msg);
    MqttTokenManager.instance().put(token.getMessageID(), token);
    asyncThreadPool.offer(new MqttSendAsyncQueue(this, msg));
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
  public MqttToken sendSequence(int sequenceId, MqttMessage msg) throws InterruptedException {

    // 如果处于关闭,不继续发送消息。
    if (isClosing() || isClosed()) {
      return MqttTokenManager.instance().createFailedResultToken(
          new XlinkMqttException(XlinkMqttException.MQTT_CLIENT_CLOSED, null));
    }
    String messageId = genMqttTokenId(msg);
    MqttToken token = MqttTokenManager.instance().createMqttToken(messageId);
    token.setMessage(msg);
    MqttTokenManager.instance().put(token.getMessageID(), token);
    logicThreadPool.offer(sequenceId, new MqttSendLogicQueue(this, msg));
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
              LogHelper.LOGGER().warn("mqtt send direct failed.client id is: %s",
                  MqttComms.this.clientId());
              asyncThreadPool.offer(new MqttSendAsyncQueue(MqttComms.this, msg));
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
    logicThreadPool.offer(Math.abs(channelId()), new MqttReceiveLogicQueue(this, msg));
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
        messageId = mqttClient.clientId();
        break;
      case PUBLISH: {
        MqttPublishMessage pubMsg = (MqttPublishMessage) msg;
        XlinkMqttTopicType topic = XlinkMqttTopicType.fromType(pubMsg.variableHeader().topicName());
        messageId = topic.getTokenId(mqttClient.clientId(), pubMsg);
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

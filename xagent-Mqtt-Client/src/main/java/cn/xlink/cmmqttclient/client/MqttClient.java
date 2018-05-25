/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License, version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package cn.xlink.cmmqttclient.client;


import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLException;

import cn.xlink.cmmqttclient.Future.DatapointSetListener;
import cn.xlink.cmmqttclient.Future.DatapointSyncListener;
import cn.xlink.cmmqttclient.Future.DeviceStateListener;
import cn.xlink.cmmqttclient.Future.MqttClientListener;
import cn.xlink.cmmqttclient.config.MqttConfig;
import cn.xlink.cmmqttclient.core.utils.LogHelper;
import cn.xlink.cmmqttclient.handler.PingReqHandler;
import cn.xlink.cmmqttclient.thread.AsyncThreadPool;
import cn.xlink.cmmqttclient.thread.LogicThreadPool;
import cn.xlink.cmmqttclient.token.MqttCmMessageToken;
import cn.xlink.cmmqttclient.token.MqttMessageToken;
import cn.xlink.cmmqttclient.token.MqttToken;
import cn.xlink.cmmqttclient.type.XlinkMqttTopicType;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.IdleStateHandler;



/**
 * Sends one message when a connection is open and echoes back any received data to the server.
 * Simply put, the echo client initiates the ping-pong traffic between the echo client and server by
 * sending the first message to the server.
 */
public final class MqttClient {
  private Channel channel;
  private AsyncThreadPool asyncThreadPool;
  private LogicThreadPool logicThreadPool;
  private EventLoopGroup nettyEventgroup;
  private MqttComms mqttComms;
  private String ptpId;
  private String clientId;
  private String certId;
  private String certKey;
  private Bootstrap boostrap;
  private boolean isShutDown = false;
  private final Object connLock = new Object();
  private boolean isMqttAuth = false;
  private MqttMessageToken connectToken = null;
 


  protected MqttClient(AsyncThreadPool asyncThreadPool, LogicThreadPool logicThreadPool,
      EventLoopGroup nettyEventgroup, String certId, String certKey, String ptpId,
      String clientId) {
    this.asyncThreadPool = asyncThreadPool;
    this.logicThreadPool = logicThreadPool;
    this.nettyEventgroup = nettyEventgroup;
    this.clientId = clientId;
    this.certId = certId;
    this.certKey = certKey;
    this.ptpId = ptpId;
    this.mqttComms = new MqttComms(this, this.asyncThreadPool, this.logicThreadPool);


    try {
      boostrap = new Bootstrap();
      createBootstrap(boostrap, this.nettyEventgroup, MqttConfig.IS_SSL);
      synchronized (connLock) {
        connLock.wait();
      }
    } catch (Exception e) {
      LogHelper.LOGGER().error("creation of mqtt client failed.", e);
    }
  }

  public Bootstrap createBootstrap(Bootstrap b, EventLoopGroup group, boolean isSSl)
      throws SSLException {
    final SslContext sslCtx;
    if (isSSl) {
      sslCtx =
          SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
    } else {
      sslCtx = null;
    }
    b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
        .handler(new ChannelInitializer<SocketChannel>() {
          @Override
          public void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline p = ch.pipeline();
            if (sslCtx != null) {
              p.addLast(
                  sslCtx.newHandler(ch.alloc(), MqttConfig.BROKER_HOST, MqttConfig.BROKER_PORT));
            }
            p.addFirst("idelstate",
                new IdleStateHandler(0, (MqttConfig.KEEP_ALIVE * 2) / 3,
                    (MqttConfig.KEEP_ALIVE * 2) / 3))
                .addLast("ping", new PingReqHandler()).addLast("decoder", new MqttDecoder())
                .addLast("encoder", MqttEncoder.INSTANCE).addLast(new MqttMessageReceiveChannel());
          }
        });

    // Start the client.
      b.connect(MqttConfig.BROKER_HOST, MqttConfig.BROKER_PORT)
          .addListener(new ConnectionListener(this));
    return b;
  }

  public void destroy() {
    synchronized (this) {
      this.isShutDown = true;
    }
    mqttComms.removeAllListener();
    int channelId = this.channel.hashCode();
    mqttComms.disconnection();
    try {
      channel.close().sync();
    } catch (InterruptedException e) {
      LogHelper.LOGGER().error(e, "client {} channel %d closed failed.", this.certId, channelId);
    } finally {
      mqttComms.changeToClosed();
      LogHelper.LOGGER().info("client {} channel %d closed.", this.certId, channelId);
    }
    mqttComms.setMqttClient(null);
    mqttComms = null;
  }

  public Channel getChannel() {
    return channel;
  }
  
  public boolean syncAndGetConnResult(){
    try {
      MqttConnAckMessage connAckMsg = (MqttConnAckMessage)this.connectToken.get(10,TimeUnit.SECONDS);
      return connAckMsg.variableHeader().connectReturnCode() == MqttConnectReturnCode.CONNECTION_ACCEPTED;
    }catch(Exception e) {
      LogHelper.LOGGER().error("syncAndGetConnResult error,", e);;
      this.channel.disconnect();
      return false;
    }
   
 
  }

  public String clientId() {
    return this.clientId;
  }

  public String certId() {
    return this.certId;
  }

  public String getPtpId() {
    return ptpId;
  }

  public boolean isMqttAuth() {
    return this.isMqttAuth;
  }
  
  public int socketId() {
    return this.channel.hashCode();
  }

/*  public void addDatapointSetListener(DatapointSetListener listeners) {
    mqttComms.addTopicListener(XlinkMqttTopicType.DEVICE_DATAPOINT_SET, listeners);
  }

  public void addDeviceStateListener(DeviceStateListener listener) {
    mqttComms.addTopicListener(XlinkMqttTopicType.DEVICE_STATE, listener);
  }

  public void addDeviceSyncListener(DatapointSyncListener listener) {
    mqttComms.addTopicListener(XlinkMqttTopicType.DEVICE_DATAPOINT_SYNC, listener);
  }*/
  
  public void addMqttClientListener(MqttClientListener listener) {
    mqttComms.addMqttClientListener(listener);
  }

  public MqttMessageToken subscribe(int messageId, String[] topicFilters, int[] qos) {
    return mqttComms.subscribe(messageId, topicFilters, qos);
  }

  public MqttCmMessageToken publish(final int messageId, String topic, byte[] payload, int qos) {
    return mqttComms.publish(messageId, topic, payload, MqttQoS.valueOf(qos));
  }

  /**
   * 顺序发布消息
   * 
   * @param messageId
   * @param topic
   * @param payload
   * @param qos
   * @param sequenceId 同一个id下，保证顺序发送
   * @return
   * @throws InterruptedException
   */
  public MqttCmMessageToken publishSequence(final int messageId, String topic, byte[] payload, int qos,
      int sequenceId) throws InterruptedException {
    return mqttComms.publishSequence(messageId, topic, payload, MqttQoS.valueOf(qos), sequenceId);
  }

  class ConnectionListener implements ChannelFutureListener {
    private MqttClient client;

    public ConnectionListener(MqttClient client) {
      this.client = client;
    }

    @Override
    public void operationComplete(ChannelFuture channelFuture) throws Exception {
      if (channelFuture.isSuccess()) {
        client.channel = channelFuture.channel();
        client.connectToken =
            mqttComms.connectServer(MqttClient.this.clientId, MqttClient.this.certId,
                MqttClient.this.certKey);
        synchronized (client.connLock) {
          client.connLock.notifyAll();

        }

      } else {
        LogHelper.LOGGER().warn("tcp connection failed,certId is {}, try to reconnect",this.client.certId);
        final EventLoop loop = channelFuture.channel().eventLoop();
        loop.schedule(new Runnable() {

          @Override
          public void run() {

            try {
              client.createBootstrap(new Bootstrap(), loop, false);
            } catch (SSLException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }

          }

        }, 5L, TimeUnit.SECONDS);
      }


    }


  }

  public class MqttMessageReceiveChannel extends SimpleChannelInboundHandler<MqttMessage> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
      // LogHelper.LOGGER().info("channel active .....");
      // mqttComms.connectServer(MqttClient.this.certId, MqttClient.this.certKey);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MqttMessage msg) throws Exception {
      LogHelper.LOGGER().debug("certId {} receive mqtt server data, type is {}",MqttClient.this.certId, msg.fixedHeader().messageType());
      mqttComms.messageArrive(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
      ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
      cause.printStackTrace();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      // 如果标记为关闭,则不要重连
      if (MqttClient.this.isShutDown) {
        return;
      }

      if (MqttClient.this.channel.hashCode() != ctx.channel().hashCode()) {
        LogHelper.LOGGER().info(
            "[channel inactive] mqttclient socket is {}, no match {}, ignore it.",
            MqttClient.this.channel.hashCode(), ctx.channel().hashCode());
        return;
      }
      mqttComms.removeAllListener();
      LogHelper.LOGGER().info("certId {} connection lost,socket is {}.try to reconnect... ",MqttClient.this.certId,ctx.channel().hashCode());
      mqttComms.changeConnStateNotConn();
      final EventLoop eventLoop = ctx.channel().eventLoop();
      eventLoop.schedule(new Runnable() {
        @Override
        public void run() {
          try {
            MqttClient.this.createBootstrap(new Bootstrap(), eventLoop, MqttConfig.IS_SSL);
          } catch (SSLException e) {
            e.printStackTrace();
          }

        }
      }, 5L, TimeUnit.SECONDS);
      super.channelInactive(ctx);
    }
  }
}

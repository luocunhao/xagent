package xlink.mqtt.client.token;

import java.util.List;

import io.netty.handler.codec.mqtt.MqttMessage;
import xlink.cm.message.CMMessage;
import xlink.core.utils.ContainerGetter;
import xlink.mqtt.client.Future.IMqttActionListener;
import xlink.mqtt.client.exception.XlinkMqttException;
import xlink.mqtt.client.utils.LogHelper;

public class MqttToken {

  private boolean isCompleted = false;
  private boolean isSuccess = false;
  private MqttMessage message;
  private String[] topics = null;
  private List<IMqttActionListener> callbackListener;
  private long createdTimeStamp;

  private Object responseLock = new Object();
  private String messageID = null;
  private XlinkMqttException exception = null;
  private CMMessage responseMsg = null;

  protected MqttToken(String messageID) {
    super();
    this.messageID = messageID;
    callbackListener = ContainerGetter.arrayList();
    this.createdTimeStamp = System.currentTimeMillis();
  }
  
  public MqttToken waitforResponse(long timeout) {
    synchronized (responseLock) {

      while (this.isCompleted == false) {
        try {
          if (timeout < 0) {
            responseLock.wait();
          } else {
            responseLock.wait(timeout);
          }

        } catch (InterruptedException e) {
          this.exception = new XlinkMqttException(XlinkMqttException.MQTT_CONNECTION_TIMEOUT, e);
        }

        if (this.isCompleted == false) {
          if (this.exception != null) {
            LogHelper.LOGGER().error("mqtt token timeout", this.exception);
          }
        }
        if (timeout > 0) {
          break;
        }
      }
    }
    return this;
  }
  
  public void setCallbackListener(IMqttActionListener callback) {
    callbackListener.add(callback);
  }

  /**
   * 标记请求处理完成
   * 
   * @param isSuccess
   */
  public void markComplete(boolean isSuccess) {
    this.isCompleted = true;
    this.isSuccess = isSuccess;
    synchronized (responseLock) {
      responseLock.notifyAll();
    }
    /*
     * if (message.fixedHeader().messageType() == MqttMessageType.PUBLISH) { MqttPublishMessage
     * pubMsg = (MqttPublishMessage) message; try { this.responseMsg =
     * CMMessage.funcParseMessage(XlinkPublishMessageFactory.version, pubMsg.payload()); } catch
     * (Exception e) { this.isSuccess = false; this.exception = new
     * XlinkMqttException(XlinkMqttException.XLINK_CM_MESSAGE_PHASE_ERROR, e); } }
     */
    for (IMqttActionListener listener : callbackListener) {
      if (isSuccess) {
        listener.onSucess(this);
      } else {
        listener.onFailure(this, this.exception);
      }
    }

  }

  public boolean isCompleted() {
    return isCompleted;
  }

  public void setCompleted(boolean isCompleted) {
    this.isCompleted = isCompleted;
  }

  public boolean isSuccess() {
    return isSuccess;
  }

  public void setSuccess(boolean isSuccess) {
    this.isSuccess = isSuccess;
  }

  public MqttMessage getMessage() {
    return message;
  }

  public void setMessage(MqttMessage message) {
    this.message = message;
  }

  public String[] getTopics() {
    return topics;
  }

  public void setTopics(String[] topics) {
    this.topics = topics;
  }

  public String getMessageID() {
    return messageID;
  }

  public void setMessageID(String messageID) {
    this.messageID = messageID;
  }

  /*
   * public IMqttActionListener getCallbackListener() { return callbackListener; }
   */

  public XlinkMqttException getException() {
    return exception;
  }

  public void setException(XlinkMqttException exception) {
    this.exception = exception;
  }

  public CMMessage getResponseMsg() {
    return responseMsg;
  }

  public void setResponseMsg(CMMessage responseMsg) {
    this.responseMsg = responseMsg;
  }

  public long getCreatedTimeStamp() {
    return createdTimeStamp;
  }

  public void setCreatedTimeStamp(long createdTimeStamp) {
    this.createdTimeStamp = createdTimeStamp;
  }



}

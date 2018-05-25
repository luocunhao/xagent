package cn.xlink.cmmqttclient.token;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import cn.xlink.cmmqttclient.Future.IMqttActionListener;
import cn.xlink.cmmqttclient.core.utils.ContainerGetter;
import cn.xlink.cmmqttclient.exception.XlinkMqttException;

public abstract class MqttToken<T> extends CompletableFuture<T>{

  private String[] topics = null;
  private List<IMqttActionListener> callbackListener;
  private long createdTimeStamp;

  private String messageID = null;


  protected MqttToken(String messageID) {
    super();
    this.messageID = messageID;
    callbackListener = ContainerGetter.arrayList();
    this.createdTimeStamp = System.currentTimeMillis();
  }
  
  public void setCallbackListener(IMqttActionListener callback) {
    callbackListener.add(callback);
  }

  /**
   * 标记请求处理完成
   * 
   * @param isSuccess
   */
  public void markComplete(T result) {
    this.complete(result);
    if(callbackListener != null) {
      for (IMqttActionListener listener : callbackListener) {
        listener.onSucess(this);  
    }
    }
   
  }
  /**
   * 以抛出exception的方式完成任务
   * @param exception
   */
  public void completeException(XlinkMqttException exception) {
    if(callbackListener != null) {
      for (IMqttActionListener listener : callbackListener) {
        listener.onFailure(this,exception );      
      }
    }
    super.completeExceptionally(exception);
    
  }
  
  /**
   * 获取返回结果
   */
  public T getResult() throws InterruptedException, ExecutionException  {
    return super.get();
  }
  
  /**
   * 获取返回结果
   */
  public T getResult(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException  {
    return super.get(timeout, unit);
  }
  /**
   * 处理错误情况
   * @param reasonCode
   * @param exception
   * @return
   */
  public MqttToken<T> exceptionHandler(int reasonCode,Throwable exception) {
      super.exceptionally(t->{
        for (IMqttActionListener listener : callbackListener) {
          listener.onFailure(this, new XlinkMqttException(reasonCode,t));      
        }
        return null;
      });
      return this;
  }
  
  public boolean isCompleted() {
    return super.isDone();
  }


  public boolean isSuccess() {
    return super.isCompletedExceptionally() == false;
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


  public long getCreatedTimeStamp() {
    return createdTimeStamp;
  }

  public void setCreatedTimeStamp(long createdTimeStamp) {
    this.createdTimeStamp = createdTimeStamp;
  }



}

package xlink.cm.ptp.future.impl;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import xlink.cm.agent.ptp.PtpFuture;
import xlink.cm.agent.ptp.PtpFutureListener;
import xlink.core.utils.ContainerGetter;
import xlink.mqtt.client.token.MqttToken;

public abstract class AbstractPtpFuture<V> implements PtpFuture<V> {
  protected MqttToken mqttToken;

  private List<PtpFutureListener<? extends Future<? super V>>> actionList;

  public AbstractPtpFuture(MqttToken mqttToken) {
    this.mqttToken = mqttToken;
    actionList = ContainerGetter.arrayList();
  }

  @Override
  public boolean cancel(boolean mayInterruptIfRunning) {
    return false;
  }

  @Override
  public boolean isCancelled() {
    return false;
  }

  @Override
  public boolean isDone() {
    return mqttToken.isCompleted();
  }

  @Override
  public boolean isSuccess() {
    return mqttToken.isCompleted();
  }

  @Override
  public Throwable cause() {
    // TODO Auto-generated method stub
    return mqttToken.getException();
  }

  @Override
  public PtpFuture<V> addListener(PtpFutureListener<? extends Future<? super V>> listener) {
    actionList.add(listener);
    return this;
  }

  @Override
  public PtpFuture<V> addListeners(PtpFutureListener<? extends Future<? super V>>... listeners) {
    Collections.addAll(actionList, listeners);
    return this;
  }

  @Override
  public PtpFuture<V> removeListener(PtpFutureListener<? extends Future<? super V>> listener) {
    actionList.remove(listener);
    return this;
  }

  @Override
  public PtpFuture<V> removeListeners(PtpFutureListener<? extends Future<? super V>>... listeners) {
    for (PtpFutureListener<? extends Future<? super V>> listener : listeners) {
      actionList.remove(listener);
    }
    return this;
  }

  @Override
  public PtpFuture<V> sync() throws InterruptedException {
    mqttToken.waitforResponse(-1);
    return this;
  }
}

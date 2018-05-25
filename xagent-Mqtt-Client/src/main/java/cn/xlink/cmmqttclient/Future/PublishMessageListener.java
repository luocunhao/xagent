package cn.xlink.cmmqttclient.Future;

import xlink.cm.message.CMMessage;

public interface PublishMessageListener<T extends CMMessage> {

  public void messageNotify(int deviceId, T message);
}

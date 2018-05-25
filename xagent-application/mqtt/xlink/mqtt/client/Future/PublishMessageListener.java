package xlink.mqtt.client.Future;

public interface PublishMessageListener<T> {

  public void messageNotify(int deviceId, T message);
}

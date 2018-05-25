package xlink.mqtt.client.token;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import xlink.mqtt.client.exception.XlinkMqttException;

public class MqttTokenManager {


  private static final MqttTokenManager singleton = new MqttTokenManager();

  private Map<String, ConcurrentLinkedQueue<MqttToken>> tokenMap;

  private MqttTokenManager() {
    tokenMap = new ConcurrentHashMap<String, ConcurrentLinkedQueue<MqttToken>>();
  }


  public static MqttTokenManager instance() {
    return singleton;
  }

  public void put(String key, MqttToken token) {
    
    ConcurrentLinkedQueue<MqttToken> tokenList = tokenMap.get(key);
    if (tokenList == null) {
      tokenList = new ConcurrentLinkedQueue<MqttToken>();
      ConcurrentLinkedQueue<MqttToken> tokenList1 = tokenMap.putIfAbsent(key, tokenList);
      if (tokenList1 != null) {
        tokenList = tokenList1;
      }

    }
    tokenList.add(token);
    // tokenMap.put(key, token);
  }

  public MqttToken remove(String key) {
    if (tokenMap.containsKey(key) == false) {
      return null;
    }

    MqttToken token = tokenMap.get(key).poll();
    ConcurrentLinkedQueue<MqttToken> tokens = tokenMap.get(key);
    if (tokens.size() == 0) {
      tokenMap.remove(key);
    }
    // return tokenMap.remove(key);
    return token;
  }

  public MqttToken get(String key) {
    // return tokenMap.get(Key);
    if (tokenMap.containsKey(key) == false) {
      return null;
    }

    return tokenMap.get(key).peek();
  }

  public List<MqttToken> getAllToken() {
    List<MqttToken> tokenList = new ArrayList<MqttToken>();
    // tokenList.addAll(tokenMap.values());
    for (ConcurrentLinkedQueue<MqttToken> tokenQueue : tokenMap.values()) {
      tokenList.addAll(tokenQueue);
    }
    return tokenList;
  }

  public MqttToken createMqttToken(String tokenId) {
    return new MqttToken(tokenId);
  }

  public MqttToken createFailedResultToken(XlinkMqttException exception) {
    MqttToken token = new MqttToken("");
    token.markComplete(false);
    token.setException(exception);
    return token;
  }
}

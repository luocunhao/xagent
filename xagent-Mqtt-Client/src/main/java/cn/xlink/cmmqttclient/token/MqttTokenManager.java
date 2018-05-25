package cn.xlink.cmmqttclient.token;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import cn.xlink.cmmqttclient.core.utils.LogHelper;
import cn.xlink.cmmqttclient.exception.XlinkMqttException;

public class MqttTokenManager {


  private static final MqttTokenManager singleton = new MqttTokenManager();

  private Map<String, ConcurrentLinkedQueue<MqttToken<?>>> tokenMap;

  private MqttTokenManager() {
    tokenMap = new ConcurrentHashMap<String, ConcurrentLinkedQueue<MqttToken<?>>>();
  }


  public static MqttTokenManager instance() {
    return singleton;
  }

  public void put(String key, MqttToken<?> token) {
    
    ConcurrentLinkedQueue<MqttToken<?>> tokenList = tokenMap.get(key);
    if (tokenList == null) {
      tokenList = new ConcurrentLinkedQueue<MqttToken<?>>();
      ConcurrentLinkedQueue<MqttToken<?>> tokenList1 = tokenMap.putIfAbsent(key, tokenList);
      if (tokenList1 != null) {
        tokenList = tokenList1;
      }

    }
    tokenList.add(token);
    // tokenMap.put(key, token);
  }

  public MqttToken<?> remove(String key) {
    if (tokenMap.containsKey(key) == false) {
      return null;
    }

    MqttToken<?> token = tokenMap.get(key).poll();
    ConcurrentLinkedQueue<MqttToken<?>> tokens = tokenMap.get(key);
    if (tokens.size() == 0) {
      tokenMap.remove(key);
    }
    // return tokenMap.remove(key);
    return token;
  }

  public MqttToken<?> get(String key) {
    // return tokenMap.get(Key);
    if (tokenMap.containsKey(key) == false) {
      return null;
    }

    return tokenMap.get(key).peek();
  }

  public List<MqttToken<?>> getAllToken() {
    List<MqttToken<?>> tokenList = new ArrayList<MqttToken<?>>();
    // tokenList.addAll(tokenMap.values());
    for (ConcurrentLinkedQueue<MqttToken<?>> tokenQueue : tokenMap.values()) {
      tokenList.addAll(tokenQueue);
    }
    return tokenList;
  }

  public MqttMessageToken createMqtToken(String tokenId,MqttTokenType type) {
    return new MqttMessageToken(tokenId);
  }
  
  public MqttToken<?> createMqttToken(String tokenId,MqttTokenType type){
    switch(type) {
      case Mqtt:{
        MqttMessageToken token = new MqttMessageToken(tokenId);
        return token;
      }
      case XLINK_CM :{
        MqttCmMessageToken token = new MqttCmMessageToken(tokenId);
        return token;
      }
      default:
        LogHelper.LOGGER().info("MqttTokenManager createMqttFailedResultToken type {} is illegal", type);
        throw new IllegalArgumentException("MqttTokenManager token type is illegal.");
    }
  }
  

  public MqttToken<?> createMqttFailedResultToken(XlinkMqttException exception,MqttTokenType type) {
    String tokenId = "";
    switch(type) {
      case Mqtt :{
        MqttMessageToken token = new MqttMessageToken(tokenId);
        token.completeException(exception);
        return token;
      }
      case XLINK_CM:{
        MqttCmMessageToken token = new MqttCmMessageToken(tokenId);
        token.completeException(exception);
        return token;
      }
      default:
        LogHelper.LOGGER().info("MqttTokenManager createMqttFailedResultToken type {} is illegal", type);
        throw new IllegalArgumentException("MqttTokenManager token type is illegal.");
    }
   
   
   
  }
}

package xlink.xagent.ptp.zr.codec;

public enum ZrMessageType {

  Unkwon(-1),

  Auth(1),

  Offline(2),

  Datapoint(3),

  HeartBeat(4),

  ;



  private int type;

  private ZrMessageType(int type) {
    this.type = type;
  }

  public int type() {
    return this.type;
  }

  public static ZrMessageType fromType(int type) {
    for (ZrMessageType msgType : values()) {
      if (msgType.type() == type) {
        return msgType;
      }
    }
    return Unkwon;
  }



}

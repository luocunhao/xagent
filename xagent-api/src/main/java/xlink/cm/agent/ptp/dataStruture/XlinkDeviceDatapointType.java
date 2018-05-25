package xlink.cm.agent.ptp.dataStruture;


/**
 * 设备端的数据端点数据类型
 * 
 * @author xlink
 */
public enum XlinkDeviceDatapointType {


  /**
   * 未知类型
   */
  Unkown(-1),
  /**
   * 布尔类型或者单字节
   */
  BoolByte(0),
  /**
   * 单字节
   */
  Int16(1),
  /**
   * 16位短整型
   */
  UnSignedInt16(2),
  /**
   * 32位整型
   */
  Int32(3),
  /**
   * 无符号32位整形
   */
  UnsignedInt32(4),
  /**
   * 64位整形
   */
  Int64(5),
  /**
   * 无符号64位
   */
  UnsignedInt64(6),
  /**
   * Float(IEEE754标准)
   */
  Float(7),
  /**
   * Double(IEEE754标准)
   */
  Double(8),
  /**
   * 字符串
   */
  String(9),
  /**
   * 字节数组
   */
  Bynary(10);

  private final int type;

  private XlinkDeviceDatapointType(int type) {
    this.type = type;

  }

  public int type() {
    return type;
  }


  public static final XlinkDeviceDatapointType fromType(int type) {
    for (XlinkDeviceDatapointType datapointDataType : values()) {
      if (datapointDataType.type == type) {
        return datapointDataType;
      }
    }
    return Unkown;
  }
}

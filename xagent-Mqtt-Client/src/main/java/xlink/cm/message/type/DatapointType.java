package xlink.cm.message.type;
/**
 * 
 * @author shenweiran
 * create at 2017年4月12日 下午4:45:35
 */
/**
 * | 设备数据类型（16进制） | 类型（高字节在前）   |
 * |--------------------|----------------------|
 * | 0                  | Byte或者BOOL         |
 * | 1                  | Int16                |
 * | 2                  | Unsigned Int16       |
 * | 3                  | Int32                |
 * | 4                  | Unsigned Int32       |
 * | 5                  | Int64                |
 * | 6                  | Unsigned Int64       |
 * | 7                  | Float(IEEE754标准)   |
 * | 8                  | Double(IEEE754标准)  |
 * | 9                  | String               |
 * | A                  | Binary(二进制数据块) |
 * | B-F保留            |                      |
 */
public enum DatapointType {
	/**
	 * 未知类型
	 */
	Unkown(-1, -1),
	/**
	 * 单字节
	 */
    Byte(2, 0),
	/**
	 * 16位短整型
	 */
	Short(3, 1),
	
	/**
	 * 32位整形
	 */
	Int(4, 3),
	/**
	 * 浮点
	 */
	Float(5, 7),
	/**
	 * 字符串
	 */
	String(6, 9 ), 
	/**
	 * 字节数组
	 */
	ByteArray(7, 0xa),
	/**
	 * 16位短整形 无符号
	 */
	UnsignedShort(8, 2),
	/**
	 * 32位整型无符号
	 */
	UnsignedInt(9, 4)
	;

	private int type;
	
	private int deviceType;

	private DatapointType(int type, int deviceType) {
		this.type = type;
		this.deviceType = deviceType;
	}

	public int type() {
		return type;
	}
	
	public int device_type(){
		return deviceType;
	}
	
	public static final DatapointType fromType(int type) {
		for (DatapointType datapointDataType : values()) {
			if (datapointDataType.type == type) {
				return datapointDataType;
			}
		}
		return Unkown;
	}
	
  public static final DatapointType fromDeviceType(int type) {
    for (DatapointType datapointDataType : values()) {
      if (datapointDataType.deviceType == type) {
        return datapointDataType;
      }
    }
    return Unkown;
  }

	@Override
	public java.lang.String toString() {
		return "[type:" + type +"]";
	}
}

package xlink.cm.message.type;

public enum DeviceLogType {

	/**
	 * 设备上线
	 */
	Online(1),
	/**
	 * 设备下线
	 */
	Offline(2),
	/**
	 * 发送ping
	 */
	Ping(3),
	/**
	 * 接收设置
	 */
	RecvSet(4),
	/**
	 * 返回设置
	 */
	RespSet(5),
	/**
	 * 接收订阅
	 */
	RecvSub(6),
	/**
	 *  返回订阅
	 */
	RespSub(7),
	/**
	 * 上报数据端点
	 */
	Sync(8),
	/**
	 * 设备接收到通知
	 */
	RecvNotify(9),
	/**
	 * 设备完成升级
	 */
	CompleteUpgrade(10),
	/**
	 * 设备自定义上报日志
	 */
	CusteomTrace(11),
	
	;
	
	private final int type;
	private DeviceLogType(int type) {
		this.type = type;
	}
	
	public int type(){
		return type;
	}
	
	public static final DeviceLogType fromType(int type){
		for (DeviceLogType tlt : values()) {
			if (tlt.type() == type) {
				return tlt;
			}
		}
		return null;
	}
	
}

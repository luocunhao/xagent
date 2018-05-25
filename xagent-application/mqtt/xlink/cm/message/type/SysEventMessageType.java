package xlink.cm.message.type;


public enum SysEventMessageType {

	/**
	 * 升级指令下发
	 */
	Upgrade(1),
	/**
	 * 升级完成上报
	 */
	UpgradeComplete(2),
	/**
	 * 请求服务器时间
	 */
	ServerTime(3),
	/**
	 * 返回服务器时间
	 */
	ServerTimeResponse(4),
	/**
	 * 踢下线
	 */
	KickedOffline(5),
	
	
	;
	private final int type;
	private SysEventMessageType(int type) {
		this.type = type;
	}
	
	public int type(){
		return type;
	}
	
	public static SysEventMessageType fromType(int type) {
		for (SysEventMessageType st : values()) {
			if (st.type() == type) {
				return st;
			}
		}
		return null;
	}
}

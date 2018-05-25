package xlink.cm.message.type;

public enum SubRetCode {

	/**
	 * 
	 */
	SUCCESS(1),
	/**
	 * 设备信息有误
	 */
	DeviceInfoError(2),
	/**
	 * ticket校验失败
	 */
	TicketVerifyError(3),
	/**
	 * 云端订阅失败
	 */
	SubFail(4),
	/**
	 * 扫描模式限制
	 */
	SubFailByScanModeLimit(5)
	
	;
	private final int code;
	private SubRetCode(int code) {
		this.code = code;
	}
	
	public int code(){
		return code;
	}
	
	public static final SubRetCode fromCode(int code){
		for (SubRetCode rc : values()) {
			if (rc.code == code) {
				return rc;
			}
		}
		return null;
	}
}

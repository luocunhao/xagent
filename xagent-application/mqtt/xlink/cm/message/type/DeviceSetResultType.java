package xlink.cm.message.type;

public enum DeviceSetResultType {

	/**
	 * 设置成功
	 */
	SUCCESS(0),
	/**
	 * 设置失败
	 */
	Fail(1),
	/**
	 * 权限不足
	 */
	NoPermission(2)
	
	;
	private final int code;
	private DeviceSetResultType(int code) {
		this.code = code;
	}
	
	public int code(){
		return code;
	}
	
	public static final DeviceSetResultType fromType(int code){
		for (DeviceSetResultType dt: values()) {
			if (dt.code == code) {
				return dt;
			}
		}
		return null;
	}
	
}

package xlink.cm.message.type;

public enum ActivateRetCodeType {
	Success(0), DeviceNoExists(1), ActivateCodeError(2),
	/**
	 * 未授权
	 */
	Unauthorize(3),;
	private final int code;

	private ActivateRetCodeType(int code) {
		this.code = code;
	}
	
	public static ActivateRetCodeType fromCode(int code){
		for (ActivateRetCodeType codeType : values()) {
			if (codeType.code == code) {
				return codeType;
			}
		}
		return null;
	}
}

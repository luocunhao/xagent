package xlink.cm.message.type;

public enum DeviceUpgradeRetCode {

	SUCCESS(1),
	
	FAIL(2),
	
	;
	private final int code;
	private DeviceUpgradeRetCode(int code) {
		this.code = code;
	}
	
	public int code(){
		return code;
	}
	public static final DeviceUpgradeRetCode fromCode(int code){
		for (DeviceUpgradeRetCode c : values()) {
			if (c.code == code) {
				return c;
			}
		}
		return null;
	}
}

package xlink.cm.message.type;

public enum DeviceLogLevelType {

	
	DEBUG(0),
	
	INFO(1),
	
	WARN(2),
	
	ERROR(3),
	
	;
	
	private final int type;
	private DeviceLogLevelType(int type) {
		this.type = type;
	}
	
	
	public int type(){
		return type;
	}
	
	public static final DeviceLogLevelType fromType(int type){
		for (DeviceLogLevelType tlt : values()) {
			if (tlt.type() == type) {
				return tlt;
			}
		}
		return null;
	}
	
}

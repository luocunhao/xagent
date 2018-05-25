package xlink.cm.message.type;

public enum DeviceStateType {

	Unknown(-1),
	
	Online(1),
	
	Offline(2),
	
	;
	
	private int type;
	private DeviceStateType(int type){
		this.type = type;
	}
	
	public int type(){
		return this.type;
	}
	
	public static DeviceStateType fromType(int type){
		for(DeviceStateType state:values()){
			if(state.type() == type){
				return state;
			}
		}
		return Unknown;
	}
	
}

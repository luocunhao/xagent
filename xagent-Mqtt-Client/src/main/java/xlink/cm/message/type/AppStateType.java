package xlink.cm.message.type;

public enum AppStateType {

	Unknown(-1),
	
	Online(1),
	
	Offline(2),
	
	;
	
	private int type;
	private AppStateType(int type){
		this.type = type;
	}
	
	public int type(){
		return this.type;
	}
	
	public static AppStateType fromType(int type){
		for(AppStateType state:values()){
			if(state.type() == type){
				return state;
			}
		}
		return Unknown;
	}
	
}

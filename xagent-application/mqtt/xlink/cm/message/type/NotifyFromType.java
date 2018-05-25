package xlink.cm.message.type;

public enum NotifyFromType {
	/**
	 * 来自Server
	 */
	Server(1),
	/**
	 * 来自Device
	 */
	Device(2),
	/**
	 * 来自App
	 */
	App(3),
	
	
	;
	
	private final int type;
	private NotifyFromType(int type) {
		this.type = type;
	}
	
	public int type(){
		return type;
	}
	
	
	public static final NotifyFromType fromType(int type){
		for(NotifyFromType nft : values()){
			if (nft.type() == type) {
				return nft;
			}
		}
		return null;
	}
	
}

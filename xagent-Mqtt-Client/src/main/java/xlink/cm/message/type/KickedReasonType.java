package xlink.cm.message.type;

public enum KickedReasonType {
	/**
	 * 别处上线导致下线
	 */
	OtherLogin(1)
	
	;
	
	private final int type;
	private KickedReasonType(int type) {
		this.type = type;
	}
	
	public byte type(){
		return (byte) type;
	}
}

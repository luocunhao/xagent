package xlink.cm.message.type;

public enum GetDeviceDatapointResultType {

	/**
	 * 成功
	 */
	Success(0),
	/**
	 * 服务不可用
	 */
	ServerUnavailable(1)
	;
	
	private final int retCode;
	private GetDeviceDatapointResultType(int retCode) {
		this.retCode = retCode;
	}
	
	public int retCode(){
		return retCode;
	}
}

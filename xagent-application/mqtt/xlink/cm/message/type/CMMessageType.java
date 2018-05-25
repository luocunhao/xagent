package xlink.cm.message.type;

public enum CMMessageType {

	Activation(1),

	ActivetionResult(2),

	DeviceOnline(3),
	
	DeviceOnlineResult(4),

	DeviceOffline(5),

	DatapointSync(6),
	
	DatapointSet(7),

	DatapointSetResult(8),

	DatapointGet(9),

	DatapointGetResult(10),

	AppOffline(11),

	DeviceNotifyEvent(12),

	AppNotifyEvent(13),

	SysEvent(14),

	Sub(15),
	
	SubResult(16),
	
	SubVerify(17),
	
	SubVerifyResult(18),
	
	DeviceLogSwitch(19),
	
	DeviceLog(20),
	
	DeviceTicketGet(21),
	
	DeviceTicketGetResult(22),
	
	DeviceLocation(23),
	
	ProductionTestStart(24),
	
	ProductionTestEnd(25),
	
	GatewaySubDevice(26),
	
	GatewayModule(27),
	
	GatewaySession(28),
	
	ProductionTestSet(29),
	
	ProductionTestSetResult(30),
	
	DeviceState(34),
	
	AppState(35),
	;

	private final int type;

	private CMMessageType(int type) {
		this.type = type;
	}

	public int type() {
		return type;
	}
	
	
	public static final CMMessageType fromType(int type){
		for(CMMessageType ct :values()){
			if (ct.type() == type) {
				return ct;
			}
		}
		return null;
	}

}

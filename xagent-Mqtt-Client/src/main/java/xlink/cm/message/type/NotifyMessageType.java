package xlink.cm.message.type;

public enum NotifyMessageType {
	/**
	 * 数据端点通知
	 */
	Datapoint("dp_notice", 1),
	/**
	 * 设备告警
	 */
	Alert("dp_alert", 2),
	/**
	 * 设备分享通知
	 */
	Share("device_share", 3),
	/**
	 * 广播消息
	 */
	Broadcast("broadcast", 4),
	/**
	 * 设备信息变化通知
	 */
	Device_Info("device_info", 5),
	/**
	 * 设备订阅关系变化
	 */
	Device_Sub("device_sub", 6),
	/**
	 * 设备状态通知
	 */
	Device_State_Notice("ds_notice", 7),
	/**
	 * 设备状态告警
	 */
	Device_State_Alert("ds_alert",8),
	/**
	 * 家庭消息通知
	 */
	Home_Message("home_message",9),
	/**
	 * 家庭邀请
	 */
	Home_Invite("home_invite", 10),
	/**
	 * 家庭设备授权
	 */
	Home_Device_Authority("home_device_authority", 11),
	/**
	 * 家庭成员变化
	 */
	Home_Member_Change("home_member_change", 12),
	/**
	 * 家庭设备变化
	 */
	Home_Device_Change("home_device_change",13)
	;

	private final String value;
	private final int type;

	private NotifyMessageType(String value, int type) {
		this.value = value;
		this.type = type;
	}

	public String value() {
		return value;
	}

	public int type() {
		return type;
	}
	
	public static final NotifyMessageType fromValue(String value){
		for (NotifyMessageType mt : values()) {
			if (mt.value().equals(value)) {
				return mt;
			}
		}
		return null;
	}
	
	public static final NotifyMessageType fromType(int type){
		for (NotifyMessageType mt : values()) {
			if (mt.type() == type) {
				return mt;
			}
		}
		return null;
	}
}

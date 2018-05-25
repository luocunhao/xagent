package xlink.cm.message.struct;

public class ActivationStruct {

	private String pid;
	private String mac;
	private boolean hasWifiFirmware;
	private boolean hasWifiVersion;
	private boolean hasMCUFirmware;
	private boolean hasMCUVersion;
	private boolean hasSn;
	private boolean hasGatewayId;
	private byte wifiFirmware;
	private int wifiVersion;
	private byte mcuFirmware;
	private int mcuVersion;
	private String sn;
	private int gatewayId;
  /**
   * 是否拥有更多标志位
   */
  private boolean hasMoreFlag;
  private byte otherFlag;
  private boolean hasActiveIp;
  private byte[] activeIp;

	public ActivationStruct(String pid, String mac, boolean hasWifiFirmware, boolean hasWifiVersion, boolean hasMCUFirmware, boolean hasMCUVersion,
      boolean hasSn, boolean hasGatewayId, byte wifiFirmware, int wifiVersion, byte mcuFirmware,
      int mcuVersion, String sn, int gatewayId, boolean hasMoreFlag, byte otherFlag,
      boolean hasActiveIp, byte[] activeIp) {
		super();
		this.pid = pid;
		this.mac = mac;
		this.hasWifiFirmware = hasWifiFirmware;
		this.hasWifiVersion = hasWifiVersion;
		this.hasMCUFirmware = hasMCUFirmware;
		this.hasMCUVersion = hasMCUVersion;
		this.hasSn = hasSn;
		this.hasGatewayId = hasGatewayId;
		this.wifiFirmware = wifiFirmware;
		this.wifiVersion = wifiVersion;
		this.mcuFirmware = mcuFirmware;
		this.mcuVersion = mcuVersion;
		this.sn = sn;
		this.gatewayId = gatewayId;
    this.hasMoreFlag = hasMoreFlag;
    this.otherFlag = otherFlag;
    this.hasActiveIp = hasActiveIp;
    this.activeIp = activeIp;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public boolean isHasWifiFirmware() {
		return hasWifiFirmware;
	}

	public void setHasWifiFirmware(boolean hasWifiFirmware) {
		this.hasWifiFirmware = hasWifiFirmware;
	}

	public boolean isHasWifiVersion() {
		return hasWifiVersion;
	}

	public void setHasWifiVersion(boolean hasWifiVersion) {
		this.hasWifiVersion = hasWifiVersion;
	}

	public boolean isHasMCUFirmware() {
		return hasMCUFirmware;
	}

	public void setHasMCUFirmware(boolean hasMCUFirmware) {
		this.hasMCUFirmware = hasMCUFirmware;
	}

	public boolean isHasMCUVersion() {
		return hasMCUVersion;
	}

	public void setHasMCUVersion(boolean hasMCUVersion) {
		this.hasMCUVersion = hasMCUVersion;
	}

	public boolean isHasSn() {
		return hasSn;
	}

	public void setHasSn(boolean hasSn) {
		this.hasSn = hasSn;
	}

	public boolean isHasGatewayId() {
		return hasGatewayId;
	}

	public void setHasGatewayId(boolean hasGatewayId) {
		this.hasGatewayId = hasGatewayId;
	}

	public byte getWifiFirmware() {
		return wifiFirmware;
	}

	public void setWifiFirmware(byte wifiFirmware) {
		this.wifiFirmware = wifiFirmware;
	}

	public int getWifiVersion() {
		return wifiVersion;
	}

	public void setWifiVersion(int wifiVersion) {
		this.wifiVersion = wifiVersion;
	}

	public byte getMcuFirmware() {
		return mcuFirmware;
	}

	public void setMcuFirmware(byte mcuFirmware) {
		this.mcuFirmware = mcuFirmware;
	}

	public int getMcuVersion() {
		return mcuVersion;
	}

	public void setMcuVersion(int mcuVersion) {
		this.mcuVersion = mcuVersion;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public int getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(int gatewayId) {
		this.gatewayId = gatewayId;
	}

  public byte[] getActiveIp() {
    return activeIp;
  }

  public void setActiveIp(byte[] activeIp) {
    this.activeIp = activeIp;
  }

  public boolean isHasActiveIp() {
    return hasActiveIp;
  }

  public void setHasActiveIp(boolean hasActiveIp) {
    this.hasActiveIp = hasActiveIp;
  }



  public boolean isHasMoreFlag() {
    return hasMoreFlag;
  }

  public void setHasMoreFlag(boolean hasMoreFlag) {
    this.hasMoreFlag = hasMoreFlag;
  }

  public byte getOtherFlag() {
    return otherFlag;
  }

  public void setOtherFlag(byte otherFlag) {
    this.otherFlag = otherFlag;
  }

  public String getActiveIpString() {
    if (activeIp != null) {
      return new StringBuilder().append(activeIp[0]).append(".").append(activeIp[1]).append(".")
          .append(activeIp[2]).append(".").append(activeIp[3]).toString();
    }
    return "";
  }

  @Override
  public String toString() {
    return "ActivationStruct [pid=" + pid + ", mac=" + mac + ", hasWifiFirmware=" + hasWifiFirmware
        + ", hasWifiVersion=" + hasWifiVersion + ", hasMCUFirmware=" + hasMCUFirmware
        + ", hasMCUVersion=" + hasMCUVersion + ", hasSn=" + hasSn + ", hasGatewayId=" + hasGatewayId
        + ", wifiFirmware=" + wifiFirmware + ", wifiVersion=" + wifiVersion + ", mcuFirmware="
        + mcuFirmware + ", mcuVersion=" + mcuVersion + ", sn=" + sn + ", gatewayId=" + gatewayId
        + ", hasMoreFlag=" + hasMoreFlag + ", otherFlag=" + otherFlag + ", hasActiveIp="
        + hasActiveIp + ", activeIp=" + getActiveIpString() + "]";
  }



}

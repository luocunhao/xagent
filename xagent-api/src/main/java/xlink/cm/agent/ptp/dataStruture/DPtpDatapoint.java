package xlink.cm.agent.ptp.dataStruture;

/**
 * 数据端点实体
 * 
 * @author xlink
 *
 */
public class DPtpDatapoint {

  /**
   * xlink平台的数据端点索引
   */
  private int index;
  /**
   * 数据端点的值
   */
  private Object value;
  /**
   * xlink设备数据端点的类型 {@link xlink.cm.agent.ptp.dataStruture.XlinkDeviceDatapointType}
   */
  private XlinkDeviceDatapointType dataType;

  public DPtpDatapoint(int index, Object value, XlinkDeviceDatapointType dataType) {
    this.index = index;
    this.value = value;
    this.dataType = dataType;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public XlinkDeviceDatapointType getDataType() {
    return dataType;
  }

  public void setDataType(XlinkDeviceDatapointType dataType) {
    this.dataType = dataType;
  }

  @Override
  public String toString() {
    return "DPtpDatapoint [index=" + index + ", value=" + value + ", dataType=" + dataType + "]";
  }



}

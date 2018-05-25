package xlink.mqtt.client.dataStructure;

public class XlinkCMDataPoint {

  private int index;
  
  private Object value;

  private DatapointDataType dataType;



  public XlinkCMDataPoint(int index, Object value, DatapointDataType dataType) {
    super();
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

  public DatapointDataType getDataType() {
    return dataType;
  }

  public void setDataType(DatapointDataType dataType) {
    this.dataType = dataType;
  }



}

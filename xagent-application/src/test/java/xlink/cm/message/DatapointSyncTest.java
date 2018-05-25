package xlink.cm.message;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import xlink.cm.agent.ptp.dataStruture.DPtpDatapoint;
import xlink.cm.agent.ptp.dataStruture.XlinkDeviceDatapointType;
import xlink.cm.message.struct.Datapoint;
import xlink.cm.message.type.DatapointType;
import xlink.core.utils.ByteTools;
import xlink.core.utils.ContainerGetter;
import xlink.mqtt.client.message.XlinkPublishMessageFactory;
import xlink.utils.tuple.TowTuple;

public class DatapointSyncTest {

  @Test
  public void syncTest() throws Exception {
    DPtpDatapoint dp = new DPtpDatapoint(0,0.3,XlinkDeviceDatapointType.Float);
    Map<Integer,DPtpDatapoint> dpMap = ContainerGetter.hashMap();
    dpMap.put(1, dp);
    List<Datapoint> datapoints = ContainerGetter.arrayList();
    for(Map.Entry<Integer, DPtpDatapoint> entry: dpMap.entrySet()){
      DPtpDatapoint  datapoint = entry.getValue();
      datapoints.add(new Datapoint(datapoint.getIndex(),DatapointType.fromDeviceType(datapoint.getDataType().type()),datapoint.getValue()));
    }
    TowTuple<String, byte[]> tuple =
        XlinkPublishMessageFactory.createDatapointSync(123, datapoints, new Date());
    System.out.println(ByteTools.printBytes2HexString(tuple.second));
  }
  
}

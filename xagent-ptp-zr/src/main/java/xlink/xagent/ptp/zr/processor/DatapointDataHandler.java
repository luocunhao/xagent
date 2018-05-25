package xlink.xagent.ptp.zr.processor;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import xlink.cm.agent.ptp.PtpDatapointSetListener;
import xlink.cm.agent.ptp.dataStruture.DPtpDatapoint;
import xlink.xagent.ptp.zr.main.ZrPlugin;

public class DatapointDataHandler implements PtpDatapointSetListener {
    private static final Logger logger = LoggerFactory.getLogger(DatapointDataHandler.class);

    @Override
    public void datapointNotify(int deviceId, int messageId,
                                Map<Integer, DPtpDatapoint> datapointValue) {
        logger.info("receive datapoint  " + datapointValue);

        //TODO 逻辑处理
        try {
//            Map<Integer,MacSlaverIdMap> map = DeviceConfig.getDeviceMap();
//            ModbusMaster master = ZrPlugin.getModbusMaster();
//            master.setTimeout(5000);
//            master.setRetries(1);
//            master.init();
//            for (Integer key : datapointValue.keySet()) {
//                System.out.println("key:===========" + key);
//                System.out.println("value:" + datapointValue.get(key).toString());
//                int slaverid = map.get(deviceId).getSlaverid();
//                DPtpDatapoint dPtpDatapoint = datapointValue.get(key);
//                int index = dPtpDatapoint.getIndex();
//                Object value = dPtpDatapoint.getValue();
//                BaseLocator baseLocator =
//                        BaseLocator.holdingRegister(slaverid,index-ZrConfig.HOLDING_INDEX, DataType.TWO_BYTE_INT_SIGNED);
//                master.setValue(baseLocator,value);
//            }
            ZrPlugin.getXagentApi().replyDatapointSet(deviceId, messageId, 0);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}

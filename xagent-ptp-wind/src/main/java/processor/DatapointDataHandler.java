package processor;

import main.WindPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xlink.cm.agent.ptp.PtpDatapointSetListener;
import xlink.cm.agent.ptp.PtpFuture;
import xlink.cm.agent.ptp.dataStruture.DPtpDatapoint;
import xlink.cm.agent.ptp.dataStruture.DatapointSyncResult;
import xlink.cm.agent.ptp.dataStruture.XlinkDeviceDatapointType;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DatapointDataHandler implements PtpDatapointSetListener {
    private static final Logger logger = LoggerFactory.getLogger(DatapointDataHandler.class);

    @Override
    public void datapointNotify(int deviceId, int messageId,
                                Map<Integer, DPtpDatapoint> datapointValue) {
        logger.info("receive datapoint  " + datapointValue);

        //TODO 逻辑处理
        try {
            WindPlugin.getXagentApi().replyDatapointSet(deviceId, messageId, 0);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}

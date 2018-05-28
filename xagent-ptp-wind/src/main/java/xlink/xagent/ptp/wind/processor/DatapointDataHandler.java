package xlink.xagent.ptp.wind.processor;

import xlink.xagent.ptp.wind.main.WindPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xlink.cm.agent.ptp.PtpDatapointSetListener;
import xlink.cm.agent.ptp.dataStruture.DPtpDatapoint;

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

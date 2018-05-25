package xlink.xagent.ptp.zr.processor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xlink.cm.agent.ptp.IPtpProtocolProcessor;
import xlink.cm.agent.ptp.PtpFuture;
import xlink.cm.agent.ptp.PtpServer;
import xlink.cm.agent.ptp.dataStruture.DDevice;
import xlink.cm.agent.ptp.dataStruture.DPtpDatapoint;
import xlink.cm.agent.ptp.dataStruture.DatapointSyncResult;
import xlink.cm.agent.ptp.dataStruture.DeviceActivateResult;
import xlink.cm.agent.ptp.dataStruture.XlinkDeviceDatapointType;
import xlink.xagent.ptp.zr.main.ZrPlugin;


public class ZrProcessor implements IPtpProtocolProcessor {
  private static final Logger logger = LoggerFactory.getLogger(ZrProcessor.class);

  @Override
  public void process(PtpServer server, int channelId, Object msg) {
    logger.info("receive data.... send back.");
    server.sendDataAndFlush(channelId, msg);
    try {
      DDevice device =
          ZrPlugin.getXagentApi().getDeviceInfo("160fa6af87a50c00160fa6af87a50c01",
              "110110110110");
      String remoteIp = ZrPlugin.getPtpServer().getSocketRemoteIp(channelId);
      if (device == null) {
        PtpFuture<DeviceActivateResult> ptpFuture =
            ZrPlugin.getXagentApi()
                .activateDevice("160fa6af87a50c00160fa6af87a50c01", "110110110110",
                    (byte) 2, 2, (byte) 2, 2, "201710191453", 0, remoteIp)
                .sync();
        logger.info("active result is: " + ptpFuture.get().getResultCode());
        device = ptpFuture.get().getDevice();
      }
      logger.info("device id is:" + device.getDeviceId());
      logger.info("start device online.");
      ZrPlugin.getXagentApi().deviceOnline(device.getDeviceId(), remoteIp).sync();
      Thread.sleep(3000);

      DPtpDatapoint dp = new DPtpDatapoint(1, 297, XlinkDeviceDatapointType.Int32);
      Map<Integer, DPtpDatapoint> datapointMap = new HashMap<Integer, DPtpDatapoint>();
      datapointMap.put(1, dp);
      PtpFuture<DatapointSyncResult> syncFuture =
          ZrPlugin.getXagentApi().datapointSync(device.getDeviceId(), new Date(), datapointMap)
              .sync();
      logger.info("datapoint sync result is: " + syncFuture.isSuccess());
      Thread.sleep(5000);
      logger.info("start device offline.");
      ZrPlugin.getXagentApi().deviceOffline(device.getDeviceId());
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  public void channelBuild(int channelId) {
    logger.info("channel build...");

  }

  @Override
  public void channelClose(int channelId) {
    logger.info("channel close...");

  }

}

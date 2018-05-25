package xlink.xagent.ptp.zr.processor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xlink.cm.agent.ptp.IPtpProtocolProcessor;
import xlink.cm.agent.ptp.PtpFuture;
import xlink.cm.agent.ptp.PtpServer;
import xlink.cm.agent.ptp.dataStruture.DDevice;
import xlink.cm.agent.ptp.dataStruture.DPtpDatapoint;
import xlink.cm.agent.ptp.dataStruture.DatapointSyncResult;
import xlink.cm.agent.ptp.dataStruture.DeviceActivateResult;
import xlink.cm.agent.ptp.dataStruture.DeviceOnlineResult;
import xlink.xagent.ptp.zr.codec.ZrMessageType;
import xlink.xagent.ptp.zr.main.ZrConfig;
import xlink.xagent.ptp.zr.main.ZrPlugin;
import xlink.xagent.ptp.zr.message.DatapointPayloadMessage;
import xlink.xagent.ptp.zr.message.ZrAuthPayloadMessage;
import xlink.xagent.ptp.zr.message.ZrHeaderMessage;
import xlink.xagent.ptp.zr.message.ZrMessage;

public class BusinessLogicProcessor implements IPtpProtocolProcessor {

  private static final Logger logger = LoggerFactory.getLogger(DemoProcessor.class);

  private Map<ZrMessageType, DemoProcessor> processorMap =
      new ConcurrentHashMap<ZrMessageType, DemoProcessor>();


  public BusinessLogicProcessor() {
    processorMap.put(ZrMessageType.Auth, new AuthProcessor());
    processorMap.put(ZrMessageType.Offline, new OfflineProcessor());
    processorMap.put(ZrMessageType.Datapoint, new DatapointProcessor());
    processorMap.put(ZrMessageType.HeartBeat, new HeartBeatProcessor());
  }

  @Override
  public void process(PtpServer server, int channelId, Object msg) {
    if (msg instanceof ZrMessage) {
      ZrMessage message = (ZrMessage) msg;
      processorMap.get(message.getHeader().getMessageType()).process(channelId, message);
    }


  }
  public interface DemoProcessor {
    public void process(int channelId, ZrMessage message);
  }

  private class AuthProcessor implements DemoProcessor {

    @Override
    public void process(int channelId, ZrMessage message) {
      // TODO Auto-generated method stub
    	ZrAuthPayloadMessage authpayload = (ZrAuthPayloadMessage)message.getPayload();
        String mac = authpayload.getIdentify();
        try {
        	DDevice device = ZrPlugin.getXagentApi().getDeviceInfo(ZrConfig.productId, mac);
        	
        		PtpFuture<DeviceActivateResult> future = ZrPlugin.getXagentApi().activateDevice(ZrConfig.productId, mac, (byte)0, 0, (byte)0, 0, null, 0, null).sync();
        		DeviceActivateResult result = future.get();
        		device = ZrPlugin.getXagentApi().getDeviceInfo(ZrConfig.productId, mac);
        		//DeviceActivateResult result = future.get();      	
            	//device = ZrPlugin.getXagentApi().getDeviceInfo(ZrConfig.productId, mac);
            	//device = result.getDevice();
            	logger.debug("active device id is {} mac is {}, result {}",device.getDeviceId(),mac,result.getResultCode());
        	
        	logger.debug("device auth success. deviceId is{},mac is{}",device.getDeviceId(),device.getMac());
        	PtpFuture<DeviceOnlineResult> onlineFuture = ZrPlugin.getXagentApi().deviceOnline(device.getDeviceId(), "127.0.0.1").sync();
        	DeviceOnlineResult onlineReslt = onlineFuture.get();
        	logger.debug("device online success. result is: {}",onlineReslt.getReturnCode());
        	ZrHeaderMessage header = new ZrHeaderMessage(ZrMessageType.Auth,1);
        	ZrMessage msg = new ZrMessage(header,null);
        	ZrPlugin.getPtpServer().sendDataAndFlush(channelId, msg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

  }

  private class OfflineProcessor implements DemoProcessor {

    @Override
    public void process(int channelId, ZrMessage message) {
      // TODO Auto-generated method stub

    }

  }

  private class DatapointProcessor implements DemoProcessor {

    @Override
    public void process(int channelId, ZrMessage message) {
    	DatapointPayloadMessage dpPayload = (DatapointPayloadMessage)message.getPayload();
     logger.debug("recevice datapoint sync message,device is {}",dpPayload.getDeviceId());
     Map<Integer, DPtpDatapoint> syncMap = new HashMap<Integer,DPtpDatapoint>();
     for(DPtpDatapoint datapoint:dpPayload.getDatapoints()) {
    	 syncMap.put(datapoint.getIndex(), datapoint);
     }
     try {
    	 PtpFuture<DatapointSyncResult> future = ZrPlugin.getXagentApi().datapointSync(dpPayload.getDeviceId(), new Date(), syncMap).sync();
    	 DatapointSyncResult dpSyncResult = future.get();
    	logger.debug("sync result code is: {}",dpSyncResult.getReturnCode());
    	
    	//回复客户端
    	ZrHeaderMessage header = new ZrHeaderMessage(ZrMessageType.Datapoint,1);
    	ZrMessage msg = new ZrMessage(header,null);
    	ZrPlugin.getPtpServer().sendDataAndFlush(channelId, msg);
	} catch (Exception e) {
		e.printStackTrace();
	}

    }

  }

  private class HeartBeatProcessor implements DemoProcessor {

    @Override
    public void process(int channelId, ZrMessage message) {
      // TODO Auto-generated method stub

    }

  }




  @Override
  public void channelBuild(int channelId) {
    logger.info("channel build,channel is: {}", channelId);

  }

  @Override
  public void channelClose(int channelId) {
    logger.info("channel close, channel is: {}", channelId);

  }
}

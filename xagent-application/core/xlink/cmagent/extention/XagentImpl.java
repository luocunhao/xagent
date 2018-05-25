package xlink.cmagent.extention;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLEngine;

import org.pf4j.Extension;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import xlink.cm.agent.extensions.XagentApi;
import xlink.cm.agent.ptp.IPtpClientProtocolProcessor;
import xlink.cm.agent.ptp.IPtpDecoder;
import xlink.cm.agent.ptp.IPtpEncoder;
import xlink.cm.agent.ptp.IPtpProtocolProcessor;
import xlink.cm.agent.ptp.PtpCertificateType;
import xlink.cm.agent.ptp.PtpClient;
import xlink.cm.agent.ptp.PtpClientStrategy;
import xlink.cm.agent.ptp.PtpDatapointSetListener;
import xlink.cm.agent.ptp.PtpFuture;
import xlink.cm.agent.ptp.PtpServer;
import xlink.cm.agent.ptp.PtpServerStrategy;
import xlink.cm.agent.ptp.dataStruture.DDevice;
import xlink.cm.agent.ptp.dataStruture.DPtpDatapoint;
import xlink.cm.agent.ptp.dataStruture.DatapointSyncResult;
import xlink.cm.agent.ptp.dataStruture.DeviceActivateResult;
import xlink.cm.agent.ptp.dataStruture.DeviceOfflineResult;
import xlink.cm.agent.ptp.dataStruture.DeviceOnlineResult;
import xlink.cm.agent.ptp.dataStruture.XlinkDeviceDatapointType;
import xlink.cm.exception.DatapointException;
import xlink.cm.message.ActivationResultMessage;
import xlink.cm.message.DatapointSetMessage;
import xlink.cm.message.DatapointSyncMessage;
import xlink.cm.message.struct.ActivationResultStruct;
import xlink.cm.message.struct.Datapoint;
import xlink.cm.message.struct.DatapointSyncStruct;
import xlink.cm.message.type.DatapointType;
import xlink.cm.ptp.future.impl.DDevicePtpFuture;
import xlink.cm.ptp.future.impl.DeviceDatapointSyncResultPtpFuture;
import xlink.cm.ptp.future.impl.DeviceOfflineResultPtpFuture;
import xlink.cm.ptp.future.impl.DeviceOnlineResultPtpFuture;
import xlink.cm.ptp.platform.PtpFramework;
import xlink.core.derby.dataStructure.DeviceInfo;
import xlink.core.derby.service.DeviceInfoService;
import xlink.core.manager.DeviceCacheManager;
import xlink.core.manager.WebApiManager;
import xlink.core.utils.ByteTools;
import xlink.core.utils.ContainerGetter;
import xlink.core.utils.NoProguard;
import xlink.core.utils.StringTools;
import xlink.mqtt.client.MqttClient;
import xlink.mqtt.client.MqttClientManager;
import xlink.mqtt.client.Future.DatapointSetListener;
import xlink.mqtt.client.Future.DeviceStateListener;
import xlink.mqtt.client.Future.IMqttActionListener;
import xlink.mqtt.client.exception.XlinkMqttException;
import xlink.mqtt.client.message.XlinkPublishMessageFactory;
import xlink.mqtt.client.queue.subclass.PtpDeviceOfflineAsyncQueue;
import xlink.mqtt.client.queue.subclass.PtpDeviceOnlineAsyncQueue;
import xlink.mqtt.client.token.MqttToken;
import xlink.mqtt.client.type.XlinkMqttTopicType;
import xlink.mqtt.client.utils.LogHelper;
import xlink.mqtt.client.utils.MqttUtils;
import xlink.utils.tuple.TowTuple;

@NoProguard
@Extension
public class XagentImpl implements XagentApi {

  private MqttClient mqttClient;
	private String ptpId;

	@Override
	public PtpServer createServer(String pluginId, String cId, String cKey, int port, IPtpDecoder decoder, IPtpEncoder encoder,
			IPtpProtocolProcessor processor, int keepAlive, PtpServerStrategy ptpServerStrategy) throws Exception {
	/*	mqttClient = MqttClientManager.instance().getMqttClient(cId, cKey, pluginId);
		ptpId = pluginId;
		PtpServer ptpServer = PtpFramework.instance().createPtpServer(pluginId, cId, cKey, port, decoder, encoder, processor, keepAlive,
				ptpServerStrategy);
		return ptpServer;*/
		return createServerWithCertType( pluginId,  cId,  cKey,  PtpCertificateType.Product,  port,decoder,  encoder,  processor,  keepAlive,ptpServerStrategy);
	}

	@Override
	public DDevice getDeviceInfo(String productId, String identify) {
		DeviceInfo info = DeviceInfoService.instance().findByProductIdAndMac(ptpId, productId, identify);
		if (info != null) {
			return new DDevice(info.getMac(), info.getDeviceId(), info.getDeviceKey(),info.getProductId());
		}
		return null;
	}

	@Override
	public PtpFuture<DeviceActivateResult> activateDevice(final String productId, final String identify, byte wifiFirmware, int wifiVersion,
      byte mcuFirmware, int mcuVersion, String sn, int gatewayDeviceId, String activeIp)
      throws Exception {
		TowTuple<String, byte[]> tuple = XlinkPublishMessageFactory.createDeviceActivePayload(productId, identify, wifiFirmware, wifiVersion,
        mcuFirmware, mcuVersion, sn, gatewayDeviceId, activeIp);
		MqttToken token = mqttClient.publish(MqttUtils.getMessageId(), tuple.first, tuple.second, 1);
		token.setCallbackListener(new IMqttActionListener() {
			@Override
			public void onSucess(MqttToken actionToken) {
				ActivationResultMessage result = (ActivationResultMessage) actionToken.getResponseMsg();
				ActivationResultStruct activeStruct = result.getActivationResultList().get(0);
				if (activeStruct.getCode() == 0) {
					/*DeviceInfoService.instance().insert(ptpId, new DeviceInfo(activeStruct.getDeviceId(), productId, identify, "-",
							activeStruct.getKey(), false, new java.sql.Date(System.currentTimeMillis())));*/
					DeviceCacheManager.instance().saveDevicebyProductIdAndMac(ptpId, new DeviceInfo(activeStruct.getDeviceId(), productId, identify, "-",
							activeStruct.getKey(), false, new java.sql.Date(System.currentTimeMillis())));
				}
			}

			@Override
			public void onFailure(MqttToken actionToken, XlinkMqttException exception) {
				// TODO Auto-generated method stub

			}

		});
		return new DDevicePtpFuture(token,productId);
	}

	@Override
  public PtpFuture<DeviceOnlineResult> deviceOnline(final int deviceId, String ip)
      throws Exception {

    TowTuple<String, byte[]> tuple =
        XlinkPublishMessageFactory.createDeviceOnlinePayLoad(deviceId, ip);
		MqttToken token = mqttClient.publish(MqttUtils.getMessageId(), tuple.first, tuple.second, 1);
		token.setCallbackListener(new IMqttActionListener() {

			@Override
			public void onSucess(MqttToken actionToken) {
        /*
         * LogHelper.LOGGER().info("subscribe datapoint topic for %d", deviceId); String topic =
         * XlinkMqttTopicType.DEVICE_DATAPOINT_SET.shortTopic().replaceAll("\\{device_id\\}",
         * Integer.toString(deviceId)); try { mqttClient.subscribe(MqttUtils.getMessageId(), new
         * String[] { topic }, new int[] { 1 }); } catch (InterruptedException e) {
         * LogHelper.LOGGER().info("subscribe failed, %s", e.getMessage()); }
         */
        MqttClientManager.instance().putAsyncQueue(new PtpDeviceOnlineAsyncQueue(deviceId, mqttClient));
			}

			@Override
			public void onFailure(MqttToken actionToken, XlinkMqttException exception) {
				// TODO Auto-generated method stub

			}

		});
    return new DeviceOnlineResultPtpFuture(token);
  }
  @Override
  public PtpFuture<DatapointSyncResult> datapointSync(int deviceId, Date updateTime,
      Map<Integer, DPtpDatapoint> datapointValue) throws Exception {
    List<Datapoint> datapoints = ContainerGetter.arrayList();
    for(Map.Entry<Integer, DPtpDatapoint> entry: datapointValue.entrySet()){
      DPtpDatapoint  datapoint = entry.getValue();
      datapoints.add(new Datapoint(datapoint.getIndex(),DatapointType.fromDeviceType(datapoint.getDataType().type()),datapoint.getValue()));
    }
    TowTuple<String, byte[]> tuple =
        XlinkPublishMessageFactory.createDatapointSync(deviceId, datapoints, updateTime);
    if (updateTime != null) {
      LogHelper.LOGGER().info("datapointSync timestamp {}, deviceId {}, payload: {}",
          updateTime.getTime(), deviceId, ByteTools.printBytes2HexString(tuple.second));
    } else {
      LogHelper.LOGGER().info("datapointSync no timestamp, deviceId {}, payload: {}", deviceId,
          ByteTools.printBytes2HexString(tuple.second));
    }
    // MqttToken token = mqttClient.publish(MqttUtils.getMessageId(), tuple.first, tuple.second, 1);
    MqttToken token = mqttClient.publishSequence(MqttUtils.getMessageId(), tuple.first,
        tuple.second, 1, deviceId);
    return new DeviceDatapointSyncResultPtpFuture(token);
  }

	@Override
	public PtpFuture<DeviceOfflineResult> deviceOffline(int deviceId) throws Exception {
		TowTuple<String, byte[]> tuple = XlinkPublishMessageFactory.createDeviceOfflinePayLoad(deviceId);
		MqttToken token = mqttClient.publish(MqttUtils.getMessageId(), tuple.first, tuple.second, 1);
    MqttClientManager.instance()
        .putAsyncQueue(new PtpDeviceOfflineAsyncQueue(deviceId, mqttClient));
		return new DeviceOfflineResultPtpFuture(token);
	}

	@Override
	public void setDatapointSetListener(final PtpDatapointSetListener datapointSetLinstener) {
		mqttClient.addDatapointSetListener(new DatapointSetListener() {
			@Override
			public void messageNotify(int deviceId, DatapointSetMessage message) {
				DatapointSyncMessage dpMsg = new DatapointSyncMessage();
				ByteBuf datapoint = Unpooled.wrappedBuffer(message.getValue());
				int messageId = datapoint.readShort();
				dpMsg.setDatapoint(datapoint);
				try {
					DatapointSyncStruct datapointSync = dpMsg.parse(null);
					Map<Integer, DPtpDatapoint> datapointMap = ContainerGetter.hashMap();
					List<Datapoint> datapoints = datapointSync.getDatapoint();
					for (Datapoint datapointStruc : datapoints) {
						XlinkDeviceDatapointType dataType = XlinkDeviceDatapointType.fromType(datapointStruc.getDataType().device_type());
						datapointMap.put(datapointStruc.getIndex(),
								new DPtpDatapoint(datapointStruc.getIndex(), datapointStruc.getValue(), dataType));
					}
					LogHelper.LOGGER().info("datapoitn setting is {}", datapointMap);
					datapointSetLinstener.datapointNotify(deviceId, messageId, datapointMap);

				} catch (IOException | DatapointException e) {
					LogHelper.LOGGER().error("datapoint set message phase error.", e);
				}

			}

		});

	}

	@Override
	public void replyDatapointSet(int deviceId, int messageId, int result) throws Exception {
		TowTuple<String, byte[]> tuple = XlinkPublishMessageFactory.createDatapointSetResult(deviceId, messageId, result);
		MqttToken token = mqttClient.publish(MqttUtils.getMessageId(), tuple.first, tuple.second, 0);

	}

	@Override
	public DDevice getDeviceInfo(String productId, String identify, String accessToken) throws Exception {
		DeviceInfo info = DeviceInfoService.instance().findByProductIdAndMac(ptpId, productId, identify);
		if (info == null) {
			// 在derby数据库里面缓存
			info = WebApiManager.instance().getDevice(productId, identify, accessToken);
			//DeviceInfoService.instance().insert(ptpId, info);
			DeviceCacheManager.instance().saveDevicebyProductIdAndMac(ptpId, info);
		}
		return new DDevice(info.getMac(), info.getDeviceId(), info.getDeviceKey(), info.getProductId());

	}


  public void subscribeDeviceState(final int deviceId, final DeviceStateListener listener)
      throws Exception {
    final String topic = XlinkMqttTopicType.DEVICE_STATE.shortTopic().replaceAll("\\{device_id\\}",
        StringTools.getString(deviceId));
	  MqttToken mqttToken = mqttClient.subscribe(MqttUtils.getMessageId(), new String[]{topic}, new int[]{0});
	  mqttToken.setCallbackListener(new IMqttActionListener() {

      @Override
      public void onSucess(MqttToken actionToken) {
        mqttClient.addDeviceStateListener(listener);
        
      }

      @Override
      public void onFailure(MqttToken actionToken, XlinkMqttException exception) {
       LogHelper.LOGGER().info("subscribe topic {} failed.",topic);
        
      }
	    
	  });
	}

@Override
public PtpServer createServerWithCertType(String pluginId, String cId, String cKey, PtpCertificateType certType, int port,
		IPtpDecoder decoder, IPtpEncoder encoder, IPtpProtocolProcessor processor, int keepAlive,
		PtpServerStrategy ptpServerStrategy) throws Exception {
	mqttClient = MqttClientManager.instance().getMqttClient(cId, cKey, pluginId,certType);
	ptpId = pluginId;
	PtpServer ptpServer = PtpFramework.instance().createPtpServer(pluginId, cId, cKey, port, decoder, encoder, processor, keepAlive,
			ptpServerStrategy);
	return ptpServer;
}

@Override
public PtpClient createClient(String pluginId, String cId, String cKey, PtpCertificateType certType,
    String hostName, int port, IPtpDecoder decoder, IPtpEncoder encoder,
    IPtpClientProtocolProcessor processor, int keepAlive, PtpClientStrategy ptpClientStrategy,SSLEngine sslEngine)
    throws Exception {
  mqttClient = MqttClientManager.instance().getMqttClient(cId, cKey, pluginId,certType);
  ptpId = pluginId;
  return PtpFramework.instance().createPtpClient(pluginId, hostName, port, decoder, encoder, processor, keepAlive, ptpClientStrategy, sslEngine);
}


}

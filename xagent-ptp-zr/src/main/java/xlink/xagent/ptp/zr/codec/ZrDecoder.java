package xlink.xagent.ptp.zr.codec;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import xlink.cm.agent.ptp.IPtpDecoder;
import xlink.cm.agent.ptp.dataStruture.DPtpDatapoint;
import xlink.cm.agent.ptp.dataStruture.XlinkDeviceDatapointType;
import xlink.cm.agent.ptp.exception.PtpException;
import xlink.xagent.ptp.zr.message.*;
import xlink.xagent.ptp.zr.message.ZrAuthPayloadMessage;

public class ZrDecoder implements IPtpDecoder {

  private static final Logger logger = LoggerFactory.getLogger(ZrDecoder.class);

  @Override
  public void doDecode(ByteBuf in, List<Object> out) throws PtpException {
    logger.info("start decode.....");
    ZrHeaderMessage header = decodeHeader(in);
    ZrPayloadMessage payload = decodePayload(header, in);
    logger.info("header is: " + header);
    logger.info("payload is: " + payload);
    out.add(new ZrMessage(header, payload));
  }


  private ZrHeaderMessage decodeHeader(ByteBuf in){
    int type = in.readByte();
    int payloadLenght = in.readUnsignedShort();
    return new ZrHeaderMessage(ZrMessageType.fromType(type), payloadLenght);
  }

  private ZrPayloadMessage decodePayload(ZrHeaderMessage header, ByteBuf in) {
    switch (header.getMessageType()) {
      case Auth: {
        int idLenght = in.readUnsignedByte();
        byte[] idBytes = new byte[idLenght];
        in.readBytes(idBytes);
        String identify = new String(idBytes);
        int passwdLengh = in.readUnsignedByte();
        byte[] passwdBytes = new byte[passwdLengh];
        in.readBytes(passwdBytes);
        String password = new String(passwdBytes);
        return new ZrAuthPayloadMessage(identify, password);
      }
      case Offline: {
        return new ZrOfflinePayloadMessage();
      }
      case Datapoint: {
        ByteBuf datapointData = Unpooled.buffer(header.getPayloadLength());
        in.readBytes(datapointData, header.getPayloadLength());
        int deviceId = datapointData.readInt();
        List<DPtpDatapoint> datapoints = new ArrayList<DPtpDatapoint>();
        while (datapointData.readableBytes() > 0) {
          int index = datapointData.readUnsignedByte();
          int typeAndLengh = datapointData.readUnsignedShort();
          int type = typeAndLengh >> 12;
          int lenght = typeAndLengh & 0x0FFF;
          int value = datapointData.readInt();
          datapoints.add(new DPtpDatapoint(index, value, XlinkDeviceDatapointType.fromType(type)));
        }
        return new DatapointPayloadMessage(deviceId,datapoints);
      }
      case HeartBeat: {
        return new ZrHeartBeatPayloadMessage();
      }
      default:
        return null;
    }
  }



}

package org.gateway.pf4j.application;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import xlink.cm.message.CMMessage;
import xlink.cm.message.DatapointSyncMessage;
import xlink.cm.message.struct.Datapoint;
import xlink.cm.message.struct.DatapointSyncStruct;
import xlink.core.utils.Utils;
import xlink.mqtt.client.message.XlinkPublishMessageFactory;

public class MqttCmMessageParser {

  public static void main(String[] args) throws Exception {
    String data =
        "00f003000110006accf2349a6ee00203136306661366166396333653361303031363066613661663963336533613031002ba1cb";
   
    byte[] bytes = Utils.hexStringToBytes(data);
    System.out.println(bytes.length);
    ByteBuf buf = Unpooled.buffer().writeBytes(bytes);
    CMMessage message =  CMMessage.funcParseMessage(XlinkPublishMessageFactory.version, buf);;
    DatapointSyncMessage sync =
        (DatapointSyncMessage) CMMessage.funcParseMessage(XlinkPublishMessageFactory.version, buf);;
  //  DatapointSyncMessage d = new DatapointSyncMessage();
   // ByteBuf datapoint = Unpooled.wrappedBuffer(sync.getValue());
   // int messageId = datapoint.readShort();
   // d.setDatapoint(datapoint);
    DatapointSyncStruct struct = sync.parse(null);

    for (Datapoint dp : struct.getDatapoint()) {
      System.out.println(dp);
    }
  }



  public static String printBytes2HexString(byte[] bytes) {
    StringBuffer buf = new StringBuffer(bytes.length * 2);
    for (int i = 0; i < bytes.length; i++) {
      if (((int) bytes[i] & 0xff) < 0x10) {
        buf.append("0");
      }
      buf.append(Long.toString((int) bytes[i] & 0xff, 16)).append(" ");
    }
    return buf.toString();
  }

}

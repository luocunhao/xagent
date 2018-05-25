package org.gateway.pf4j.application;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import xlink.cm.message.ActivationMessage;
import xlink.cm.message.CMMessage;
import xlink.core.utils.Utils;
import xlink.mqtt.client.message.XlinkPublishMessageFactory;

public class MqttActivationMessageParser {


  public static void main(String[] args) throws Exception {
    String data =
        "0001002c002031363037643262336535666330303031313630376432623365356663356130310003100003007f000001";

    byte[] bytes = Utils.hexStringToBytes(data);
    ByteBuf buf = Unpooled.buffer().writeBytes(bytes);
    ActivationMessage activation =
        (ActivationMessage) CMMessage.funcParseMessage(XlinkPublishMessageFactory.version, buf);;
    System.out.println(activation.getActivationMessageList());

  }
}

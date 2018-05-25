package xlink.xagent.ptp.zr.test;

import java.net.Socket;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import xlink.xagent.ptp.zr.main.ZrConfig;

public class DemoClient {

	private Socket socket = null;
	String testMac = "200000000009";
	String testpassword = "1234";
	int deviceId = 	1490643466;
	
	@Before
	public void init() throws Exception {
		socket =  new Socket("127.0.0.1", ZrConfig.serverPort);
	}
	
	@Test
	public void autPayloadtest() throws Exception {
		ByteBuf buf = Unpooled.buffer();
		buf.writeByte(1).writeShort(2+testMac.length()+testpassword.length())
		.writeByte(testMac.length()).writeBytes(testMac.getBytes())
		.writeByte(testpassword.length()).writeBytes(testpassword.getBytes());
		byte[] outputBytes = new byte[buf.readableBytes()];
		buf.readBytes(outputBytes);
		socket.getOutputStream().write(outputBytes);
		socket.getOutputStream().flush();
		byte[] result = new byte[10];
		socket.getInputStream().read(result);
		System.out.println(Arrays.toString(result));
	}
	
	@Test
	public void datapointSyncTest() throws Exception {
		int index = 2;
		int type = 5;
		int dpLen = 4;
		int typeLen = (type&0x0F)<<12 | (dpLen&0x0FFF);
		int dpValue = 234;
		ByteBuf buf = Unpooled.buffer();
		ByteBuf payload = Unpooled.buffer();
		payload.writeInt(deviceId).writeByte(index).writeShort(typeLen).writeInt(dpValue);
		buf.writeByte(3).writeShort(payload.readableBytes()).writeBytes(payload, payload.readableBytes());
		
		byte[] outputBytes = new byte[buf.readableBytes()];
		buf.readBytes(outputBytes);
		socket.getOutputStream().write(outputBytes);
		socket.getOutputStream().flush();
		byte[] result = new byte[10];
		socket.getInputStream().read(result);
		System.out.println(Arrays.toString(result));
	}
	
}

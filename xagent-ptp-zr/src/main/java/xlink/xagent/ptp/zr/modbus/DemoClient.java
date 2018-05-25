package xlink.xagent.ptp.zr.modbus;

import com.serotonin.modbus4j.BatchRead;
import com.serotonin.modbus4j.BatchResults;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.locator.BaseLocator;
import com.serotonin.modbus4j.msg.ReadInputRegistersRequest;
import com.serotonin.modbus4j.msg.ReadInputRegistersResponse;

public class DemoClient {
    public static void main(String[] args) throws Exception {
        IpParameters ipParameters = new IpParameters();
        ipParameters.setHost("127.0.0.1");
        ipParameters.setPort(51108);
        ipParameters.setEncapsulated(true);
        ModbusFactory modbusFactory = new ModbusFactory();
        ModbusMaster master = modbusFactory.createTcpMaster(ipParameters, true);
        master.setTimeout(5000);
        master.setRetries(1);
        master.init();
//		NumericLocator el = new NumericLocator(1, RegisterRange.INPUT_REGISTER, 0, DataType.);
//		//NumericLocator fjk = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 3000, DataType.FOUR_BYTE_FLOAT);
//			try {
//				 System.out.println("FOUR_BYTE_INT_SIGNED " + master.getValue(el));
//			} catch (Exception e) {
//				e.printStackTrace();
//		}
        BatchRead<String> batchRead = new BatchRead<>();
        batchRead.addLocator("id1", BaseLocator.inputRegister(1, 0, DataType.TWO_BYTE_INT_SIGNED));
        BatchResults<String> results = master.send(batchRead);
        Short a = (Short) results.getValue("id1");
        System.out.println(a);
        ReadInputRegistersResponse response = (ReadInputRegistersResponse) master
                .send(new ReadInputRegistersRequest(1, 0, 10));
        System.out.println(response);
        short[] shortData = response.getShortData();
        for (int i = 0; i < shortData.length; i++) {
            System.out.println(shortData[i]);
        }
//                System.out.println("==================================================");
////                ByteQueue byteQueue = new ByteQueue(12);
////                response.write(byteQueue);
////                byte[] result = new byte[10];
////                byteQueue.peek(result,4,1);
////                System.out.println("收到的响应信息:"+result[0]);
//                System.out.println("收到的响应信息值:");
//            //    System.out.println("收到的响应信息大小2:"+byteQueue.size());

        master.destroy();
    }
}

package xlink.xagent.ptp.zr.modbus;

import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.locator.BaseLocator;

public class WriteDemo {
    public static void main(String[] args){
        IpParameters ipParameters = new IpParameters();
        ipParameters.setHost("127.0.0.1");
        ipParameters.setPort(51108);
        ipParameters.setEncapsulated(true);
        ModbusFactory modbusFactory = new ModbusFactory();
        ModbusMaster master = modbusFactory.createTcpMaster(ipParameters, true);
        master.setTimeout(5000);
        master.setRetries(1);
        try {
            master.init();
            BaseLocator ba = BaseLocator.holdingRegister(1,0, DataType.TWO_BYTE_INT_SIGNED);
            master.setValue(ba,10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

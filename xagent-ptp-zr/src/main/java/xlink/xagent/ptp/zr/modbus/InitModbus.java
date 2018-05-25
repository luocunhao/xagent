package xlink.xagent.ptp.zr.modbus;

import com.serotonin.modbus4j.ip.IpParameters;

public class InitModbus {

    private static final InitModbus singleton = new InitModbus();
    public static InitModbus instance(){
        return singleton;
    }

    private static IpParameters ipParameters = new IpParameters();

    public void init() {
        ipParameters.setHost("172.16.0.196");
        ipParameters.setPort(51108);
        ipParameters.setEncapsulated(true);
    }

    public static IpParameters getIpParameters() {

        return ipParameters;
    }

    public static void setIpParameters(IpParameters ipParameters) {

        InitModbus.ipParameters = ipParameters;
    }
}

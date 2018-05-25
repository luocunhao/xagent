package xlink.xagent.ptp.zr.main;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.ip.IpParameters;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xlink.cm.agent.extensions.XagentApi;
import xlink.cm.agent.ptp.PtpCertificateType;
import xlink.cm.agent.ptp.PtpServer;
import xlink.cm.agent.ptp.PtpServerStrategy;
import xlink.xagent.ptp.zr.codec.ZrDecoder;
import xlink.xagent.ptp.zr.codec.ZrEncoder;
import xlink.xagent.ptp.zr.modbus.InitModbus;
import xlink.xagent.ptp.zr.modbus.StartQuartz;
import xlink.xagent.ptp.zr.processor.BusinessLogicProcessor;
import xlink.xagent.ptp.zr.processor.DatapointDataHandler;


public class ZrPlugin extends Plugin {

    private static final Logger logger = LoggerFactory.getLogger(ZrPlugin.class);

    // ptp server 对象
    public static PtpServer ptpServer;
    // xagent操作对象
    private static XagentApi xagent;
    private PluginWrapper wrapper;
    public static ModbusMaster master;
    public ZrPlugin(PluginWrapper wrapper) {
        super(wrapper);
        this.wrapper = wrapper;
    }


    @Override
    public void start() {
        try {
            xagent = this.wrapper.getPluginManager().getExtensions(XagentApi.class).get(0);
            ptpServer =
                    xagent.createServerWithCertType(this.wrapper.getPluginId(), ZrConfig.certId, ZrConfig.certKey, PtpCertificateType.Certificate, ZrConfig.serverPort, new ZrDecoder(), new ZrEncoder(),
                            new BusinessLogicProcessor(), 60, PtpServerStrategy.TCP);
            xagent.setDatapointSetListener(new DatapointDataHandler());
            ptpServer.startServer();
            IpParameters ipParameters = InitModbus.getIpParameters();
            ipParameters.setHost(ZrConfig.serial_host);
            ipParameters.setPort(ZrConfig.serial_port);
            ipParameters.setEncapsulated(true);
            ModbusFactory modbusFactory = new ModbusFactory();
            master = modbusFactory.createTcpMaster(ipParameters, true);
            DeviceConfig.instance().init();
            StartQuartz.instance().init();
        } catch (Exception e) {
//      // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static PtpServer getPtpServer() {
        return ptpServer;
    }

    public static XagentApi getXagentApi() {
        return xagent;
    }
    public static ModbusMaster getModbusMaster(){return master;}
}

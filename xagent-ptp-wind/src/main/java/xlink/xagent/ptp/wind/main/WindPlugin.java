package xlink.xagent.ptp.wind.main;

import xlink.xagent.ptp.wind.codec.WindDecoder;
import xlink.xagent.ptp.wind.codec.WindEncoder;
import xlink.xagent.ptp.wind.domain.MacAddressIdMap;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xlink.xagent.ptp.wind.processor.BusinessLogicProcessor;
import xlink.xagent.ptp.wind.processor.DatapointDataHandler;
import xlink.xagent.ptp.wind.tcp.TcpClient;
import xlink.cm.agent.extensions.XagentApi;
import xlink.cm.agent.ptp.PtpCertificateType;
import xlink.cm.agent.ptp.PtpServer;
import xlink.cm.agent.ptp.PtpServerStrategy;

import java.util.Map;


public class WindPlugin extends Plugin {
    private static final Logger logger = LoggerFactory.getLogger(WindPlugin.class);
    // ptp server 对象
    public static PtpServer ptpServer;
    // xagent操作对象
    private static XagentApi xagent;
    private PluginWrapper wrapper;

    public WindPlugin(PluginWrapper wrapper) {
        super(wrapper);
        this.wrapper = wrapper;
    }
    @Override
    public void start() {
        try {
            xagent = this.wrapper.getPluginManager().getExtensions(XagentApi.class).get(0);
            ptpServer =
                    xagent.createServerWithCertType(this.wrapper.getPluginId(), WindConfig.certId, WindConfig.certKey, PtpCertificateType.Certificate, WindConfig.serverPort, new WindDecoder(), new WindEncoder(),
                            new BusinessLogicProcessor(), 60, PtpServerStrategy.TCP);
            xagent.setDatapointSetListener(new DatapointDataHandler());
            ptpServer.startServer();
            DeviceConfig.instance().init();
            TcpClient tcpClient = new TcpClient(WindConfig.serial_host, WindConfig.serial_port);
            boolean connect = tcpClient.connect(5000, true);
            if (connect) {
                Map<String, MacAddressIdMap> deviceMap = DeviceConfig.getDeviceMap();
                for (String key : deviceMap.keySet()) {
                    MacAddressIdMap macAddressIdMap = deviceMap.get(key);
                    xagent.activateDevice(WindConfig.productId, macAddressIdMap.getMac(), (byte) 0, 0, (byte) 0, 0, macAddressIdMap.getSn(), 0, null);
                    xagent.deviceOnline(macAddressIdMap.getDevice_id(), "");
                    String cmdST4 = "^P"+key+"ST4";
                    String cmdST6 = "^P"+key+"ST6";
                    int cmdST4length = cmdST4.getBytes().length;
                    int cmdST6length = cmdST6.getBytes().length;
                    try {
                        final boolean cmdST4ret = tcpClient.send(cmdST4length, cmdST4.getBytes());
                        final boolean cmdST6ret = tcpClient.send(cmdST6length, cmdST6.getBytes());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
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
}

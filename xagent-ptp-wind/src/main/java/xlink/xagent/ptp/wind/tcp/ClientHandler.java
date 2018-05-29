package xlink.xagent.ptp.wind.tcp;
import xlink.xagent.ptp.wind.domain.MacAddressIdMap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import xlink.xagent.ptp.wind.main.DeviceConfig;
import xlink.xagent.ptp.wind.main.WindPlugin;
import xlink.xagent.ptp.wind.utils.DataTransUtils;
import xlink.xagent.ptp.wind.utils.StrUtil;
import xlink.cm.agent.extensions.XagentApi;
import xlink.cm.agent.ptp.dataStruture.DPtpDatapoint;
import xlink.cm.agent.ptp.dataStruture.XlinkDeviceDatapointType;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientHandler extends SimpleChannelInboundHandler<byte[]> {

    public ChannelHandlerContext ctx;
    private Map<Integer, String> response = new ConcurrentHashMap<>();
    private final Map<Integer, Thread> waiters = new ConcurrentHashMap<>();
    private final AtomicInteger sequence = new AtomicInteger();
    private static boolean result = false;
    public String host;
    public int port;
    public long timeout;
    public boolean isNonblock;

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public Map<Integer, String> getResponse() {
        return response;
    }

    public void setResponse(Map<Integer, String> response) {
        this.response = response;
    }

    public Map<Integer, Thread> getWaiters() {
        return waiters;
    }

    public AtomicInteger getSequence() {
        return sequence;
    }

    public static boolean isResult() {
        return result;
    }

    public static void setResult(boolean result) {
        ClientHandler.result = result;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public boolean isNonblock() {
        return isNonblock;
    }

    public void setNonblock(boolean nonblock) {
        isNonblock = nonblock;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
        // TODO
        //获取并解析byte[]
        String hexStr = StrUtil.bytesToHexString(msg);
        String recv_string = StrUtil.convertHexToString(hexStr);
        System.out.println(recv_string);//^D609000,0,0,0
        /**
         * =======================================解析响应字符串===============================================
         * */
        String function = recv_string.substring(2, 5);
        String addressId = recv_string.substring(5, 8);
        XagentApi xagent = WindPlugin.getXagentApi();
        Map<Integer, DPtpDatapoint> map = new HashMap<>();
        Map<String, MacAddressIdMap> deviceMap = DeviceConfig.getDeviceMap();
        MacAddressIdMap macAddressIdMap = deviceMap.get(addressId);
        //xagent.deviceOnline(macAddressIdMap.getDevice_id(), "");
        switch (function) {
            case "014":{
                String[] strs = recv_string.split(",");
                String b = strs[1];
                if("3".equals(b)){
                    //电网三相电压
                    float three_phase_V = Float.parseFloat(strs[3]);
                    DPtpDatapoint dPtpDatapoint3 = new DPtpDatapoint(3,three_phase_V,XlinkDeviceDatapointType.Float);
                    map.put(3,dPtpDatapoint3);
                }
                break;
            }
            case "117":{
                break;
            }
            case "219":{
                //电网频率
                String[] strs = recv_string.split(",");
                float freq = Float.parseFloat(strs[4])*0.1f;
                DPtpDatapoint dPtpDatapoint4 = new DPtpDatapoint(4,freq,XlinkDeviceDatapointType.Float);
                map.put(4,dPtpDatapoint4);
                break;
            }
            case "318":{
                break;
            }
            case "609": {
                //运行状态
                String status = recv_string.split(",")[3];
                DPtpDatapoint dPtpDatapoint0 = new DPtpDatapoint(0, status, XlinkDeviceDatapointType.String);
                map.put(0, dPtpDatapoint0);
                break;
            }
            case "416": {
                String[] values = recv_string.split(",");
                //瞬时发电功率
                float power = Float.parseFloat(values[1]);
                float roundPower = DataTransUtils.round(power*0.1f,2, BigDecimal.ROUND_HALF_UP);
                DPtpDatapoint dPtpDatapoint1 = new DPtpDatapoint(1, roundPower, XlinkDeviceDatapointType.Float);
                map.put(1, dPtpDatapoint1);
                //发电量
                float enegry = Float.parseFloat(values[2]);
                float roundEnegry = DataTransUtils.round(enegry,2,BigDecimal.ROUND_HALF_UP);
                DPtpDatapoint dPtpDatapoint2 = new DPtpDatapoint(2, roundEnegry, XlinkDeviceDatapointType.Float);
                map.put(2, dPtpDatapoint2);
                break;
            }
            default: {
                break;
            }
        }
        if (map.size() > 0) {
            xagent.datapointSync(macAddressIdMap.getDevice_id(), new Date(), map);
        }
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.err.println("成功连接服务器");
        this.ctx = ctx;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        TcpClient tcpClient = new TcpClient(this.host, this.port);
        tcpClient.connect(timeout, true);
    }
}
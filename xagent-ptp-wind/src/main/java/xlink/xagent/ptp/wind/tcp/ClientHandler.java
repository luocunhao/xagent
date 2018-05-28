package xlink.xagent.ptp.wind.tcp;

import xlink.xagent.ptp.wind.domain.MacAddressIdMap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import xlink.xagent.ptp.wind.main.DeviceConfig;
import xlink.xagent.ptp.wind.main.WindPlugin;
import xlink.xagent.ptp.wind.utils.StrUtil;
import xlink.cm.agent.extensions.XagentApi;
import xlink.cm.agent.ptp.dataStruture.DPtpDatapoint;
import xlink.cm.agent.ptp.dataStruture.XlinkDeviceDatapointType;

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
        switch (function) {
            case "609": {
                //运行状态
                int status = Integer.parseInt(recv_string.split(",")[3]);
                DPtpDatapoint dPtpDatapoint0 = new DPtpDatapoint(0, status, XlinkDeviceDatapointType.BoolByte);
                map.put(0, dPtpDatapoint0);
                break;
            }
            case "416": {
                String[] values = recv_string.split(",");
                //功率
                int power = Integer.parseInt(values[1]);
                DPtpDatapoint dPtpDatapoint1 = new DPtpDatapoint(1, (float) (power * 0.1), XlinkDeviceDatapointType.Float);
                map.put(1, dPtpDatapoint1);
                //发电量
                int enegry = Integer.parseInt(values[2]);
                DPtpDatapoint dPtpDatapoint2 = new DPtpDatapoint(2, (float) enegry, XlinkDeviceDatapointType.Float);
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
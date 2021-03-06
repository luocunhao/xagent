package xlink.xagent.ptp.zr.modbus;

import com.serotonin.modbus4j.BatchRead;
import com.serotonin.modbus4j.BatchResults;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.locator.BaseLocator;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import xlink.cm.agent.extensions.XagentApi;
import xlink.cm.agent.ptp.dataStruture.*;
import xlink.xagent.ptp.zr.domain.MacSlaverIdMap;
import xlink.xagent.ptp.zr.main.ZrConfig;
import xlink.xagent.ptp.zr.main.ZrPlugin;
import xlink.xagent.ptp.zr.main.DeviceConfig;
import xlink.xagent.ptp.zr.utils.DataTransUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RequestQuartz implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("===========================设备定时任务启动==========================");
        try {
            ModbusMaster master = ZrPlugin.getModbusMaster();
            master.setTimeout(5000);
            master.setRetries(1);
            master.init();
            XagentApi xagent = ZrPlugin.getXagentApi();
            Map<Integer, MacSlaverIdMap> deviceMap = DeviceConfig.getDeviceMap();
            for (Integer key : deviceMap.keySet()) {
                MacSlaverIdMap macSlaverIdMap = deviceMap.get(key);
                int slaverId = macSlaverIdMap.getSlaverid();
                BatchRead<String> batchRead = new BatchRead<>();
                batchRead.addLocator("inverter_status", BaseLocator.inputRegister(slaverId, 0, DataType.TWO_BYTE_INT_SIGNED));
                batchRead.addLocator("input_power_H", BaseLocator.inputRegister(slaverId, 1, DataType.TWO_BYTE_INT_SIGNED));
                batchRead.addLocator("input_power_L", BaseLocator.inputRegister(slaverId, 2, DataType.TWO_BYTE_INT_SIGNED));
                batchRead.addLocator("output_power_H", BaseLocator.inputRegister(slaverId, 11, DataType.TWO_BYTE_INT_SIGNED));
                batchRead.addLocator("output_power_L", BaseLocator.inputRegister(slaverId, 12, DataType.TWO_BYTE_INT_SIGNED));
                batchRead.addLocator("gird_freq", BaseLocator.inputRegister(slaverId, 13, DataType.TWO_BYTE_INT_SIGNED));
                batchRead.addLocator("vac1", BaseLocator.inputRegister(slaverId, 14, DataType.TWO_BYTE_INT_SIGNED));
                batchRead.addLocator("lac1", BaseLocator.inputRegister(slaverId, 15, DataType.TWO_BYTE_INT_SIGNED));
                batchRead.addLocator("vac2", BaseLocator.inputRegister(slaverId, 18, DataType.TWO_BYTE_INT_SIGNED));
                batchRead.addLocator("lac2", BaseLocator.inputRegister(slaverId, 19, DataType.TWO_BYTE_INT_SIGNED));
                batchRead.addLocator("vac3", BaseLocator.inputRegister(slaverId, 22, DataType.TWO_BYTE_INT_SIGNED));
                batchRead.addLocator("lac3", BaseLocator.inputRegister(slaverId, 23, DataType.TWO_BYTE_INT_SIGNED));
                batchRead.addLocator("energy_totay_H", BaseLocator.inputRegister(slaverId, 26, DataType.TWO_BYTE_INT_SIGNED));
                batchRead.addLocator("energy_totay_L", BaseLocator.inputRegister(slaverId, 27, DataType.TWO_BYTE_INT_SIGNED));
                batchRead.addLocator("energy_total_H", BaseLocator.inputRegister(slaverId, 28, DataType.TWO_BYTE_INT_SIGNED));
                batchRead.addLocator("energy_total_L", BaseLocator.inputRegister(slaverId, 29, DataType.TWO_BYTE_INT_SIGNED));
                batchRead.addLocator("time_total_H", BaseLocator.inputRegister(slaverId, 30, DataType.TWO_BYTE_INT_SIGNED));
                batchRead.addLocator("time_total_L", BaseLocator.inputRegister(slaverId, 31, DataType.TWO_BYTE_INT_SIGNED));
                batchRead.addLocator("temperature", BaseLocator.inputRegister(slaverId, 32, DataType.TWO_BYTE_INT_SIGNED));
                // batchRead.addLocator("on_off",BaseLocator.holdingRegister(slaverId,0,DataType.TWO_BYTE_INT_SIGNED));
                batchRead.addLocator("power_factor",BaseLocator.holdingRegister(slaverId,5,DataType.TWO_BYTE_INT_SIGNED));
                BatchResults<String> results = master.send(batchRead);
                System.out.println("没有类型转换之前=========" + results.toString());
                Short inverter_status = (Short) results.getValue("inverter_status");
                Short input_power_H = (Short) results.getValue("input_power_H");
                Short input_power_L = (Short) results.getValue("input_power_L");
                Short output_power_H = (Short) results.getValue("output_power_H");
                Short output_power_L = (Short) results.getValue("output_power_L");
                Short gird_freq = (Short) results.getValue("gird_freq");
                Short vac1 = (Short)results.getValue("vac1");
                Short lac1 = (Short)results.getValue("lac1");
                Short vac2 = (Short)results.getValue("vac2");
                Short lac2 = (Short)results.getValue("lac2");
                Short vac3 = (Short)results.getValue("vac3");
                Short lac3 = (Short)results.getValue("lac3");
                Short energy_totay_H = (Short) results.getValue("energy_totay_H");
                Short energy_totay_L = (Short) results.getValue("energy_totay_L");
                Short energy_total_H = (Short) results.getValue("energy_total_H");
                Short energy_total_L = (Short) results.getValue("energy_total_L");
                Short time_total_H = (Short) results.getValue("time_total_H");
                Short time_total_L = (Short) results.getValue("time_total_L");
                Short temperature = (Short) results.getValue("temperature");
                Short on_off = (Short) results.getValue("on_off");
                Short power_factor = (Short)results.getValue("power_factor");
                Map<Integer, DPtpDatapoint> map = new HashMap<>();
                //逆变器运行状态 0:等待状态, 1:正常, 3:报 错状态
                DPtpDatapoint dPtpDatapoint0 = new DPtpDatapoint(0, inverter_status, XlinkDeviceDatapointType.BoolByte);
                //输入功率高位
                //DPtpDatapoint dPtpDatapoint1 = new DPtpDatapoint(1, input_power_H.intValue(), XlinkDeviceDatapointType.Int16);
                //输入功率低位
                //DPtpDatapoint dPtpDatapoint2 = new DPtpDatapoint(2, input_power_L.intValue(), XlinkDeviceDatapointType.Int16);
                //输出功率高位
                //DPtpDatapoint dPtpDatapoint11 = new DPtpDatapoint(11, output_power_H.intValue(), XlinkDeviceDatapointType.Int16);
                //输出功率低位
                //DPtpDatapoint dPtpDatapoint12 = new DPtpDatapoint(12, output_power_L.intValue(), XlinkDeviceDatapointType.Int16);
                //电网频率
                DPtpDatapoint dPtpDatapoint13 = new DPtpDatapoint(13, gird_freq.intValue(), XlinkDeviceDatapointType.Int16);
                DPtpDatapoint dPtpDatapoint14 = new DPtpDatapoint(14, vac1*0.1f, XlinkDeviceDatapointType.Float);
                DPtpDatapoint dPtpDatapoint15 = new DPtpDatapoint(15, lac1*0.1f, XlinkDeviceDatapointType.Float);
                DPtpDatapoint dPtpDatapoint18 = new DPtpDatapoint(18, vac2*0.1f, XlinkDeviceDatapointType.Float);
                DPtpDatapoint dPtpDatapoint19 = new DPtpDatapoint(19, lac2*0.1f, XlinkDeviceDatapointType.Float);
                DPtpDatapoint dPtpDatapoint22 = new DPtpDatapoint(22, vac3*0.1f, XlinkDeviceDatapointType.Float);
                DPtpDatapoint dPtpDatapoint23 = new DPtpDatapoint(23, lac3*0.1f, XlinkDeviceDatapointType.Float);
                //今天发电量高位
                //DPtpDatapoint dPtpDatapoint26 = new DPtpDatapoint(26, energy_totay_H.intValue(), XlinkDeviceDatapointType.Int16);
                //今天发电量低位
                //DPtpDatapoint dPtpDatapoint27 = new DPtpDatapoint(27, energy_totay_L.intValue(), XlinkDeviceDatapointType.Int16);
                //总发电量高位
                //DPtpDatapoint dPtpDatapoint28 = new DPtpDatapoint(28, energy_total_H.intValue(), XlinkDeviceDatapointType.Int16);
                //总发电量低位
                //DPtpDatapoint dPtpDatapoint29 = new DPtpDatapoint(29, energy_total_L.intValue(), XlinkDeviceDatapointType.Int16);
                //总发电时长高位
                //DPtpDatapoint dPtpDatapoint30 = new DPtpDatapoint(30, time_total_H.intValue(), XlinkDeviceDatapointType.Int16);
                //总发电时长低位
                //DPtpDatapoint dPtpDatapoint31 = new DPtpDatapoint(31, time_total_L.intValue(), XlinkDeviceDatapointType.Int16);
                //逆变器温度
                float roundTemperature = DataTransUtils.round(temperature.intValue() * 0.1f, 2, BigDecimal.ROUND_HALF_UP);
                DPtpDatapoint dPtpDatapoint32 = new DPtpDatapoint(32, roundTemperature, XlinkDeviceDatapointType.Float);
                //输入总功率
                float roundInputPower = DataTransUtils.round(DataTransUtils.HighAndLow(input_power_H, input_power_L) * 0.1f,
                        2, BigDecimal.ROUND_HALF_UP);
                DPtpDatapoint dPtpDatapoint33 = new DPtpDatapoint(33, roundInputPower, XlinkDeviceDatapointType.Float);
                //输出总功率
                float roundOutputPower = DataTransUtils.round(DataTransUtils.HighAndLow(output_power_H, output_power_L) * 0.1f,
                        2, BigDecimal.ROUND_HALF_UP);
                DPtpDatapoint dPtpDatapoint34 = new DPtpDatapoint(34, roundOutputPower, XlinkDeviceDatapointType.Float);
                //今天总发电量
                float roundEnergyToday = DataTransUtils.round(DataTransUtils.HighAndLow(energy_totay_H, energy_totay_L) * 0.1f,
                        2, BigDecimal.ROUND_HALF_UP);
                DPtpDatapoint dPtpDatapoint35 = new DPtpDatapoint(35, roundEnergyToday, XlinkDeviceDatapointType.Float);
                //历史总发电量
                float roundEnergyTotal = DataTransUtils.round(DataTransUtils.HighAndLow(energy_total_H, energy_total_L) * 0.1f,
                        2, BigDecimal.ROUND_HALF_UP);
                DPtpDatapoint dPtpDatapoint36 = new DPtpDatapoint(36, roundEnergyTotal, XlinkDeviceDatapointType.Float);
                //历史总工作时间
                float roundTimeTotal = DataTransUtils.seconds2hour(DataTransUtils.HighAndLow(time_total_H, time_total_L) / 2);
                DPtpDatapoint dPtpDatapoint37 = new DPtpDatapoint(37, roundTimeTotal, XlinkDeviceDatapointType.Float);
                //开关状态
                //DPtpDatapoint dPtpDatapoint38 = new DPtpDatapoint(38,on_off,XlinkDeviceDatapointType.BoolByte);
                //功率因数
                DPtpDatapoint dPtpDatapoint39 = new DPtpDatapoint(39,power_factor.intValue(),XlinkDeviceDatapointType.UnSignedInt16);
                map.put(0, dPtpDatapoint0);
                //map.put(1, dPtpDatapoint1);
                //map.put(2, dPtpDatapoint2);
                //map.put(3, dPtpDatapoint11);
                //map.put(12, dPtpDatapoint12);
                map.put(13, dPtpDatapoint13);
                map.put(14, dPtpDatapoint14);
                map.put(15, dPtpDatapoint15);
                map.put(18, dPtpDatapoint18);
                map.put(19, dPtpDatapoint19);
                map.put(22, dPtpDatapoint22);
                map.put(23, dPtpDatapoint23);
                //map.put(26, dPtpDatapoint26);
                //map.put(27, dPtpDatapoint27);
                //map.put(28, dPtpDatapoint28);
                //map.put(29, dPtpDatapoint29);
                //map.put(30, dPtpDatapoint30);
                //map.put(31, dPtpDatapoint31);
                map.put(32, dPtpDatapoint32);
                map.put(33, dPtpDatapoint33);
                map.put(34, dPtpDatapoint34);
                map.put(35, dPtpDatapoint35);
                map.put(36, dPtpDatapoint36);
                map.put(37, dPtpDatapoint37);
                //map.put(38,dPtpDatapoint38);
                map.put(39,dPtpDatapoint39);
                System.out.println("results======================   " + results.toString());
                System.out.println("keyset size :=================" + map.keySet().size());
                xagent.activateDevice(ZrConfig.productId, macSlaverIdMap.getMac(), (byte) 0, 0, (byte) 0, 0, macSlaverIdMap.getSn(), 0, null);
                xagent.deviceOnline(key, "");
                xagent.datapointSync(key, new Date(), map);
            }
            master.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

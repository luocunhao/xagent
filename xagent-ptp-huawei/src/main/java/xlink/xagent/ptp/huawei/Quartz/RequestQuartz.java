package xlink.xagent.ptp.huawei.Quartz;

import cn.xlink.iot.sdk.datastruct.XlinkIotPublishModel;
import cn.xlink.iot.sdk.exception.XlinkIotException;
import cn.xlink.iot.sdk.operator.XlinkIotPublish;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import xlink.xagent.ptp.huawei.config.XagentConfig;
import xlink.xagent.ptp.huawei.huaweiapi.HwApi;
import xlink.xagent.ptp.huawei.huaweiapi.QueryApList;
import xlink.xagent.ptp.huawei.main.HwPlugin;

public class RequestQuartz implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("=========================数据同步任务=============================");
        XlinkIotPublish xlinkIotPublish =  HwPlugin.getXlinkIotPublish();
        HwApi hwApi = new QueryApList();
        XlinkIotPublishModel apListPushModel = hwApi.query(XagentConfig.host,XagentConfig.port,XagentConfig.openId);
        try {
            xlinkIotPublish.publishToXlinkIotAsync(apListPushModel);
        } catch (XlinkIotException e) {
            e.printStackTrace();
        }
    }
}

package xlink.xagent.ptp.huawei.Quartz;

import org.apache.http.HttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import xlink.xagent.ptp.huawei.config.RequestMethod;
import xlink.xagent.ptp.huawei.config.UrlConfig;
import xlink.xagent.ptp.huawei.config.XagentConfig;
import xlink.xagent.ptp.huawei.utils.HttpsClient;
import xlink.xagent.ptp.huawei.utils.ParseResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OpenIdQuartz implements Job{
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("===========================获取openid定时任务==========================");
        //set parameters
        List<BasicNameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("userid", XagentConfig.username));
        parameters.add(new BasicNameValuePair("value", XagentConfig.password));
        parameters.add(new BasicNameValuePair("ipaddr", XagentConfig.userip));
        try {
            HttpResponse response = HttpsClient.access(XagentConfig.host,XagentConfig.port, UrlConfig.openidURL,
                    RequestMethod.PUT,null,parameters);
            String ret = HttpsClient.getResult(response);
            System.out.println("=============================ret===============================");
            System.out.println(ret);
            Map<String, String> retMap = ParseResponse.parseResponse(ret);
            if(retMap.get("code").equals("0")){
                String openid = retMap.get("data");
                XagentConfig.openId = openid;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

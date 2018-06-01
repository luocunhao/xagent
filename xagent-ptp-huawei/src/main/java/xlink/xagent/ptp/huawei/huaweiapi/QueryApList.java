package xlink.xagent.ptp.huawei.huaweiapi;

import cn.xlink.iot.sdk.datastruct.XlinkIotPublishModel;
import cn.xlink.iot.sdk.datastruct.type.XlinkIotPublishOperation;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicNameValuePair;
import xlink.xagent.ptp.huawei.config.RequestMethod;
import xlink.xagent.ptp.huawei.config.UrlConfig;
import xlink.xagent.ptp.huawei.config.XagentConfig;
import xlink.xagent.ptp.huawei.utils.HttpsClient;
import xlink.xagent.ptp.huawei.utils.ParseResponse;

import java.util.HashMap;
import java.util.Map;

public class QueryApList implements HwApi {
    @Override
    public XlinkIotPublishModel query(String host, int port, String openid) {
        XlinkIotPublishModel publishModel = null;
        String url = UrlConfig.aplistURL;
        //set headers
        BasicNameValuePair[] headers = {new BasicNameValuePair("openid", openid)};
        //set parameters
        BasicNameValuePair[] parameters =
                {
                        new BasicNameValuePair("pageSize ", "1"),
                        new BasicNameValuePair("start ", "1")
                };

        try {
            HttpResponse response = HttpsClient.access(host, port, url, RequestMethod.GET, headers, parameters);
            String ret = HttpsClient.getResult(response);
            System.out.println("========================aplist ret=========================");
            Map<String, String> retMap = ParseResponse.parseResponse(ret);
            String dataString = retMap.get("data");
            JSONObject dataJson = JSONObject.parseObject(dataString);
            //构建publishmodel
            publishModel = new XlinkIotPublishModel();
            publishModel.setServiceId("intelligent_electricity_meter");
            publishModel.setObjectName("electricity_meter");
            publishModel.setOperation(XlinkIotPublishOperation.Insert);
            publishModel.setProductId(XagentConfig.productId);
            Map<String, Object> data = new HashMap<>();
            data.put("id", dataJson.get(""));
            data.put("online_status", dataJson.get(""));
            data.put("reading", dataJson.get(""));
            publishModel.setData(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return publishModel;
    }
}

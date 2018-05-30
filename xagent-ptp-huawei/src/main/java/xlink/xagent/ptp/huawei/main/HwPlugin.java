package xlink.xagent.ptp.huawei.main;

import cn.xlink.iot.sdk.XlinkIot;
import cn.xlink.iot.sdk.XlinkIotBuilder;
import cn.xlink.iot.sdk.datastruct.XlinkIotPublishModel;
import cn.xlink.iot.sdk.datastruct.type.XlinkIotPublishOperation;
import cn.xlink.iot.sdk.exception.XlinkIotException;
import cn.xlink.iot.sdk.operator.XlinkIotPublish;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xlink.xagent.ptp.huawei.config.XagentConfig;

import java.util.HashMap;
import java.util.Map;

public class HwPlugin extends Plugin {
    private static final Logger logger = LoggerFactory.getLogger(HwPlugin.class);
    private PluginWrapper wrapper;

    public HwPlugin(PluginWrapper wrapper) {
        super(wrapper);
        this.wrapper = wrapper;
    }

    @Override
    public void start() {
        //创建一个客户端构造器
        XlinkIotBuilder builder = new XlinkIotBuilder();
        //设置凭证和地址
        builder.setAppId(XagentConfig.appId)
                .setAppSecret(XagentConfig.appSecret)
                .setEndPoint(XagentConfig.endPoint);
        //创建一个http连接的客户端
        XlinkIot xlinkIot = builder.buildXlinkIotHttpClient();
        //1.创建一个publish实例
        XlinkIotPublish xlinkIotPublish = new XlinkIotPublish(xlinkIot);
        //2.构建publishmodel
        XlinkIotPublishModel publishModel = new XlinkIotPublishModel();
        publishModel.setServiceId("intelligent_electricity_meter");
        publishModel.setObjectName("electricity_meter");
        publishModel.setOperation(XlinkIotPublishOperation.Insert);
        publishModel.setProductId(XagentConfig.productId);
        Map<String,Object> data = new HashMap<>();
        data.put("id","104808747439");
        data.put("online_status",1);
        data.put("reading",10.5f);
        publishModel.setData(data);
        try {
            xlinkIotPublish.publishToXlinkIot(publishModel);
        } catch (XlinkIotException e) {
            e.printStackTrace();
        }
    }
}

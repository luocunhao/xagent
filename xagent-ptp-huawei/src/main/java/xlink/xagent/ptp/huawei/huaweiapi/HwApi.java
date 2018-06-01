package xlink.xagent.ptp.huawei.huaweiapi;

import cn.xlink.iot.sdk.datastruct.XlinkIotPublishModel;

public interface HwApi {
    XlinkIotPublishModel query(String host,int port,String openid);
}

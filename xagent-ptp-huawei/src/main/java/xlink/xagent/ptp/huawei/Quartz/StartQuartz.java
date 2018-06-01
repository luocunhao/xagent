package xlink.xagent.ptp.huawei.Quartz;

public class StartQuartz {
    private static String POST_MODBUS_REQUEST = "定时发送请求";
    private static final StartQuartz singleton = new StartQuartz();

    public static StartQuartz instance() {
        return singleton;
    }

    public void init() {
        QuartzManager.addJob(POST_MODBUS_REQUEST, OpenIdQuartz.class, " */10 *  * * * ?");
    }
}

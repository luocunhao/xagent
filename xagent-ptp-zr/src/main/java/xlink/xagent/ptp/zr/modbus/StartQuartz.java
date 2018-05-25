package xlink.xagent.ptp.zr.modbus;

public class StartQuartz {
    private static String POST_MODBUS_REQUEST    = "定时发送请求";
    private static final StartQuartz singleton = new StartQuartz();
    public static StartQuartz instance(){
        return singleton;
    }
    public void init(){
        QuartzManager.addJob(POST_MODBUS_REQUEST, RequestQuartz.class, " */10 *  * * * ?");
    }
}


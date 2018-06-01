package xlink.xagent.ptp.huawei.config;
public enum RequestMethod {
    PUT("PUT"),
    GET("GET"),
    POST("POST"),
    DELETE("DELETE"),
    UNKNOWN("UNKNOWN");
    private final String desc;
    public String desc(){return desc;}
    RequestMethod(String d){this.desc = d;}
    public static final RequestMethod from(String desc){
        for(RequestMethod method:values()){
            if(method.desc().equals(desc)){
                return method;
            }
        }
        return UNKNOWN;
    }
}

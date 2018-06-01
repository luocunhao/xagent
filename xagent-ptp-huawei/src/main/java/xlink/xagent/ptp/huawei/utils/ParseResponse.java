package xlink.xagent.ptp.huawei.utils;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ParseResponse {
    public static Map<String,String> parseResponse(String input){
        Map<String,String> retMap = new HashMap<>();
        JSONObject jObject = JSONObject.parseObject(input);
        if (null != jObject.get("code"))
        {
            String i = jObject.get("code").toString();
            retMap.put("code", i);
        }
        if (null != jObject.get("data"))
        {
            String data = jObject.get("data").toString();
            retMap.put("data", data);
        }
        if (null != jObject.get("description"))
        {
            String des = jObject.get("description").toString();
            retMap.put("description", des);
        }
        if (null != jObject.get("result"))
        {
            String res = jObject.get("result").toString();
            retMap.put("result", res);
        }
        return retMap;
    }
}

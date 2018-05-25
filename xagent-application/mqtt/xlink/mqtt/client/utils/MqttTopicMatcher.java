package xlink.mqtt.client.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MqttTopicMatcher {
	
	private static final String PARAM_PATTERN_STR = "\\{(\\w*?)\\}";
	
	private static Pattern PARAM_PATTERN = Pattern.compile(PARAM_PATTERN_STR);

	private  Pattern topic_pattern;
	
	private List<String> parmNames;
	
	
	
	public MqttTopicMatcher(String topicPattern){
		
		Matcher m = PARAM_PATTERN.matcher(topicPattern);
		parmNames = new ArrayList<String> ();
		while(m.find()){
				parmNames.add(m.group(1));

		}
    String patternStr = topicPattern.replace("$", "\\$").replaceAll("/", "\\\\/")
        .replaceAll(PARAM_PATTERN_STR, "(\\\\w*)");
		topic_pattern = Pattern.compile(patternStr);
	}
	
	public boolean match(String text){
		Matcher m = topic_pattern.matcher(text);
		if(m.matches()){
			return true;
		}
		return false;
	}
	
	
	public String getParamValue(String text,String paramName){
		Matcher m = topic_pattern.matcher(text);
		if(m.matches()){
			Map<String,String> paramMap = new HashMap<String,String>();
			for(int i=0;i<m.groupCount();i++){
				paramMap.put(parmNames.get(i), m.group(i+1));
				
			}
			return paramMap.get(paramName);
		}
		return null;
	}
}

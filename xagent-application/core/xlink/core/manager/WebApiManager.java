package xlink.core.manager;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import xlink.agent.main.Args;
import xlink.cm.agent.ptp.exception.DeviceNotFoundException;
import xlink.core.derby.dataStructure.DeviceInfo;
import xlink.core.okhttp.OKHttp;
import xlink.core.okhttp.datastruct.OKHttpResponse;

public class WebApiManager {

	private static final Logger logger = LoggerFactory.getLogger(WebApiManager.class);

	private static final WebApiManager singleton = new WebApiManager();

	private static volatile String XLINK_ACCESS_TOKEN = null;

	private WebApiManager() {

	}

	public static WebApiManager instance() {
		return singleton;
	}

	public DeviceInfo getDevice(String productId, String identify, String AccessToken) throws Exception {
		String url = Args.XLINK_HOST + WebApi.XLINK_PRODUCT_DEVICES.replaceAll("\\{product_id\\}", productId);
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		headers.put("Access-Token", AccessToken);

		JSONObject body = new JSONObject();
		JSONObject query = new JSONObject();
		JSONObject field = new JSONObject();

		field.put("$like", identify);
		query.put("mac", field);
		body.put("query", query);
		JSONArray filters = new JSONArray();
		filters.add("id");
		filters.add("mac");
		body.put("filter", filters);
		try {
			OKHttpResponse response = OKHttp.instance().post(url, body.toJSONString(), headers, null);
			logger.info("response is: " + response);
			if (response.getHttpReturnCode() == 200) {
				JSONObject json = JSONObject.parseObject(response.getContent());
				int count = json.getIntValue("count");
				if (count > 0) {
					JSONObject item = json.getJSONArray("list").getJSONObject(0);
					String mac = item.getString("mac");
					int deviceId = item.getIntValue("id");
					return new DeviceInfo(deviceId, productId, mac, "", "", false, new Date(System.currentTimeMillis()));
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		throw new DeviceNotFoundException("device not found");
	}
}

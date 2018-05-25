package xlink.mqtt.client.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xlink.core.utils.StringTools;

public class MqttConfig {

	private static final Logger logger = LoggerFactory.getLogger(MqttConfig.class);


	public static String BROKER_URI;
  // [product environment]
  // public static String BROKER_HOST = "mqtt.xlink.cn";
  //public static String BROKER_HOST = "114.55.250.242";
  // [biguiyuan environment]
   public static String BROKER_HOST = "iotcm.bgycc.com";
  // [develop environment]
 //  public static String BROKER_HOST = "54.222.229.62";
	public static int BROKER_PORT = 1883;
	public static int KEEP_ALIVE = 60;

	public static boolean IS_CLEAN_SESSION = true;
	public static boolean IS_SSL = false;

	/**
	 * derby数据库的目录
	 */
	public static String DERBY_DIR = "/data1/linsd/gateway-pf4j/db/mqtt/";
	//public static String DERBY_DIR="E:\\db\\mqtt";

	public static final void initConfig(String config_properties) throws Exception {

		FileInputStream in = null;
		try {
			in = new FileInputStream(config_properties);
			Reader reader = new InputStreamReader(in, Charset.forName("UTF-8"));
			Properties properties = new Properties();
			properties.load(reader);
			//CLIENT_ID = getProperties(properties, "mqtt.client.id");
			BROKER_URI = getProperties(properties, "mqtt.broker.uri");
			BROKER_HOST = getProperties(properties, "mqtt.broker.host");
			BROKER_PORT = StringTools.getInt(getProperties(properties, "mqtt.broker.port"));
			DERBY_DIR = getProperties(properties, "derby.db.path");

		} catch (Exception e) {
			logger.error("mqtt plugin config failed.", e);
			throw e;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.error("IOException:" + e);
				}
			}
		}
	}

	protected static final String getProperties(Properties prop, String key) {
		if (prop == null) {
			logger.error("config properties is null ");
			System.exit(-1);
		}
		if (prop.containsKey(key) == false) {
			logger.error(String.format("config.properties have no key \"%s\" !", key));
			return null;
		}
		return prop.getProperty(key);
	}
}

package cn.xlink.cmmqttclient.core.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


public class Utils {
	private static final Logger logger = LoggerFactory.getLogger(Utils.class);

	/**
	 * 操作符 << 的优先级比 & 高 intValue = (bytes[3] & 0xFF) << 24 | (bytes[2] & 0xFF)
	 * << 16 | (bytes[1] & 0xFF) << 8 | (bytes[0] & 0xFF) << 0
	 * 
	 * @param bytes
	 * @return
	 */
	public static int bytesToInt(byte[] bytes) {
		int length = 4;
		int intValue = 0;
		for (int i = length - 1; i >= 0; i--) {
			int offset = i * 8; // 24, 16, 8
			intValue |= ( bytes[i] & 0xFF ) << offset;
		}
		return intValue;
	}

	public static int byteToInt(byte b) {
		return (int) b;
	}

	public static String bytesToString(byte[] bytes, int start, int length) {
		ByteBuf bb = Unpooled.wrappedBuffer(bytes, start, length);
		return bb.toString(Charset.forName("UTF-8"));
	}

	public static Charset defaultStringCharset() {
		return Charset.forName("UTF-8");
	}

	public static void intTo2Bytes(int v, byte[] buf) {
		buf[1] = (byte) ( v & 0x000000FF );
		buf[0] = (byte) ( ( v & 0x0000FF00 ) >> 8 );
	}

	public static void intToBytes(int v, byte[] buf) {
		buf[3] = (byte) ( v & 0x000000FF );
		buf[2] = (byte) ( ( v & 0x0000FF00 ) >> 8 );
		buf[1] = (byte) ( ( v & 0x00FF0000 ) >> 16 );
		buf[0] = (byte) ( ( v & 0xFF000000 ) >> 24 );
	}

	public static String getBinString(byte aByte) {
		String out = "";
		int i = 0;
		for (i = 0; i < 8; i++) {
			int v = ( aByte << i ) & 0x80;
			v = ( v >> 7 ) & 1;
			out += v;
		}
		return out;
	}

	public static String getBinString(byte[] bytes) {
		String output = "";
		for (int i = 0; i < bytes.length; i++) {
			output += getBinString(bytes[i]);
			if (i != bytes.length - 1) {
				output += " ";
			}
		}
		return output;
	}

	
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	public static byte[] hexStringToBytes(String hexString) {   
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) ( charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]) );
		}
		return d;
	}

	/**
	 * Convert char to byte
	 * 
	 * @param c
	 *            char
	 * @return byte
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

		
	public static int getBitValue(byte anByte, int bitIndex) {
		int bitValue = anByte << ( 7 - bitIndex );
		bitValue = bitValue >> 7;
		bitValue &= 0x01;
		return bitValue;
	}

	public static int getValidBitMask(int bitIndex) {
		switch (bitIndex) {
		case 0:
			return Integer.parseInt("00000001", 2);
		case 1:
			return Integer.parseInt("00000010", 2);
		case 2:
			return Integer.parseInt("00000100", 2);
		case 3:
			return Integer.parseInt("00001000", 2);
		case 4:
			return Integer.parseInt("00010000", 2);
		case 5:
			return Integer.parseInt("00100000", 2);
		case 6:
			return Integer.parseInt("01000000", 2);
		case 7:
			return Integer.parseInt("10000000", 2);
		}

		return 0xFF;
	}

	public static int getUnvalidBitMask(int bitIndex) {
		switch (bitIndex) {
		case 0:
			return Integer.parseInt("11111110", 2);
		case 1:
			return Integer.parseInt("11111101", 2);
		case 2:
			return Integer.parseInt("11111011", 2);
		case 3:
			return Integer.parseInt("11110111", 2);
		case 4:
			return Integer.parseInt("11101111", 2);
		case 5:
			return Integer.parseInt("11011111", 2);
		case 6:
			return Integer.parseInt("10111111", 2);
		case 7:
			return Integer.parseInt("01111111", 2);
		}

		return 0xFF;
	}

	public static byte setBitValid(byte anByte, int bitIndex) {
		return (byte) ( (int) anByte | getValidBitMask(bitIndex) );
	}

	public static byte unsetBitValid(byte anByte, int bitIndex) {
		return (byte) ( (int) anByte & getUnvalidBitMask(bitIndex) );
	}

	public final static String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

		try {
			byte[] btInput = s.getBytes();
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			logger.error("Utils.MD5", e);
			return null;
		}
	}

	/**
	 * 获取现在时间
	 * 
	 * @return返回字符串格式 yyyy-MM-dd HH:mm:ss
	 */
	public static String getStringDate() {
		return getStringDate(System.currentTimeMillis());
	}

	public static String getStringDate(long time) {
		Date currentTime = new Date(time);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	public static String getStringDate(Date time) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		String dateString = formatter.format(time);
		return dateString;
	}

	public static Date getDate(String date) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		try {
			return formatter.parse(date);
		} catch (ParseException e) {
			logger.error("Utils.getDate", e);
		}
		return null;

	}

	public static String getStringDateShort() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	// FC3A2C

	public static byte[] hexStringToByteArray(String hexString) {
    if (hexString.isEmpty() /* || hexString.length() % 2 != 0 */)
			return null;

		hexString = hexString.toLowerCase();
		final byte[] byteArray = new byte[hexString.length() / 2];
		int k = 0;
		for (int i = 0; i < byteArray.length; i++) {
			// 因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
			byte high = (byte) ( Character.digit(hexString.charAt(k), 16) & 0xff );
			byte low = (byte) ( Character.digit(hexString.charAt(k + 1), 16) & 0xff );
			byteArray[i] = (byte) ( high << 4 | low );
			k += 2;
		}
		return byteArray;
	}

	/**
	 * BASE64解密
	 * 
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public static byte[] base64Decrypt(String key) throws IOException {
		return ( new BASE64Decoder() ).decodeBuffer(key);
	}

	/**
	 * BASE64加密
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String base64Encrypt(byte[] key) throws IOException {
		return ( new BASE64Encoder() ).encodeBuffer(key);
	}

	/**
	 * 填充JSON status
	 * 
	 * @param result
	 * @param code
	 */
//	public static void setReturnStatus(JSONObject result, int code) {
//		result.put("status", code);
//		result.put("msg", Code.getStatusMsg(code));
//	}

	public static long getUnsignedInt(int data) { 
		return data & 0x0FFFFFFFFl;
	}

	public static int getUnsignedByte(byte data) { 
		return data & 0x0FF;
	}

	public static int  getUnsignedShort(short data) {
		return data & 0x0FFFF;
	}
	
	
	public static byte[] funcDatapointEncodeV1(JSONArray datapoint){
		
		
		
		
		return null;
	}
	
	public static byte[] funcDatapointEncodeV2(JSONArray datapoint){
		return null;
	}
	
	
	
}
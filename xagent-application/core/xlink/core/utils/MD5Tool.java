package xlink.core.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Tool {

	/**
	 * MD5方法
	 * 
	 * @param string
	 * @return String
	 */
	public static String MD5(String string) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			return byteArrayToHexString(md.digest(string.getBytes()));
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}
	
	public static String MD5(byte[] bytes){
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			return byteArrayToHexString(md.digest(bytes));
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

	private static String byteArrayToHexString(byte[] bytes) {
		StringBuffer buf = new StringBuffer(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			if (((int) bytes[i] & 0xff) < 0x10) {
				buf.append("0");
			}
			buf.append(Long.toString((int) bytes[i] & 0xff, 16));
		}
		return buf.toString();
	}
}

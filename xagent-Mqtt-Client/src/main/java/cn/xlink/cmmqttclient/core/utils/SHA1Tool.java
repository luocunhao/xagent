package cn.xlink.cmmqttclient.core.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA1Tool {
	/**
	 * sha1加密
	 * 
	 * @param sourceString
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public final static String SHA1(String sourceString) throws NoSuchAlgorithmException {
		String resultString = null;
		try {
			resultString = new String(sourceString);
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			resultString = byte2hexString(md.digest(resultString.getBytes()));
		} catch (NoSuchAlgorithmException ex) {
			throw ex;
		}
		return resultString;
	}

	
	public final static String SHA256(String sourceString) throws NoSuchAlgorithmException {
		String resultString = null;
		try {
			resultString = new String(sourceString);
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			resultString = byte2hexString(md.digest(resultString.getBytes()));
		} catch (NoSuchAlgorithmException ex) {
			throw ex;
		}
		return resultString;
	}
	
	/**
	 * byte to hex
	 * 
	 * @param bytes
	 * @return
	 */
	private final static String byte2hexString(byte[] bytes) {
		StringBuffer buf = new StringBuffer(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			if (( (int) bytes[i] & 0xff ) < 0x10) {
				buf.append("0");
			}
			buf.append(Long.toString((int) bytes[i] & 0xff, 16));
		}
		return buf.toString().toUpperCase();
	}
}

package cn.xlink.cmmqttclient.core.utils;

public class ByteTools {
	// 以十六进制格式打印byte数组
	public static String printBytes2HexString(byte[] bytes) {
		StringBuffer buf = new StringBuffer(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			if (((int) bytes[i] & 0xff) < 0x10) {
				buf.append("0");
			}
      buf.append(Long.toString((int) bytes[i] & 0xff, 16));
		}
		return buf.toString();
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
}

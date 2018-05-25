package xlink.core.utils;

/**
 * 字符串工具类
 * 
 * @author shenweiran
 */
public final class StringTools {

	public static final String _blank = "";
	public static final String s_blank_regrex = "\\s*";
	public static final String en_blank_regrex = " +";
	public static final String cn_blank_regrex = "　+";
	
	private StringTools() {
	}

	public final static String getString(Object obj) {
		if (obj == null) {
			return "";
		}
		return obj.toString();
	}

	public final static long getLong(Object str) {
		if (str == null) {
			return 0;
		}
		return getLong(str.toString());
	}

	public final static int getInt(Object str) {
		if (str == null) {
			return 0;
		}
		return getInt(str.toString());
	}

	public final static float getFloat(Object str) {
		if (str == null) {
			return 0;
		}
		return getFloat(str.toString());
	}

	public final static double getDouble(Object str) {
		if (str == null) {
			return 0;
		}
		return getDouble(str.toString());
	}

	public final static boolean getBoolean(Object str) {
		if (str == null) {
			return false;
		}
		return getBoolean(str.toString());
	}

	public final static short getShort(Object str) {
		return getShort(str.toString());
	}

	public final static long getLong(String str) {
		return (long) getDouble(str);
	}

	public final static int getInt(String str) {
		return (int) getDouble(str);
	}

	public final static float getFloat(String str) {
		return (float) getDouble(str);
	}

	public final static double getDouble(String str) {
		try {
			return Double.parseDouble(str);
		} catch (Exception e) {
			return 0;
		}
	}

	public final static boolean getBoolean(String str) {
		return Boolean.valueOf(str);
	}

	public final static short getShort(String str) {
		return Short.parseShort(str);
	}

	public final static boolean isEmpty(String str) {
		return (null == str) ? true : ((str.trim().length() == 0) ? true : false);
	}

	/**
	 * 判断两个字符串是否相等，兼容比较字符为null的情况。
	 * 
	 * @author linsida
	 * @date 2016年8月4日 下午2:29:55
	 */
	public final static boolean equals(String str1, String str2) {
		return (str1 == null) ? (str2 == null) : ((str2 == null) ? false : str1.equals(str2));
	}
	
	/**
	 * 空白
	 * @param str
	 * @return
	 */
	public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }
	
	public final static String removeBlank(String expression) {
		if(null != expression){
			expression = expression.replaceAll(en_blank_regrex, _blank);
			expression = expression.replaceAll(cn_blank_regrex, _blank);
			expression = expression.replaceAll(s_blank_regrex, _blank);
		}
		return expression;
	}


  public final static byte getByte(String str) {
    return (byte) getDouble(str);
  }

  public final static byte getByte(Object str) {
    if (str == null) {
      return 0;
    }
    return getByte(str.toString());
  }

  public final static byte[] getbytesFromIpString(String ip) {
    if (isEmpty(ip) == false) {
      String[] ipbyteStr = ip.split("\\.");
      if (ipbyteStr.length == 4) {
        byte[] ipBytes = new byte[4];
        ipBytes[0] = getByte(ipbyteStr[0]);
        ipBytes[1] = getByte(ipbyteStr[1]);
        ipBytes[2] = getByte(ipbyteStr[2]);
        ipBytes[3] = getByte(ipbyteStr[3]);
        return ipBytes;
      }
    }
    return null;
  }
}

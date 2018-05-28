package xlink.xagent.ptp.zr.utils;

import java.math.BigDecimal;
import java.util.Stack;

public class DataTransUtils {
    private static char[] array = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
            .toCharArray();
    private static String numStr = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String _10_to_N(long number, int N) {
        long rest = number;
        Stack<Character> stack = new Stack<>();
        StringBuilder result = new StringBuilder(0);
        while (rest != 0) {
            stack.add(array[new Long((rest % N)).intValue()]);
            rest = rest / N;
        }
        for (; !stack.isEmpty(); ) {
            result.append(stack.pop());
        }
        return result.length() == 0 ? "0" : result.toString();
    }
    // 其他进制转为10进制，按权展开
    public static long N_to_10(String number, int N) {
        char ch[] = number.toCharArray();
        int len = ch.length;
        long result = 0;
        if (N == 10) {
            return Long.parseLong(number);
        }
        long base = 1;
        for (int i = len - 1; i >= 0; i--) {
            int index = numStr.indexOf(ch[i]);
            result += index * base;
            base *= N;
        }
        return result;
    }
    public static int HighAndLow(Short high,Short low){
        int H = high<<16;
        return H+low;
    }
    /**
     * float 精确度转换
     * */
    public static float round(float value,int scale,int roundingModel){
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(scale,roundingModel);
        float f = bd.floatValue();
        bd = null;
        return f;
    }
    /**
     * 时间秒转小时
     * */
    public static float seconds2hour(int seconds){
        float f = seconds/3600f;
        float h = round(f,3,BigDecimal.ROUND_HALF_UP);
        return h;
    }
    public static void main(String[] args) {
        // TODO Auto-generated method stub
       // System.out.println(HighAndLow((short)124,(short)419));
        System.out.println(seconds2hour(121));
    }

}

package mytest.algorithm.string;

import java.util.ArrayList;
import java.util.List;

/**
 * //    题目：String的二进制求和运算。要求将输入的两个String中的二进制数据进行求和，并依然用String的方式输出。
 * //    例如：输入String是“101”和“1”，则输出String是“110”
 * //    可以假定输入必然合法，也无需做空字串或者其他异常判断
 */
public class BinStringAdd {
    public static void main(String[] args) {
        System.out.println(binStringAdd("1110", "111"));
    }


    public static String binStringAdd(String s1, String s2) {
        char[] s1Chars = s1.toCharArray();
        char[] s2Chars = s2.toCharArray();
        List<Character> resultChars = new ArrayList<>();
        int len = Math.max(s1.length(), s2.length());
        // 是否有进位
        int pre = 0;
        for (int i = 0; i < len; i++) {
            int s1Index = s1.length() - i;
            int s2Index = s2.length() - i;

            // 如果小于0，说明最高位已经结束了，补0去计算 例如1110  和 0111，让两个长度一致
            char s1Char = s1Index <= 0 ? '0' : s1Chars[s1Index - 1];
            char s2Char = s2Index <= 0 ? '0' : s2Chars[s2Index - 1];
            // 计算当前位置的最终值 = 当前位置之和 + pre 进位
            int cur = 0;
            if (s1Char == '1' && s2Char == '1') {
                // 之前有进位，cur此时等于 1
                if (pre == 1) {
                    cur = 1;
                }
                // 都为1时，肯定进一位
                pre = 1;
            } else if (s1Char == '0' && s2Char == '0') {
                if (pre == 1) {
                    cur = 1;
                }
                pre = 0;
            } else {
                // 1 + 1 =0 ; pre =1
                if (pre == 1) {
                    cur = 0;
                    pre = 1;
                } else { // 1+ 0 =1; pre = 0
                    cur = 1;
                    pre = 0;
                }
            }

            resultChars.add(cur == 0 ? '0' : '1');
        }

        // 如果仍有进位，需要补上
        if (pre == 1) {
            resultChars.add('1');
        }

        StringBuilder sb = new StringBuilder();
        // resultChars是从个位开始的，需要反向输出
        for (int i = resultChars.size() - 1; i >= 0; i--) {
            sb.append(resultChars.get(i));
        }


        return sb.toString();
    }
}

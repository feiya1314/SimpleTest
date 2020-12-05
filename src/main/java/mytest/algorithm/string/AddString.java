package mytest.algorithm.string;

/**
 * 给定两个字符串形式的非负整数 num1 和num2 ，计算它们的和。
 *
 *  
 *
 * 提示：
 *
 * num1 和num2 的长度都小于 5100
 * num1 和num2 都只包含数字 0-9
 * num1 和num2 都不包含任何前导零
 * 你不能使用任何內建 BigInteger 库， 也不能直接将输入的字符串转换为整数形式
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/add-strings
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 * @description :
 */
public class AddString {
    public static String addStrings(String num1, String num2) {
        if ("0".equals(num1)) {
            return num2;
        } else if ("0".equals(num2)) {
            return num1;
        }

        StringBuilder sb = new StringBuilder();
        char[] num1Char = num1.toCharArray();
        char[] num2Char = num2.toCharArray();

        int num1CharLen = num1Char.length;
        int num2CharLen = num2Char.length;

        int maxLen = Math.max(num1CharLen, num2CharLen);
        int i = num1CharLen - 1;
        int j = num2CharLen - 1;

        int add = 0;
        for (int k = 0; k < maxLen; k++) {
            int v1 = i < 0 ? 0 : Character.getNumericValue(num1Char[i]);
            int v2 = j < 0 ? 0 : Character.getNumericValue(num2Char[j]);
            int result = v1 + v2 + add;
            sb.append(result % 10);
            add = result >= 10 ? 1 : 0;
            i--;
            j--;
        }
        if (add > 0) {
            sb.append(add);
        }
        sb.reverse();
        return sb.toString();
    }

    public static String addStrings2(String num1, String num2) {
        if ("0".equals(num1)) {
            return num2;
        } else if ("0".equals(num2)) {
            return num1;
        }

        StringBuilder sb = new StringBuilder();
        char[] num1Char = num1.toCharArray();
        char[] num2Char = num2.toCharArray();

        int i = num1Char.length - 1;
        int j = num2Char.length - 1;
        int add = 0;
        while (i >= 0 || j >= 0 || add > 0) {
            int v1 = i < 0 ? 0 : Character.getNumericValue(num1Char[i]);
            int v2 = j < 0 ? 0 : Character.getNumericValue(num2Char[j]);
            int result = v1 + v2 + add;
            sb.append(result % 10);
            add = result / 10;
            i--;
            j--;
        }
        return sb.reverse().toString();
    }
}

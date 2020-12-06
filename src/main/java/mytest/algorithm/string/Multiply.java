package mytest.algorithm.string;

/**
 * 示例 1:
 * <p>
 * 输入: num1 = "2", num2 = "3"
 * 输出: "6"
 * 示例 2:
 * <p>
 * 输入: num1 = "123",
 * num2 = "456"
 * 输出: "56088"
 * 说明：
 * <p>
 * num1 和 num2 的长度小于110。
 * num1 和 num2 只包含数字 0-9。
 * num1 和 num2 均不以零开头，除非是数字 0 本身。
 * 不能使用任何标准库的大数类型（比如 BigInteger）或直接将输入转换为整数来处理。
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/multiply-strings
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class Multiply {
    public static void main(String[] args) {
        System.out.println(multiply2("456", "123"));
    }

    /**
     * 每次计算num2 的每一位和num1的所有相乘，结果和前一次相加，
     *
     * @param num1
     * @param num2
     * @return
     */
    public static String multiply(String num1, String num2) {
        if ("0".equals(num1) || "0".equals(num2)) {
            return "0";
        }

        char[] num1Char = num1.toCharArray();
        char[] num2Char = num2.toCharArray();
        //int i = num1Char.length-1;
        //int j = num2Char.length-1;
        String lastResult = "0";
        for (int i = num1Char.length - 1; i >= 0; i--) {
            int v1 = Character.getNumericValue(num1Char[i]);
            StringBuilder sb = new StringBuilder();
            for (int k = 0; k < num1Char.length - 1 - i; k++) {
                sb.append("0");
            }

            int add = 0;
            for (int j = num2Char.length - 1; j >= 0; j--) {
                int v2 = Character.getNumericValue(num2Char[j]);
                int result = v1 * v2 + add;
                sb.append(result % 10);
                add = result / 10;
            }
            if (add > 0) {
                sb.append(add);
            }
            lastResult = AddString.addStrings2(lastResult, sb.reverse().toString());
        }

        return lastResult;
    }

    /**
     * @param num1
     * @param num2
     * @return
     */
    public static String multiply2(String num1, String num2) {
        if ("0".equals(num1) || "0".equals(num2)) {
            return "0";
        }
        char[] num1Char = num1.toCharArray();
        char[] num2Char = num2.toCharArray();

        int[] result = new int[num1Char.length + num2Char.length];

        // 每次乘得的结果位置刚好位于 result[i+j] 和 result[i+j+1]
        for (int i = num1Char.length - 1; i >= 0; i--) {
            int v1 = Character.getNumericValue(num1Char[i]);
            for (int j = num2Char.length - 1; j >= 0; j--) {
                int v2 = Character.getNumericValue(num2Char[j]);
                int mu = v1 * v2;
                int temp1 = mu % 10 + result[i + j + 1];
                result[i + j + 1] = temp1 % 10;
                result[i + j] = mu / 10 + temp1 / 10 + result[i + j];
            }
        }

        int index = 0;
        while (result[index] == 0) {
            index++;
        }
        StringBuilder sb = new StringBuilder();
        for (; index < result.length; index++) {
            sb.append(result[index]);
        }

        return sb.toString();
    }
}

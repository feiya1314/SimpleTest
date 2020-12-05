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
        System.out.println(multiply("456", "123"));
    }

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
}

package mytest.algorithm.string;

import java.math.BigInteger;

/**
 * 累加数 (LeetCode 306)
 * 
 * 判断一个字符串是否是累加数（Additive Number）
 * 累加序列：每个数是前两个数之和
 */
public class AdditiveNumber {

    /**
     * 方法1：DFS 回溯（推荐）
     * 
     * 思路：
     * 1. 枚举第一个数和第二个数的所有可能分割
     * 2. 从第三个开始，验证是否符合 num1 + num2 = num3
     * 3. 剪枝：数字不能以 0 开头（除非是单独的 "0"）
     */
    public boolean isAdditiveNumber(String num) {
        int n = num.length();
        
        // 枚举第二个数的起始位置（至少需要3个数，所以从1开始）
        for (int i = 1; i <= n - 2; i++) {
            // 枚举第二个数的结束位置
            for (int j = i + 1; j <= n - 1; j++) {
                if (dfs(num, 0, i, j)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean dfs(String num, int start1, int end1, int start2) {
        String num1 = num.substring(start1, end1);
        String num2 = num.substring(start2, num.length());

        // 剪枝：不能有前导零（除非是 "0" 本身）
        if ((num1.length() > 1 && num1.startsWith("0")) 
            || (num2.length() > 1 && num2.startsWith("0"))) {
            return false;
        }

        // 从第三个数字开始验证
        int start3 = start2 + num2.length();
        
        // 如果已经到末尾，说明前两个数就能构成序列（但需要至少3个数）
        if (start3 >= num.length()) {
            return start3 == num.length() && (start2 - end1) > 0;
        }

        // 计算 expected = num1 + num2
        BigInteger b1 = new BigInteger(num1);
        BigInteger b2 = new BigInteger(num2);
        BigInteger sum = b1.add(b2);
        String expected = sum.toString();

        // 检查从 start3 开始的子串是否以 expected 开头
        if (!num.substring(start3).startsWith(expected)) {
            return false;
        }

        // 递归验证后续
        return dfs(num, end1, start2, start3 + expected.length());
    }

    /**
     * 方法2：迭代法
     * 
     * 思路类似，但使用迭代而不是递归
     */
    public boolean isAdditiveNumber2(String num) {
        int n = num.length();

        for (int i = 1; i <= n - 2; i++) {
            for (int j = i + 1; j <= n - 1; j++) {
                if (check(num, i, j)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean check(String num, int i, int j) {
        if ((num.charAt(0) == '0' && i > 1) 
            || (num.charAt(i) == '0' && j - i > 1)) {
            return false;
        }

        int start1 = 0, end1 = i;
        int start2 = i, end2 = j;

        while (end2 < num.length()) {
            String s1 = num.substring(start1, end1);
            String s2 = num.substring(start2, end2);

            // 计算和
            BigInteger n1 = new BigInteger(s1);
            BigInteger n2 = new BigInteger(s2);
            String sum = n1.add(n2).toString();

            // 检查后续是否匹配
            int nextStart = end2;
            int nextEnd = nextStart + sum.length();
            
            if (nextEnd > num.length()) {
                break;
            }
            
            if (!num.substring(nextStart, nextEnd).equals(sum)) {
                break;
            }

            // 匹配成功，继续下一步
            if (nextEnd == num.length()) {
                return true;
            }

            start1 = start2;
            end1 = end2;
            start2 = nextStart;
            end2 = nextEnd;
        }

        return false;
    }
}

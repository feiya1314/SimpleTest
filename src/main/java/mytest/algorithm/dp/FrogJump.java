package mytest.algorithm.dp;

/**
 * 假设你正在爬楼梯。需要 n 阶你才能到达楼顶。
 * <p>
 * 每次你可以爬 1 或 2 个台阶。你有多少种不同的方法可以爬到楼顶呢？
 * <p>
 * 注意：给定 n 是一个正整数。
 * <p>
 * 示例 1：
 * <p>
 * 输入： 2
 * 输出： 2
 * 解释： 有两种方法可以爬到楼顶。
 * 1.  1 阶 + 1 阶
 * 2.  2 阶
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/climbing-stairs
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class FrogJump {

    public int climbStairsDP(int n) {
        if (n < 3) {
            return n;
        }
        int[] dp = new int[n + 1];
        dp[0] = 0;
        dp[1] = 1;
        dp[2] = 2;
        for (int i = 3; i < n + 1; i++) {
            dp[i] = dp[i - 1] + dp[i - 2]; // 最后一步可能跨了一级台阶，也可能跨了两级台阶,所以可能的方法是 dp[i] = dp[i - 1] + dp[i - 2]
        }

        return dp[n];
    }

    public int climbStairs(int n) {
        if (n < 3) {
            return n;
        }

        int left1 = 1;  // 针对dp优化，只需要前两个状态，没必要记录所有的状态
        int left2 = 2;
        int cur = 0;

        for (int i = 3; i < n + 1; i++) {
            cur = left1 + left2; // 最后一步可能跨了一级台阶，也可能跨了两级台阶,所以可能的方法是 dp[i] = dp[i - 1] + dp[i - 2]
            left1 = left2;
            left2 = cur;
        }

        return cur;
    }
}

package mytest.algorithm.dp;

/**
 * 你有三种硬币，分别面值2元，5元和7元，
 * 每种硬币都有足够多。买一本书需要27元。
 * 如何用最少的硬币组合正好付清，不需要对方找钱？
 * 要找 27 的组合，必然包含三种情况，包含一枚2元的 、包含一枚5元的 、包含一枚7元的
 * 也即如下
 * f(27) = f(27-2) + 1 = f(25) +1
 * f(27) = f(27-5) + 1 = f(22) +1
 * f(27) = f(27-7) + 1 = f(20) +1
 * <p>
 * 只需要找出f(25) f(22) f(20)三者之中最小的
 * f(x) = min{f(x-2)+1, f(x-5)+1, f(x-7)+1}
 */
public class CoinChange {
    public static void main(String[] args) {
        for (int i = 0; i <=27; i++) {
            System.out.println(exec(i));
        }
    }

    public static int exec(int price) {
        // 边界情况，当 price 为 0 时，此时返回 0
        if (price == 0) {
            return 0;
        }
        // dp 记录 price 之前的每种状态，也即是 0 ~ price 时的最少的硬币组合
        // 也即是 dp 中存放的就是每种情况的结果
        int[] dp = new int[price + 1];

        // 初始化边界
        dp[0] = 0;

        for (int i = 1; i <= price; i++) {
            // 如果 i 的前面的坐标小于 0 则用 int 最大值，如果前面的历史情况 值已经是 int 最大值，说明无法组成要求的价格，那么 i 也就无法组成
            // 最后如果能组成，就用历史值+1
            int v1 = (i - 2) < 0 ? Integer.MAX_VALUE : (dp[i - 2] == Integer.MAX_VALUE ? Integer.MAX_VALUE : (dp[i - 2] + 1));
            int v2 = (i - 5) < 0 ? Integer.MAX_VALUE : (dp[i - 5] == Integer.MAX_VALUE ? Integer.MAX_VALUE : (dp[i - 5] + 1));
            int v3 = (i - 7) < 0 ? Integer.MAX_VALUE : (dp[i - 7] == Integer.MAX_VALUE ? Integer.MAX_VALUE : (dp[i - 7] + 1));

            dp[i] = Math.min(Math.min(v1, v2), v3);
        }
        // 最终的结果就是下面
        return dp[price];
    }
}

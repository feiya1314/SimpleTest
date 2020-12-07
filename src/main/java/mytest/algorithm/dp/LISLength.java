package mytest.algorithm.dp;

/**
 * 给定一个无序的整数数组，找到其中最长上升子序列的长度。
 * <p>
 * 示例:
 * <p>
 * 输入: [10,9,2,5,3,7,101,18]
 * 输出: 4
 * 解释: 最长的上升子序列是 [2,3,7,101]，它的长度是 4。
 * 说明:
 * <p>
 * 可能会有多种最长上升子序列的组合，你只需要输出对应的长度即可。
 * 你算法的时间复杂度应该为 O(n2) 。
 * 进阶: 你能将算法的时间复杂度降低到 O(n log n) 吗?
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/longest-increasing-subsequence
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class LISLength {
    public static void main(String[] args) {

    }

    // 时间复杂度 n^2  空间复杂度 n
    // 思路，动态规划，dp[0] =1 ,dp[1] =1 ,dp[2] =1,dp[3] =2,dp[4] =2,dp[5] =1
    // 只要 arr[i] 大于 arr[j],那么 dp[i] 最小要等于 dp[j]+1;
    public static int lengthOfLIS(int[] nums) {
        if (nums.length == 0) {
            return 0;
        }
        int[] dp = new int[nums.length]; // 记录每个 位置 的最长上升子序列
        dp[0] = 1;
        int maxans = 1;

        for (int i = 1; i < dp.length; i++) {
            int maxval = 0;
            for (int j = 0; j < i; j++) {
                if (nums[i] > nums[j]) {  // 如果 nums[i] <= nums[j] 那么 dp[i] 肯定不会比 dp[j] 大
                    maxval = Math.max(maxval, dp[j]);
                }
            }
            dp[i] = maxval + 1; // 前面只是取的 dp[j] 的最大值，但是没有 +1，遍历结束后加上
            maxans = Math.max(maxans, dp[i]); // 和 dp[i] 之前的比较，取最大值
        }
        return maxans;
    }

    // 方法二，找长度相同，但是结尾数更小的上升序列
    // https://leetcode-cn.com/problems/longest-increasing-subsequence/solution/dong-tai-gui-hua-er-fen-cha-zhao-tan-xin-suan-fa-p/
}

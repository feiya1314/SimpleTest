package mytest.algorithm.doublepoint;

/**
 * //给定一个含有 n 个正整数的数组和一个正整数 s ，找出该数组中满足其和 ≥ s 的长度最小的 连续 子数组，并返回其长度。如果不存在符合条件的子数组，返回
 * // 0。
 * //
 * //
 * //
 * // 示例：
 * //
 * // 输入：s = 7, nums = [2,3,1,2,4,3]
 * //输出：2
 * //解释：子数组 [4,3] 是该条件下的长度最小的子数组。
 * // 进阶：
 * //
 * //
 * // 如果你已经完成了 O(n) 时间复杂度的解法, 请尝试 O(n log n) 时间复杂度的解法。
 * //
 * // Related Topics 数组 双指针 二分查找
 * https://leetcode-cn.com/problems/minimum-size-subarray-sum/
 *
 * @author : yufei
 * @date : 2021/1/21 14:32
 * @description :
 */
public class MinSubArrayLen {
    // 双指针，当子数组长度>=s时，移动左指针，直到小于s，再移动右指针
    public int minSubArrayLen(int s, int[] nums) {
        if (nums == null || nums.length < 1) {
            return 0;
        }
        int min = Integer.MAX_VALUE;
        int left = 0;
        int right = 0;
        int sum = 0;
        while (right < nums.length) {
            sum += nums[right];
            // left右移，直到sum<s时，右移right，当right到结束时，说明前面left已经移动到小于sum的位置了，再右移只会更小
            while (sum >= s) {
                min = Math.min(min, right - left + 1);
                if (min == 1) {
                    return min;
                }
                sum -= nums[left];
                left++;
            }
            right++;
        }
        return min == Integer.MAX_VALUE ? 0 : min;
    }

    // 暴力双指针，固定左指针，向右移动，直到大于s，更新最小数组长度
    public int minSubArrayLen2(int s, int[] nums) {
        if (nums == null || nums.length < 1) {
            return 0;
        }
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] >= s) {
                return 1;
            }
            int sum = nums[i];
            int j = i + 1;
            while (sum < s && j < nums.length) {
                sum += nums[j];
                j++;
            }
            if (sum >= s) {
                min = Math.min(min, j - i);
            }
        }
        if (min > nums.length) {
            return 0;
        }
        return min;
    }
}

package mytest.algorithm.slidingwindow;

import java.util.Arrays;
import java.util.PriorityQueue;

/**
 * //给你一个整数数组 nums，有一个大小为 k 的滑动窗口从数组的最左侧移动到数组的最右侧。你只可以看到在滑动窗口内的 k 个数字。滑动窗口每次只向右移动一位
 * //。
 * //
 * // 返回滑动窗口中的最大值。
 * //
 * //
 * //
 * // 示例 1：
 * //
 * //
 * //输入：nums = [1,3,-1,-3,5,3,6,7], k = 3
 * //输出：[3,3,5,5,6,7]
 * //解释：
 * //滑动窗口的位置                最大值
 * //---------------               -----
 * //[1  3  -1] -3  5  3  6  7       3
 * // 1 [3  -1  -3] 5  3  6  7       3
 * // 1  3 [-1  -3  5] 3  6  7       5
 * // 1  3  -1 [-3  5  3] 6  7       5
 * // 1  3  -1  -3 [5  3  6] 7       6
 * // 1  3  -1  -3  5 [3  6  7]      7
 * //
 * //
 * // 示例 2：
 * //
 * //
 * //输入：nums = [1], k = 1
 * //输出：[1]
 * //
 * //
 * // 示例 3：
 * //
 * //
 * //输入：nums = [1,-1], k = 1
 * //输出：[1,-1]
 * //
 * //
 * // 示例 4：
 * //
 * //
 * //输入：nums = [9,11], k = 2
 * //输出：[11]
 * //
 * //
 * // 示例 5：
 * //
 * //
 * //输入：nums = [4,-2], k = 2
 * //输出：[4]
 * // 提示：
 * // 1 <= nums.length <= 105
 * // -104 <= nums[i] <= 104
 * // 1 <= k <= nums.length
 * 堆 Sliding Window
 * https://leetcode-cn.com/problems/sliding-window-maximum/
 *
 * @author : yufei
 * @date : 2021/1/22 14:54
 * @description :
 */
public class MaxSlidingWindow {

    // 使用堆结构
    public int[] maxSlidingWindow(int[] nums, int k) {
        PriorityQueue<Integer> queue = new PriorityQueue<>(k);
        return new int[0];
        //todo
    }

    //暴力解法 超时
    public int[] maxSlidingWindow2(int[] nums, int k) {
        if (nums == null || nums.length < 1) {
            return nums;
        }

        int[] result = new int[nums.length - k + 1];
        int max = Integer.MIN_VALUE;
        int left = 0;
        int right = k - 1;
        int i = 0;
        while (right < nums.length) {
            int j = left;
            while (j <= right) {
                max = Math.max(max, nums[j]);
                j++;
            }
            result[i++] = max;
            left++;
            right++;
            max = Integer.MIN_VALUE;
        }

        return result;
    }
}
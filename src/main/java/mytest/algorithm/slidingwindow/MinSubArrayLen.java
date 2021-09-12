package mytest.algorithm.slidingwindow;

/**
 * 给定一个含有n个正整数的数组和一个正整数 target 。
 * <p>
 * 找出该数组中满足其和 ≥ target 的长度最小的 连续子数组[numsl, numsl+1, ..., numsr-1, numsr] ，并返回其长度。如果不存在符合条件的子数组，返回 0 。
 * <p>
 *
 * <p>
 * 示例 1：
 * <p>
 * 输入：target = 7, nums = [2,3,1,2,4,3]
 * 输出：2
 * 解释：子数组[4,3]是该条件下的长度最小的子数组。
 * 示例 2：
 * <p>
 * 输入：target = 4, nums = [1,4,4]
 * 输出：1
 * 示例 3：
 * <p>
 * 输入：target = 11, nums = [1,1,1,1,1,1,1,1]
 * 输出：0
 *
 * <p>
 * 提示：
 * <p>
 * 1 <= target <= 109
 * 1 <= nums.length <= 105
 * 1 <= nums[i] <= 105
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/2VG8Kg
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author : feiya
 * @date : 2021/9/12
 * @description :
 */
public class MinSubArrayLen {
    /**
     * 双指针，滑动窗口，右指针移动并相加，直到和大于target，移动左指针，同时减区出去的值，
     * @param target
     * @param nums
     * @return
     */
    public int minSubArrayLen(int target, int[] nums) {
        int left = 0;
        int right = 0;
        int subLen = Integer.MAX_VALUE;

        int sum = nums[left];
        while (left <= right && right < nums.length) {
            // 和小于target，则移动右指针
            if (sum < target) {
                right++;
                // 超出数组长度，说明结束，否则把右边的值加上
                if (right < nums.length) {
                    sum += nums[right];
                }
            } else {
                subLen = Math.min(subLen, right - left + 1);
                if (subLen == 1) {
                    return subLen;
                }
                // 更新sum，并移动左指针
                sum = sum - nums[left];
                left++;
            }
        }
        if (subLen == Integer.MAX_VALUE) {
            return 0;
        }
        return subLen;
    }
}
